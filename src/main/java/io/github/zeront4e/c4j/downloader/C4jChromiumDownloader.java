package io.github.zeront4e.c4j.downloader;

import io.github.zeront4e.c4j.C4jOsArchitecture;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jOsDetectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class C4jChromiumDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4jChromiumDownloader.class);

    public static final String DEFAULT_USER_HOME_DOWNLOAD_DIRECTORY = "chromium4j-downloads";

    /**
     * Returns the default distribution installation directory file.
     * @return The default distribution installation file.
     */
    public static File getDefaultDistributionInstallationDirectory(C4jOsChromiumDistribution c4jOsChromiumDistribution) {
        String baseDirectoryPath = getDefaultInstallationDirectory().getAbsolutePath();

        return new File(baseDirectoryPath + "/" + c4jOsChromiumDistribution.getId());
    }

    /**
     * Returns the default installation directory file.
     * @return The default installation file.
     */
    public static File getDefaultInstallationDirectory() {
        String baseDirectoryPath = System.getProperty("user.home");

        return new File(baseDirectoryPath + "/" + DEFAULT_USER_HOME_DOWNLOAD_DIRECTORY);
    }

    /**
     * Downloads the latest Chromium distribution for the current OS architecture into the home-directory. The function
     * reads the system properties to apply possible URL overwrites. The downloaded files will be stored in the home
     * directory of the user in the directory "chromium4j-downloads". The downloaded file will be deleted
     * after the extraction.
     * @param c4jOsChromiumDistribution The Chromium distribution to download.
     * @return The directory containing the extracted data.
     * @throws Exception An unexpected exception.
     */
    public static File downloadChromiumOrFail(C4jOsChromiumDistribution c4jOsChromiumDistribution) throws Exception {
        return downloadChromiumOrFail(c4jOsChromiumDistribution, true);
    }

    /**
     * Downloads the latest Chromium distribution for the current OS architecture into the home-directory. The function
     * reads the system properties to apply possible URL overwrites. The downloaded files will be stored in the home
     * directory of the user in the directory "chromium4j-downloads".
     * @param c4jOsChromiumDistribution The Chromium distribution to download.
     * @param deleteDownloadedFile True, if the downloaded file should be deleted.
     * @return The directory containing the extracted data.
     * @throws Exception An unexpected exception.
     */
    public static File downloadChromiumOrFail(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                              boolean deleteDownloadedFile) throws Exception {
        File downloadDirectoryFile = getDefaultInstallationDirectory();

        return downloadChromiumOrFail(c4jOsChromiumDistribution, deleteDownloadedFile, downloadDirectoryFile.toPath());
    }

    /**
     * Downloads the latest Chromium distribution for the current OS architecture into the given directory. The function
     * reads the system properties to apply possible URL overwrites.
     * @param c4jOsChromiumDistribution The Chromium distribution to download.
     * @param deleteDownloadedFile True, if the downloaded file should be deleted.
     * @param downloadDirectoryPath The path to download/install the distributions to.
     * @return The directory containing the extracted data.
     * @throws Exception An unexpected exception.
     */
    public static File downloadChromiumOrFail(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                              boolean deleteDownloadedFile, Path downloadDirectoryPath) throws Exception {
        return downloadChromiumOrFail(c4jOsChromiumDistribution, deleteDownloadedFile, downloadDirectoryPath,
                C4jOsDetectionUtil.detectOsArchitecture());
    }

    /**
     * Downloads the latest Chromium distribution for the given OS architecture into the given directory. The function
     * reads the system properties to apply possible URL overwrites.
     * @param c4jOsChromiumDistribution The Chromium distribution to download.
     * @param deleteDownloadedFile True, if the downloaded file should be deleted.
     * @param downloadDirectoryPath The path to download/install the distributions to.
     * @param c4jOsArchitecture The architecture to download the Chromium distribution for.
     * @return The directory containing the extracted data.
     * @throws Exception An unexpected exception.
     */
    public static File downloadChromiumOrFail(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                              boolean deleteDownloadedFile, Path downloadDirectoryPath,
                                              C4jOsArchitecture c4jOsArchitecture) throws Exception {
        return downloadChromiumOrFail(c4jOsChromiumDistribution, deleteDownloadedFile, downloadDirectoryPath,
                c4jOsArchitecture, System.getProperties());
    }


    /**
     * Downloads the latest Chromium distribution for the given OS architecture into the given directory.
     * @param c4jOsChromiumDistribution The Chromium distribution to download.
     * @param deleteDownloadedFile True, if the downloaded file should be deleted.
     * @param downloadDirectoryPath The path to download/install the distributions to.
     * @param c4jOsArchitecture The architecture to download the Chromium distribution for.
     * @param properties The properties to overwrite the default download URLs with.
     * @return The directory containing the extracted data.
     * @throws Exception An unexpected exception.
     */
    public static File downloadChromiumOrFail(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                              boolean deleteDownloadedFile, Path downloadDirectoryPath,
                                              C4jOsArchitecture c4jOsArchitecture,
                                              Properties properties) throws Exception {
        LOGGER.info("Try to download Chromium for distribution {} for architecture {}. Download path: \"{}\" " +
                "Delete downloaded file: {}", c4jOsChromiumDistribution.name(), c4jOsArchitecture.name(),
                downloadDirectoryPath.toAbsolutePath(), deleteDownloadedFile);

        if(c4jOsChromiumDistribution == C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD) {
            return LatestTrunkChromiumDownloader.downloadChromiumOrFail(c4jOsChromiumDistribution, deleteDownloadedFile,
                    downloadDirectoryPath, c4jOsArchitecture, properties);
        }

        throw new Exception("Missing Chromium distribution implementation \"" + c4jOsChromiumDistribution.name() +
                "\".");
    }
}

