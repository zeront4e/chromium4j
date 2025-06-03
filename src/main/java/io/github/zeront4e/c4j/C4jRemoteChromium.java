package io.github.zeront4e.c4j;

import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;


/**
 * Class for managing a remote Chromium instance.
 */
public class C4jRemoteChromium {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4jRemoteChromium.class);

    private final boolean testInstance;
    private final ChromeDriver chromeDriver;

    private final Set<C4jCommonExtension> c4jCommonExtensions;

    /**
     * Creates a new dummy remote Chromium instance for testing.
     */
    C4jRemoteChromium() {
        testInstance = true;
        chromeDriver = null;
        c4jCommonExtensions = Set.of();
    }

    /**
     * Creates a new remote Chromium instance using the provided Chrome binary file and Chrome options.
     * @param chromeBinaryFile The path to the Chrome binary file.
     * @param c4jChromeOptions The Chrome options.
     */
    C4jRemoteChromium(File chromeBinaryFile, C4jChromeOptions c4jChromeOptions) throws Exception {
        File installationDirectory = chromeBinaryFile.getParentFile();

        File extensionsDirectory = new File(installationDirectory, "c4j-extensions");
        extensionsDirectory.mkdirs();

        //Download and install the common extensions.

        for(C4jCommonExtension tmpExtension : c4jChromeOptions.getC4jCommonExtensions()) {
            File extensionFile = new File(extensionsDirectory, tmpExtension.getId() + ".crx");

            if(extensionFile.isFile()) {
                LOGGER.info("Extension {} is already installed (path \"{}\"). Skip download.", tmpExtension.getId(),
                        extensionFile.getAbsolutePath());
            }
            else {
                String downloadUrl = c4jChromeOptions.getCustomExtensionProperties()
                        .getProperty(tmpExtension.getDownloadOverwriteProperty(), tmpExtension.getDefaultDownloadUrl());

                LOGGER.info("Try to download extension: {} Target file: \"{}\"", tmpExtension.getId(),
                        extensionFile.getAbsolutePath());

                FileDownloadUtil.downloadFileOrFail(downloadUrl, extensionFile);

                LOGGER.info("Downloaded extension.");
            }

            LOGGER.info("Try to register extension. Path: {}", extensionFile.getAbsolutePath());

            c4jChromeOptions.getChromeOptions().addExtensions(extensionFile);
        }

        //Configure the ChromeDriver.

        testInstance = false;

        c4jChromeOptions.getChromeOptions().setBinary(chromeBinaryFile);

        chromeDriver = new ChromeDriver(c4jChromeOptions.getChromeOptions());

        c4jCommonExtensions = c4jChromeOptions.getC4jCommonExtensions();

        //Add a shutdown hook to quit the Chromium instance when the VM is terminated.

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                chromeDriver.quit();
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to quit Chrome driver.", exception);
            }
        }));
    }

    /**
     * Returns whether this instance is a test instance.
     * @return True if this instance is a test instance, false otherwise.
     */
    boolean isTestInstance() {
        return testInstance;
    }

    /**
     * Returns the {@link ChromeDriver} instance.
     * @return The ChromeDriver instance.
     */
    public ChromeDriver getChromeDriver() {
        return chromeDriver;
    }

    /**
     * Returns the expected common extensions to obtain and install.
     * @return The common extensions.
     */
    public Set<C4jCommonExtension> getC4jCommonExtensions() {
        return c4jCommonExtensions;
    }
}
