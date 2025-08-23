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

import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.devtools.DevTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

/**
 * Class for managing a remote Chromium instance.
 */
public class C4jRemoteChromium {
    /**
     * Represents a version of Chromium.
     * @param guessedVersionId The guessed version ID (first part of the full version string).
     * @param fullVersionString The full version string.
     */
    public record ChromiumVersion(String guessedVersionId, String fullVersionString) {

    }

    private boolean chromiumInstanceActive = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(C4jRemoteChromium.class);

    private final boolean testInstance;
    private final ChromeDriver chromeDriver;

    private final SeleniumUtilChromiumVersionObtainer seleniumUtilChromiumVersionObtainer;

    private final Set<C4jExtension> c4JExtensions;

    private final List<Runnable> browserExitListeners = Collections.synchronizedList(new ArrayList<>());

    private WindowManagerWindow stealthChromiumWindow = null;

    /**
     * Creates a new dummy remote Chromium instance for testing.
     */
    C4jRemoteChromium() {
        testInstance = true;
        chromeDriver = null;

        seleniumUtilChromiumVersionObtainer = null;

        c4JExtensions = Set.of();
    }

    /**
     * Creates a new remote Chromium instance using the provided Chrome binary file and Chrome options.
     * @param chromeBinaryFile The path to the Chrome binary file.
     * @param c4jChromeOptions The Chrome options.
     */
    C4jRemoteChromium(File chromeBinaryFile, C4jChromeOptions c4jChromeOptions) throws Exception {
        testInstance = false;

        //Obtain all extensions that should be installed.

        obtainExtensionsOrFail(chromeBinaryFile, c4jChromeOptions);

        //Configure/overwrite the Chromium binary.

        c4jChromeOptions.getChromeOptions().setBinary(chromeBinaryFile);

        //Check if we want to set a custom service-builder.

        ChromeDriverService.Builder builder;

        if(c4jChromeOptions.getChromeDriverServiceBuilder() != null) {
            LOGGER.info("Use provided builder for ChromeDriverService.");

            builder = c4jChromeOptions.getChromeDriverServiceBuilder();
        }
        else {
            LOGGER.info("Use default builder for ChromeDriverService.");

            builder = new ChromeDriverService.Builder();
        }

        //Check if we want to set custom env-variables.

        if(c4jChromeOptions.getEnvironmentVariablesMap() != null) {
            LOGGER.info("Set/overwrite builder environment variables.");

            builder.withEnvironment(c4jChromeOptions.getEnvironmentVariablesMap());
        }

        //Check if we want to set a custom driver-window size.

        ChromeDriverService chromeDriverService = builder.build();

        chromeDriver = new ChromeDriver(chromeDriverService, c4jChromeOptions.getChromeOptions());

        if(c4jChromeOptions.getCustomDriverWidth() > -1 && c4jChromeOptions.getCustomDriverHeight() > -1) {
            LOGGER.info("Set custom driver size. Width: {}, Height: {}", c4jChromeOptions.getCustomDriverWidth(),
                    c4jChromeOptions.getCustomDriverHeight());

            chromeDriver.manage().window().setSize(new Dimension(c4jChromeOptions.getCustomDriverWidth(),
                    c4jChromeOptions.getCustomDriverHeight()));
        }

        //Check if we want to run the browser in stealth-mode.

        if(c4jChromeOptions.isEnableStealthMode()) {
            LOGGER.info("Try to enable stealth mode.");

            UuidWebServer uuidWebServer = null;

            try {
                uuidWebServer = new UuidWebServer();

                uuidWebServer.start();

                try {
                    chromeDriver.manage().window().minimize();
                }
                catch (Exception exception) {
                    LOGGER.warn("Unable to minimize initial window.", exception);
                }

                chromeDriver.get(uuidWebServer.getUrl());

                List<WindowManagerWindow> windows = WindowManagerUtil.getWindows(uuidWebServer.getUuid());

                if(windows.size() != 1)
                    throw new Exception("Unable to find window. Expected window UUID: " + uuidWebServer.getUuid());

                WindowManagerWindow window = windows.get(0);

                WindowManagerUtil.minimizeWindow(window.id());
                WindowManagerUtil.changeTaskbarVisibility(window.id(), false);

                stealthChromiumWindow = window;

                LOGGER.info("Stealth mode was enabled. The window should be hidden.");
            }
            catch (Exception exception) {
                LOGGER.error("Unable to enable stealth mode.", exception);
            }
            finally {
                try {
                    if(uuidWebServer != null)
                        uuidWebServer.stop();
                }
                catch (Exception exception) {
                    LOGGER.error("Unable to stop the UUID server.", exception);
                }
            }
        }

        //Set additional internal variables.

        seleniumUtilChromiumVersionObtainer = new SeleniumUtilChromiumVersionObtainer(chromeBinaryFile);

        c4JExtensions = Collections.unmodifiableSet(c4jChromeOptions.getC4jCommonExtensions());

        //Add a shutdown hook to quit the Chromium instance when the VM is terminated.

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                chromeDriver.quit();

                transformToInactiveState();
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to quit Chrome driver.", exception);
            }
        }));

        //Start the internal monitoring to detect the browser exit.

        monitorBrowserExit(chromeDriver);
    }

    /**
     * Toggles the taskbar visibility of the stealth Chromium window. The window is minimized, if the icon should be
     * hidden (visibility is set to "false").
     * @param showWindow The desired visibility state.
     * @return True if the visibility was successfully toggled (the stealth mode is enabled), false otherwise (there is
     * no window, because the stealth mode is disabled).
     */
    public boolean toggleStealthWindowTaskbarVisibility(boolean showWindow) throws Exception {
        if(stealthChromiumWindow == null)
            return false;

        LOGGER.debug("Toggle stealth window taskbar visibility. New state: {}", showWindow);

        if(!showWindow)
            WindowManagerUtil.minimizeWindow(stealthChromiumWindow.id());

        WindowManagerUtil.changeTaskbarVisibility(stealthChromiumWindow.id(), showWindow);

        return true;
    }

    /**
     * Returns true if the Chromium instance is active and ready to interact with.
     * @return True if the Chromium instance is (still) active, false otherwise.
     */
    public boolean isChromiumInstanceActive() {
        return chromiumInstanceActive;
    }

    /**
     * Clears the browser data for the given URL path for a given handle (by using the dev-tools for a certain
     * window/tab).
     * @param urlPath The URL path to clear.
     * @param devTools The DevTools instance for the window/tab.
     */
    public void clearBrowserDataForUrlPath(String urlPath, DevTools devTools) {
        SeleniumUtilBrowserDataClearUtil.clearDataForUrlPath(chromeDriver, devTools, urlPath);
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
     * Returns the version of Chromium that this instance uses or null, if no version could be extracted.
     * @return The version of Chromium.
     */
    public ChromiumVersion getChromiumVersionOrNull() {
        return seleniumUtilChromiumVersionObtainer == null ?
                null : seleniumUtilChromiumVersionObtainer.obtainChromiumVersionOrNull();
    }

    /**
     * Returns the configured extensions (the set is read-only).
     * @return The common extensions.
     */
    public Set<C4jExtension> getC4jExtensions() {
        return c4JExtensions;
    }

    /**
     * Adds a listener that will be called when the browser exits.
     * @param listener The listener to be added.
     * @return True if the listener was added, false otherwise.
     */
    public boolean addBrowserExitListener(Runnable listener) {
        return browserExitListeners.add(listener);
    }

    /**
     * Removes a listener that will be called when the browser exits.
     * @param listener The listener to be removed.
     * @return True if the listener was removed, false otherwise.
     */
    public boolean removeBrowserExitListener(Runnable listener) {
        return browserExitListeners.remove(listener);
    }

    private void monitorBrowserExit(ChromeDriver chromeDriver) {
        //Monitors Selenium if the browser exits.

        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    //Try to get the current URL. If it fails, it means the browser has exited.
                    chromeDriver.getCurrentUrl();
                }
                catch (Exception exception) {
                    LOGGER.info("Unable to contact the browser. Assume it was exited.", exception);

                    break;
                }

                try {
                    Thread.sleep(1000);
                }
                catch (Exception exception) {
                    Thread.currentThread().interrupt();
                }
            }

            transformToInactiveState();
        });
    }

    private synchronized void transformToInactiveState() {
        chromiumInstanceActive = false;

        for(Runnable runnable : browserExitListeners) {
            try {
                runnable.run();
            }
            catch (Exception callbackException) {
                LOGGER.warn("Error calling browser exit listener.", callbackException);
            }
        }

        browserExitListeners.clear();
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

                FilesDownloadUtil.downloadFileOrFail(downloadUrl, extensionFile);

                LOGGER.info("Downloaded extension.");

                verifyHashOrFail(extensionFile, tmpExtension.getOptionalSha256Checksum());
            }

            LOGGER.info("Try to register extension. Path: {}", extensionFile.getAbsolutePath());

            c4jChromeOptions.getChromeOptions().addExtensions(extensionFile);
        }
    }

    private void verifyHashOrFail(File extensionFile, String expectedChecksum) throws Exception {
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
