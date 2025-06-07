package io.github.zeront4e.c4j;

import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.Set;


/**
 * Class for managing a remote Chromium instance.
 */
public class C4jRemoteChromium {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4jRemoteChromium.class);

    private final boolean testInstance;
    private final ChromeDriver chromeDriver;

    private final Set<C4jExtension> c4JExtensions;

    /**
     * Creates a new dummy remote Chromium instance for testing.
     */
    C4jRemoteChromium() {
        testInstance = true;
        chromeDriver = null;
        c4JExtensions = Set.of();
    }

    /**
     * Creates a new remote Chromium instance using the provided Chrome binary file and Chrome options.
     * @param chromeBinaryFile The path to the Chrome binary file.
     * @param c4jChromeOptions The Chrome options.
     */
    C4jRemoteChromium(File chromeBinaryFile, C4jChromeOptions c4jChromeOptions) throws Exception {
        //Obtain all extensions that should be installed.

        obtainExtensionsOrFail(chromeBinaryFile, c4jChromeOptions);

        //Configure the ChromeDriver.

        testInstance = false;

        //We always overwrite the binary file path in the Chrome options.
        c4jChromeOptions.getChromeOptions().setBinary(chromeBinaryFile);

        chromeDriver = new ChromeDriver(c4jChromeOptions.getChromeOptions());

        c4JExtensions = Collections.unmodifiableSet(c4jChromeOptions.getC4jCommonExtensions());

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
     * Returns whether this instance is a test instance (without any actual functionality).
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
     * Returns the configured extensions (the set is read-only).
     * @return The common extensions.
     */
    public Set<C4jExtension> getC4jExtensions() {
        return c4JExtensions;
    }

    private void obtainExtensionsOrFail(File chromeBinaryFile, C4jChromeOptions c4jChromeOptions) throws Exception {
        File installationDirectory = chromeBinaryFile.getParentFile();

        File extensionsDirectory = new File(installationDirectory, "c4j-extensions");
        extensionsDirectory.mkdirs();

        //Download and install the common extensions.

        for(C4jExtension tmpExtension : c4jChromeOptions.getC4jCommonExtensions()) {
            LOGGER.info("Try to obtain extension \"{}\". ID: \"{}\"", tmpExtension.getName(), tmpExtension.getId());

            File extensionFile = new File(extensionsDirectory, tmpExtension.getName() + ".crx");

            if(extensionFile.isFile() && !c4jChromeOptions.isReinstallExtensions()) {
                LOGGER.info("The extension is already installed (path \"{}\"). Skip download.",
                        extensionFile.getAbsolutePath());
            }
            else {
                LOGGER.info("The extension should be installed.");

                //Delete the existing extension file if it exists.

                if(extensionFile.isFile()) {
                    extensionFile.delete();

                    LOGGER.info("Delete existing extension file (path \"{}\").", extensionFile.getAbsolutePath());
                }

                //Download the extension.

                String downloadUrl = tmpExtension.getDownloadUrl();


                LOGGER.info("Try to download extension. Extension ID: \"{}\" Description: \"{}\" Source URL: \"{}\" " +
                                "Target file: \"{}\"", tmpExtension.getId(), tmpExtension.getDescription(), downloadUrl,
                        extensionFile.getAbsolutePath());

                FileDownloadUtil.downloadFileOrFail(downloadUrl, extensionFile);

                LOGGER.info("Downloaded extension.");
            }

            LOGGER.info("Try to register extension. Path: {}", extensionFile.getAbsolutePath());

            c4jChromeOptions.getChromeOptions().addExtensions(extensionFile);
        }
    }
}
