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

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

class DownloaderStableBrave {
    public static String GIT_HUB_ORGANIZATION = "release-monitoring-project";
    public static String GIT_HUB_REPOSITORY = "brave-release-tracker";
    public static String GIT_HUB_ASSET_NAME = "brave_download_links.json";

    public static final String WINDOWS_X64_ARCHITECTURE_PROPERTY = "chromium4j.download-url.stable-brave.windows_x64";
    public static final String WINDOWS_X86_ARCHITECTURE_PROPERTY = "chromium4j.download-url.stable-brave.windows_x86";

    public static final String WINDOWS_ARM64_ARCHITECTURE_PROPERTY =
            "chromium4j.download-url.stable-brave.windows_arm64";

    public static final String WINDOWS_ARM32_ARCHITECTURE_PROPERTY =
            "chromium4j.download-url.stable-brave.windows_arm32";

    public static final String LINUX_X64_ARCHITECTURE_PROPERTY = "chromium4j.download-url.stable-brave.linux_x64";
    public static final String LINUX_X86_ARCHITECTURE_PROPERTY = "chromium4j.download-url.stable-brave.linux_x86";

    public static final String LINUX_ARM64_ARCHITECTURE_PROPERTY = "chromium4j.download-url.stable-brave.linux_arm64";
    public static final String LINUX_ARM32_ARCHITECTURE_PROPERTY = "chromium4j.download-url.stable-brave.linux_arm32";

    private static final String ZIP_FILE_PREFIX = "brave-stable";
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
        DownloaderReleaseMonitoringProjectUtil.DownloadConfiguration downloadConfiguration =
                new DownloaderReleaseMonitoringProjectUtil.DownloadConfiguration(
                        GIT_HUB_ORGANIZATION,
                        GIT_HUB_REPOSITORY,
                        GIT_HUB_ASSET_NAME,

                        ZIP_FILE_PREFIX,
                        ZIP_FILE_SUFFIX,

                        WINDOWS_X64_ARCHITECTURE_PROPERTY,
                        WINDOWS_X86_ARCHITECTURE_PROPERTY,
                        WINDOWS_ARM64_ARCHITECTURE_PROPERTY,
                        WINDOWS_ARM32_ARCHITECTURE_PROPERTY,
                        LINUX_X64_ARCHITECTURE_PROPERTY,
                        LINUX_X86_ARCHITECTURE_PROPERTY,
                        LINUX_ARM32_ARCHITECTURE_PROPERTY,
                        LINUX_ARM64_ARCHITECTURE_PROPERTY
                );

        return DownloaderReleaseMonitoringProjectUtil.downloadChromiumOrFail(downloadConfiguration,
                c4jOsChromiumDistribution, deleteDownloadedFile, downloadDirectoryPath, c4jOsArchitecture, properties);
    }
}

