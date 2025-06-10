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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

class LatestTrunkChromiumDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LatestTrunkChromiumDownloader.class);

    public static final String WINDOWS_X86_ARCHITECTURE_PROPERTY = "chromium4j.download-url.latest-trunk.windows_x86";
    public static final String WINDOWS_X64_ARCHITECTURE_PROPERTY = "chromium4j.download-url.latest-trunk.windows_x64";

    public static final String LINUX_X86_ARCHITECTURE_PROPERTY = "chromium4j.download-url.latest-trunk.linux_x86";
    public static final String LINUX_X64_ARCHITECTURE_PROPERTY = "chromium4j.download-url.latest-trunk.linux_x64";

    public static final String DEFAULT_WINDOWS_X86_URL =
            "https://download-chromium.appspot.com/dl/Win";

    public static final String DEFAULT_WINDOWS_X64_URL =
            "https://download-chromium.appspot.com/dl/Win_x64";

    public static final String DEFAULT_LINUX_X86_URL =
            "https://download-chromium.appspot.com/dl/Linux";

    public static final String DEFAULT_LINUX_X64_URL =
            "https://download-chromium.appspot.com/dl/Linux_x64";

    private static final String ZIP_FILE_PREFIX = "chromium-trunk";
    private static final String ZIP_FILE_SUFFIX = ".zip";

    /**
     * Downloads and extracts the latest Chromium build for the given OS architecture and returns the final
     * browser-directory.
     * @param c4jOsChromiumDistribution The Chromium distribution.
     * @param deleteDownloadedFile If true, the downloaded file will be deleted after extraction.
     * @param downloadDirectoryPath The directory where the downloaded file will be saved.
     * @param c4jOsArchitecture The OS architecture.
     * @param properties The properties containing the download URLs (overwrites of the default URLs).
     * @return The browser-directory.
     * @throws Exception An unexpected exception.
     */
    public static File downloadChromiumOrFail(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                              boolean deleteDownloadedFile, Path downloadDirectoryPath,
                                              C4jOsArchitecture c4jOsArchitecture,
                                              Properties properties) throws Exception {
        String obtainDownloadUrl = getDownloadUrl(c4jOsArchitecture, properties);

        if (obtainDownloadUrl == null) {
            String infoString = C4jOsDetectionUtil.getOsArchitectureInfo().getInfoString();

            LOGGER.error("Unsupported OS: {}", infoString);

            throw new Exception("The given OS \"" + infoString + "\" is unsupported.");
        }

        Path extractionDir = downloadDirectoryPath.resolve(c4jOsChromiumDistribution.getId());

        Files.createDirectories(downloadDirectoryPath);
        Files.createDirectories(extractionDir);

        String zipFileName = ZIP_FILE_PREFIX + System.currentTimeMillis() + ZIP_FILE_SUFFIX;

        Path zipFilePath = extractionDir.resolve(zipFileName);

        downloadFileOrFail(obtainDownloadUrl, zipFilePath);

        extractZipOrFail(zipFilePath, extractionDir);

        LOGGER.info("Chromium downloaded and extracted successfully to: {}", extractionDir);

        if(deleteDownloadedFile) {
            LOGGER.info("Try to delete downloaded file.");

            try {
                Files.delete(zipFilePath);

                LOGGER.info("The downloaded file was deleted.");
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to delete downloaded file.", exception);
            }
        }

        return extractionDir.toFile();
    }

    private static String getDownloadUrl(C4jOsArchitecture osArchitecture, Properties properties) {
        return switch (osArchitecture) {
            case WINDOWS_X86 -> properties.getProperty(WINDOWS_X86_ARCHITECTURE_PROPERTY, DEFAULT_WINDOWS_X86_URL);
            case WINDOWS_X64 -> properties.getProperty(WINDOWS_X64_ARCHITECTURE_PROPERTY, DEFAULT_WINDOWS_X64_URL);
            case LINUX_X86 -> properties.getProperty(LINUX_X86_ARCHITECTURE_PROPERTY, DEFAULT_LINUX_X86_URL);
            case LINUX_X64 -> properties.getProperty(LINUX_X64_ARCHITECTURE_PROPERTY, DEFAULT_LINUX_X64_URL);
            default -> null;
        };
    }

    private static void downloadFileOrFail(String fileUrl, Path destinationPath) throws Exception {
        LOGGER.info("Try to download Chromium browser from URL: {}", fileUrl);

        long time = System.currentTimeMillis();

        FileDownloadUtil.downloadFileOrFail(fileUrl, destinationPath.toFile());

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

