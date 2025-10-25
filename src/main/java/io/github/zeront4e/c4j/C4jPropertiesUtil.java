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

/**
 * Utility class to set global properties, used by chromium4j.
 */
public class C4jPropertiesUtil {
    //--------------------------------------------------------
    //Chromium trunk download properties.
    //--------------------------------------------------------

    //Windows

    /**
     * Sets the Windows x64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsX64ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.WINDOWS_X64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Windows x86 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsX86ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.WINDOWS_X86_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Windows arm64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsArm64ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.WINDOWS_ARM64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Windows arm32 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsArm32ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.WINDOWS_ARM32_ARCHITECTURE_PROPERTY, value);
    }

    //Linux

    /**
     * Sets the Linux x64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxX64ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.LINUX_X64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Linux x86 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxX86ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.LINUX_X86_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Linux arm64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxArm64ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.LINUX_ARM64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Linux arm32 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxArm32ChromiumTrunkDownloadUrlProperty(String value) {
        System.setProperty(DownloaderLatestTrunkChromiumUtil.LINUX_ARM32_ARCHITECTURE_PROPERTY, value);
    }

    //--------------------------------------------------------
    //Brave download properties.
    //--------------------------------------------------------

    //Windows

    /**
     * Sets the Windows x64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsX64BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.WINDOWS_X64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Windows x86 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsX86BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.WINDOWS_X86_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Windows arm64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsArm64BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.WINDOWS_ARM64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Windows arm32 download url.
     *
     * @param value The URL to download from.
     */
    public static void setWindowsArm32BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.WINDOWS_ARM32_ARCHITECTURE_PROPERTY, value);
    }

    //Linux

    /**
     * Sets the Linux x64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxX64BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.LINUX_X64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Linux x86 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxX86BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.LINUX_X86_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Linux arm64 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxArm64BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.LINUX_ARM64_ARCHITECTURE_PROPERTY, value);
    }

    /**
     * Sets the Linux arm32 download url.
     *
     * @param value The URL to download from.
     */
    public static void setLinuxArm32BraveDownloadUrlProperty(String value) {
        System.setProperty(DownloaderStableBrave.LINUX_ARM32_ARCHITECTURE_PROPERTY, value);
    }

    //--------------------------------------------------------
    //Additional properties.
    //--------------------------------------------------------

    /**
    * Sets the home directory.
    *
    * @param value The home directory.
    */
    public static void setChromium4jHomeDirectoryProperty(String value) {
        System.setProperty(Constants.CHROMIUM4J_HOME_DIRECTORY_PATH_PROPERTY, value);
    }

    /**
    * Sets the Java installation directory.
    *
    * @param value The Java installation directory.
    */
    public static void setJavaExecutableDirectoryProperty(String value) {
        System.setProperty(WindowManagerUtil.JAVA_EXECUTABLE_DIRECTORY_PATH_PROPERTY, value);
    }
}
