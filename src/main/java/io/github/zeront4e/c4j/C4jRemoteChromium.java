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

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
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
     * Clears the browser data for the given URL path for a given handle (by using the dev-tools for a certain
     * window/tab).
     * @param urlPath The URL path to clear.
     * @param devTools The DevTools instance for the window/tab.
     */
    public void clearBrowserDataForUrlPath(String urlPath, DevTools devTools) {
        BrowserDataClearUtil.clearDataForUrlPath(chromeDriver, devTools, urlPath);
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

                verifyHashOrFail(extensionFile, tmpExtension.getOptionalSha256Checksum());
            }

            LOGGER.info("Try to register extension. Path: {}", extensionFile.getAbsolutePath());

            c4jChromeOptions.getChromeOptions().addExtensions(extensionFile);
        }
    }

    private static void verifyHashOrFail(File extensionFile, String expectedChecksum) throws Exception {
        if(expectedChecksum != null && !expectedChecksum.isBlank()) {
            LOGGER.info("Check the SHA-256 checksum of the downloaded extension.");

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            //Update the digest by using an input stream.

            try(FileInputStream fileInputStream = new FileInputStream(extensionFile)) {
                byte[] buffer = new byte[8192];

                int bytesRead;

                while((bytesRead = fileInputStream.read(buffer, 0, buffer.length)) != -1) {
                    messageDigest.update(buffer, 0, bytesRead);
                }
            }

            byte[] digestBytes = messageDigest.digest();

            String sha256Checksum = String.format("%0" + (digestBytes.length << 1) + "X",
                    new BigInteger(1, digestBytes));

            LOGGER.info("Expected hash: {} Actual hash: {}", expectedChecksum, sha256Checksum);

            if(!sha256Checksum.equalsIgnoreCase(expectedChecksum))
                throw new Exception("Invalid SHA-256 checksum for the downloaded extension.");

            LOGGER.info("SHA-256 checksum is valid.");
        }
    }
}
