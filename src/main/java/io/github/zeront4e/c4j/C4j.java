package io.github.zeront4e.c4j;

import io.github.zeront4e.c4j.downloader.C4jChromiumDownloader;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

public class C4j {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4j.class);

    /**
     * Reports status updates (e.g. the state of the local setup and the overall progress).
     */
    public interface StatusCallback {
        /**
         * The callback function to indicate the status update.
         * @param status Additional (new) status information.
         */
        void onStatusUpdate(String status);
    }

    /**
     * Creates a new Chromium instance for the given distribution that can be controlled remotely.
     * @param c4jOsChromiumDistribution The distribution to obtain (download) or launch (if already installed).
     * @return The remote instance.
     * @throws Exception An unexpected exception.
     */
    public static C4jRemoteChromium createInstance(C4jOsChromiumDistribution c4jOsChromiumDistribution) throws Exception {
        return createInstance(c4jOsChromiumDistribution, C4jChromeOptions.fromBuilder(new ChromeOptions()).build());
    }

    /**
     * Creates a new Chromium instance for the given distribution that can be controlled remotely.
     * @param c4jOsChromiumDistribution The distribution to obtain (download) or launch (if already installed).
     * @param c4jChromeOptions Configuration options to pass to the remote Chromium instance. Note that the binary-path is
     *                      automatically overwritten for the given distribution.
     * @return The remote instance.
     * @throws Exception An unexpected exception.
     */
    public static C4jRemoteChromium createInstance(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                                   C4jChromeOptions c4jChromeOptions) throws Exception {
        return createInstance(c4jOsChromiumDistribution, c4jChromeOptions, false);
    }

    /**
     * Creates a new Chromium instance for the given distribution that can be controlled remotely.
     * @param c4jOsChromiumDistribution The distribution to obtain (download) or launch (if already installed).
     * @param c4jChromeOptions Configuration options to pass to the remote Chromium instance. Note that the binary-path is
     *                      automatically overwritten for the given distribution.
     * @param overwrite True if the latest Chromium distribution should be downloaded and installed, even if a local
     *                  installation already exists.
     * @return The remote instance.
     * @throws Exception An unexpected exception.
     */
    public static C4jRemoteChromium createInstance(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                                   C4jChromeOptions c4jChromeOptions,
                                                   boolean overwrite) throws Exception {
        return createInstance(c4jOsChromiumDistribution, c4jChromeOptions, overwrite, LOGGER::info);
    }

    /**
     * Creates a new Chromium instance for the given distribution that can be controlled remotely.
     * @param c4jOsChromiumDistribution The distribution to obtain (download) or launch (if already installed).
     * @param c4jChromeOptions Configuration options to pass to the remote Chromium instance. Note that the binary-path is
     *                      automatically overwritten for the given distribution.
     * @param overwrite True if the latest Chromium distribution should be downloaded and installed, even if a local
     *                  installation already exists.
     * @param statusCallback A status callback to reports status updates (e.g. the state of the local setup and the
     *                       overall progress).
     * @return The remote instance.
     * @throws Exception An unexpected exception.
     */
    public static C4jRemoteChromium createInstance(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                                   C4jChromeOptions c4jChromeOptions, boolean overwrite,
                                                   StatusCallback statusCallback) throws Exception {
        File chromiumFile = obtainDefaultChromiumOrFail(c4jOsChromiumDistribution, statusCallback, overwrite);

        return createInstance(chromiumFile, c4jChromeOptions);
    }

    /**
     * Creates a new Chromium instance for the given executable and the launch options.
     * @param chromiumFile The executable to pass the options to.
     * @param c4jChromeOptions The options to pass to the executable.
     * @return The remote instance.
     */
    public static C4jRemoteChromium createInstance(File chromiumFile, C4jChromeOptions c4jChromeOptions) {
        return new C4jRemoteChromium(chromiumFile, c4jChromeOptions.getChromeOptions());
    }

    /**
     * Returns true if an installation is present for the given Chromium distribution for the detected architecture.
     * @param c4jOsChromiumDistribution The distribution to check.
     * @return True if there is an existing Chromium installation.
     */
    public static boolean isDefaultInstallationPresent(C4jOsChromiumDistribution c4jOsChromiumDistribution) {
        return isDefaultInstallationPresent(getDefaultInstallationChromiumFile(c4jOsChromiumDistribution));
    }

    /**
     * Returns true if an installation is present for the given Chromium distribution and the given architecture.
     * @param c4jOsChromiumDistribution The distribution to check.
     * @param c4jOsArchitecture The architecture to check.
     * @return True if there is an existing Chromium installation.
     */
    public static boolean isDefaultInstallationPresent(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                                       C4jOsArchitecture c4jOsArchitecture) {
        return isDefaultInstallationPresent(getDefaultInstallationChromiumFile(c4jOsChromiumDistribution,
                c4jOsArchitecture));
    }

    private static boolean isDefaultInstallationPresent(File file) {
        return file != null && file.exists();
    }

    private static File getDefaultInstallationChromiumFile(C4jOsChromiumDistribution c4jOsChromiumDistribution) {
        return getDefaultInstallationChromiumFile(c4jOsChromiumDistribution, C4jOsDetectionUtil.detectOsArchitecture());
    }

    private static File getDefaultInstallationChromiumFile(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                                          C4jOsArchitecture c4jOsArchitecture) {
        File defaultDirectory = C4jChromiumDownloader
                .getDefaultDistributionInstallationDirectory(c4jOsChromiumDistribution);

        if(!defaultDirectory.isDirectory())
            return null;

        return ChromiumDetectionUtil.findChromiumExecutableOrNull(c4jOsChromiumDistribution, c4jOsArchitecture,
                defaultDirectory);
    }

    private static File obtainDefaultChromiumOrFail(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                                    StatusCallback statusCallback, boolean overwrite) throws Exception {
        boolean performInstallation;

        if(overwrite) {
            File defaultDirectoryFile = C4jChromiumDownloader
                    .getDefaultDistributionInstallationDirectory(c4jOsChromiumDistribution);

            statusCallback.onStatusUpdate("Overwrite is enabled. Try to delete existing Chromium installation. Path: " +
                    defaultDirectoryFile.getAbsolutePath());

            boolean deletion = Files.deleteIfExists(defaultDirectoryFile.toPath());

            statusCallback.onStatusUpdate("Deletion attempt was completed. Deletion occurred: " + deletion);

            performInstallation = true;
        }
        else {
            statusCallback.onStatusUpdate("Overwrite is disabled. Try to find existing Chromium installation.");

            performInstallation = !isDefaultInstallationPresent(c4jOsChromiumDistribution);
        }

        if(performInstallation) {
            statusCallback.onStatusUpdate("An installation attempt should be performed. Try to download Chromium. " +
                    "Please wait.");

            C4jChromiumDownloader.downloadChromiumOrFail(c4jOsChromiumDistribution);

            statusCallback.onStatusUpdate("The Chromium download was completed.");
        }

        File existingFile = getDefaultInstallationChromiumFile(c4jOsChromiumDistribution);

        if(existingFile == null || !existingFile.exists()) {
            statusCallback.onStatusUpdate("Unable to find Chromium installation. Return null.");

            return null;
        }

        statusCallback.onStatusUpdate("The Chromium installation was found. Path: " + existingFile.getAbsolutePath());

        return existingFile;
    }
}
