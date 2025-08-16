/*
Copyright 2025 zeront4e (https://github.com/zeront4e)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.github.zeront4e.c4j;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

class DownloaderReleaseMonitoringProjectUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloaderReleaseMonitoringProjectUtil.class);

    public record DownloadConfiguration(
            String gitHubOrganization,
            String gitHubRepository,
            String gitHubReleaseAssetName,

            String zipFilePrefix,
            String zipFileSuffix,

            String windowsX64ArchitectureProperty,
            String windowsX86ArchitectureProperty,
            String windowsArm64ArchitectureProperty,
            String windowsArm32ArchitectureProperty,
            String linuxX64ArchitectureProperty,
            String linuxX86ArchitectureProperty,
            String linuxArm32ArchitectureProperty,
            String linuxArm64ArchitectureProperty
    ) {
        //Ignore...
    }

    /**
     * Downloads and extracts the latest Chromium build for the given configuration and returns the final
     * browser-directory.
     * @param downloadConfiguration The download configuration.
     * @param c4jOsChromiumDistribution The Chromium distribution.
     * @param deleteDownloadedFile If true, the downloaded file will be deleted after extraction.
     * @param downloadDirectoryPath The directory where the downloaded file will be saved.
     * @param c4jOsArchitecture The OS architecture.
     * @param properties The properties containing the download URLs (overwrites of the default URLs).
     * @return The browser-directory.
     * @throws Exception An unexpected exception.
     */
    public static File downloadChromiumOrFail(DownloadConfiguration downloadConfiguration,
                                              C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                              boolean deleteDownloadedFile, Path downloadDirectoryPath,
                                              C4jOsArchitecture c4jOsArchitecture,
                                              Properties properties) throws Exception {
        //Obtain JSON data for the latest release.

        byte[] releaseJsonBytes =
                DownloaderGitHubAssetUtil.downloadAssetFileOrFail(downloadConfiguration.gitHubOrganization(),
                downloadConfiguration.gitHubRepository(), downloadConfiguration.gitHubReleaseAssetName);

        String releaseJsonString = new String(releaseJsonBytes, StandardCharsets.UTF_8);

        JsonElement jsonElement = JsonParser.parseString(releaseJsonString);

        if(!jsonElement.isJsonObject())
            throw new Exception("The given JSON is not a valid JSON object.");

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String obtainDownloadUrl = getDownloadUrlOrNull(downloadConfiguration, c4jOsArchitecture, properties,
                jsonObject);

        if (obtainDownloadUrl == null) {
            String infoString = C4jOsDetectionUtil.getOsArchitectureInfo().getInfoString();

            LOGGER.error("Unsupported OS: {}", infoString);

            throw new Exception("The given OS \"" + infoString + "\" is unsupported.");
        }

        Path extractionDir = downloadDirectoryPath.resolve(c4jOsChromiumDistribution.getId() + "/" +
                c4jOsArchitecture.name());

        Files.createDirectories(downloadDirectoryPath);
        Files.createDirectories(extractionDir);

        String zipFileName = downloadConfiguration.zipFilePrefix() + System.currentTimeMillis() +
                downloadConfiguration.zipFileSuffix();

        Path zipFilePath = extractionDir.resolve(zipFileName);

        downloadFileOrFail(obtainDownloadUrl, zipFilePath);

        extractZipOrFail(zipFilePath, extractionDir);

        LOGGER.info("Chromium downloaded and extracted successfully to: {}", extractionDir);

        if(deleteDownloadedFile) {
            LOGGER.info("Try to delete downloaded file.");

            try {
                FilesDeletionUtil.deleteFileOrFail(zipFilePath.toFile());

                LOGGER.info("The downloaded file was deleted.");
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to delete downloaded file.", exception);
            }
        }

        return extractionDir.toFile();
    }

    private static String getDownloadUrlOrNull(DownloadConfiguration downloadConfiguration,
                                               C4jOsArchitecture osArchitecture, Properties properties,
                                               JsonObject jsonObject) {
        try {
            return switch (osArchitecture) {
                case WINDOWS_X64 -> properties.contains(downloadConfiguration.windowsX64ArchitectureProperty()) ?
                        properties.getProperty(downloadConfiguration.windowsX64ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "windows", "x64");
                case WINDOWS_X86 -> properties.contains(downloadConfiguration.windowsX86ArchitectureProperty()) ?
                        properties.getProperty(downloadConfiguration.windowsX86ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "windows", "x86");
                case WINDOWS_ARM64 -> properties.contains(downloadConfiguration.windowsArm64ArchitectureProperty()) ?
                        properties.getProperty(downloadConfiguration.windowsArm64ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "windows", "arm64");
                case WINDOWS_ARM32 -> properties.contains(downloadConfiguration.windowsArm32ArchitectureProperty()) ?
                        properties.getProperty(downloadConfiguration.windowsArm32ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "windows", "arm32");
                case LINUX_X64 -> properties.contains(downloadConfiguration.linuxX64ArchitectureProperty()) ?
                        properties.getProperty(downloadConfiguration.linuxX64ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "linux", "x64");
                case LINUX_X86 -> properties.contains(downloadConfiguration.linuxX86ArchitectureProperty()) ?
                        properties.getProperty(downloadConfiguration.linuxX86ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "linux", "x86");
                case LINUX_ARM32 -> properties.contains(downloadConfiguration.linuxArm32ArchitectureProperty()) ?
                        properties.getProperty(downloadConfiguration.linuxArm32ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "linux", "arm32");
                case LINUX_ARM64 -> properties.contains(downloadConfiguration.linuxArm64ArchitectureProperty())  ?
                        properties.getProperty(downloadConfiguration.linuxArm64ArchitectureProperty()) :
                        extractDownloadUrlOrFail(jsonObject, "linux", "arm64");
                default -> null;
            };
        }
        catch (Exception exception) {
            LOGGER.warn("Unable to obtain download URL. Return null.", exception);

            return null;
        }
    }

    private static String extractDownloadUrlOrFail(JsonObject jsonObject, String platform, String architecture) {
        return jsonObject.get("downloads").getAsJsonObject()
                .get(platform).getAsJsonObject()
                .get(architecture).getAsJsonObject()
                .get("zip").getAsString();
    }

    private static void downloadFileOrFail(String fileUrl, Path destinationPath) throws Exception {
        LOGGER.info("Try to download Chromium browser from URL: {}", fileUrl);

        long time = System.currentTimeMillis();

        FilesDownloadUtil.downloadFileOrFail(fileUrl, destinationPath.toFile());

        time = System.currentTimeMillis() - time;

        LOGGER.info("Downloaded file in {}ms.", time);
    }

    private static void extractZipOrFail(Path zipFilePath, Path outputDirectoryPath) throws IOException {
        LOGGER.info("Try to extract downloaded ZIP file \"{}\" to \"{}\".", zipFilePath.toString(),
                outputDirectoryPath.toString());

        long time = System.currentTimeMillis();

        ZipUtil.unzip(zipFilePath.toFile(), outputDirectoryPath.toFile());

        time = System.currentTimeMillis() - time;

        LOGGER.info("Extracted ZIP file in {}ms.", time);
    }
}

