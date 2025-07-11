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

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Class to configure the Chromium instance to launch. It contains (common) preset functions to create a preconfigured
 * Chromium instance. There is also a Builder class to create a fully customized Chromium instance.
 */
public class C4jChromeOptions {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4jChromeOptions.class);

    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.3";

    //Internal data.

    private record InternalData(ChromeOptions chromeOptions, Set<C4jExtension> c4JExtensions,
                                boolean reinstallExtensions, int customDriverWidth, int customDriverHeight,
                                ChromeDriverService.Builder chromeDriverServiceBuilder,
                                Map<String, String> environmentVariablesMap, boolean enableStealthMode) {

    }

    private final InternalData internalData;

    private C4jChromeOptions(InternalData internalData) {
        this.internalData = internalData;
    }

    /**
     * Returns the {@link ChromeOptions} instance.
     * @return The ChromeOptions instance.
     */
    public ChromeOptions getChromeOptions() {
        return internalData.chromeOptions();
    }

    /**
     * Returns the set of registered common extensions.
     * @return The set of common extensions.
     */
    public Set<C4jExtension> getC4jCommonExtensions() {
        return internalData.c4JExtensions();
    }

    /**
     * Returns true if all extensions should be reinstalled, even if already downloaded.
     * @return True if extensions should be reinstalled.
     */
    public boolean isReinstallExtensions() {
        return internalData.reinstallExtensions();
    }

    /**
     * Returns the custom driver width.
     * @return The custom driver width.
     */
    public int getCustomDriverWidth() {
        return internalData.customDriverWidth();
    }

    /**
     * Returns the custom driver height.
     * @return The custom driver height.
     */
    public int getCustomDriverHeight() {
        return internalData.customDriverHeight();
    }

    /**
     * Returns the builder used to create this instance.
     * @return The builder used to create this instance.
     */
    public ChromeDriverService.Builder getChromeDriverServiceBuilder() {
        return internalData.chromeDriverServiceBuilder();
    }

    /**
     * Returns the environment variables map.
     * @return The environment variables map.
     */
    public Map<String, String> getEnvironmentVariablesMap() {
        return internalData.environmentVariablesMap();
    }

    /**
     * Returns true if the stealth mode is enabled.
     * @return True if the stealth mode is enabled.
     */
    public boolean isEnableStealthMode() {
        return internalData.enableStealthMode();
    }

    //Builder.

    public static class Builder {
        private final ChromeOptions chromeOptions;

        private Set<C4jExtension> c4JExtensions = Collections.emptySet();
        private boolean reinstallExtensions = false;

        private int customDriverWidth = -1;
        private int customDriverHeight = -1;

        private ChromeDriverService.Builder chromeDriverServiceBuilder = null;

        private Map<String, String> environmentVariablesMap = null;

        private boolean enableStealthMode = false;

        Builder(ChromeOptions chromeOptions) {
            this.chromeOptions = chromeOptions;
        }

        /**
         * Adds experimental options to disable automation warning ("excludeSwitches" set to "enable-automation"
         * and "useAutomationExtension" set to "false"). Also disables the blink feature "AutomationControlled".
         * @return The builder instance.
         */
        public Builder addOptionDisabledAutomationWarningOption() {
            C4jOsArchitecture c4jOsArchitecture = C4jOsDetectionUtil.detectOsArchitecture();

            if(c4jOsArchitecture != C4jOsArchitecture.WINDOWS_X86 &&
                    c4jOsArchitecture != C4jOsArchitecture.WINDOWS_X64) {
                LOGGER.warn("Chromium doesn't hide the test-related info-bar messages on non-Windows platforms. " +
                        "Set the argument \"--disable-infobars\" to hide ALL info-bar messages.");

                chromeOptions.addArguments("--disable-infobars");
            }

            LOGGER.info("Try to disable automation detection. Set experimental option \"excludeSwitches\" to " +
                    "\"enable-automation\" and \"useAutomationExtension\" to \"false\". Also disable blink feature " +
                    "\"AutomationControlled\".");

            chromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
            chromeOptions.setExperimentalOption("useAutomationExtension", false);

            chromeOptions.addArguments("--disable-blink-features=AutomationControlled");

            return this;
        }

        /**
         * Sets the default user agent.
         * @return The builder instance.
         */
        public Builder addDefaultUserAgent() {
            return addOptionUserAgent(DEFAULT_USER_AGENT);
        }

        /**
         * Sets the custom user agent.
         * @param userAgent The custom user agent.
         * @return The builder instance.
         */
        public Builder addOptionUserAgent(String userAgent) {
            LOGGER.info("Set user agent to: {}", userAgent);

            chromeOptions.addArguments("--user-agent=" + userAgent);

            return this;
        }

        /**
         * Adds the \"--app\" option to open Chromium in the app mode.
         * @param appUrl URL of the app to open in the app mode.
         * @return The builder instance.
         */
        public Builder addOptionApp(String appUrl) {
            LOGGER.info("Add \"--app\" option. URL: {}", appUrl);

            chromeOptions.addArguments("--app=" + appUrl);

            return this;
        }

        /**
         * Adds the "--headless" option to run Chrome in headless mode.
         * @return The builder instance.
         */
        public Builder addOptionHeadless() {
            LOGGER.info("Add \"--headless\" option.");

            chromeOptions.addArguments("--headless");

            return this;
        }

        /**
         * Adds the "--disable-gpu" option to disable GPU acceleration.
         * @return The builder instance.
         */
        public Builder addOptionDisableGpu() {
            LOGGER.info("Add \"--disable-gpu\" option.");

            chromeOptions.addArguments("--disable-gpu");

            return this;
        }

        /**
         * Adds the "--window-size" option to set the initial window size.
         * @param width The width of the window.
         * @param height The height of the window.
         * @return The builder instance.
         */
        public Builder addOptionWindowSize(int width, int height) {
            return addOptionWindowSize(width, height, true);
        }

        /**
         * Adds the "--window-size" option to set the initial window size.
         * @param width The width of the window.
         * @param height The height of the window.
         * @param setCustomDriverSize Sets the given with and height also for the driver, if true.
         * @return The builder instance.
         */
        public Builder addOptionWindowSize(int width, int height, boolean setCustomDriverSize) {
            LOGGER.info("Add \"--window-size\" option. Width: {} Height: {}", width, height);

            chromeOptions.addArguments("--window-size=" + width + "," + height);

            if(setCustomDriverSize) {
                LOGGER.info("Set also custom driver window-size. Width: {} Height: {}", width, height);

                customDriverWidth = width;
                customDriverHeight = height;
            }

            return this;
        }

        /**
         * Adds the "--disable-dev-shm-usage" option to disable shared memory usage. This can prevent Chrome from
         * crashing when running in certain environments.
         * @return The builder instance.
         */
        public Builder addOptionDisableDevShmUsage() {
            LOGGER.info("Add \"--disable-dev-shm-usage\" option.");

            chromeOptions.addArguments("--disable-dev-shm-usage");

            return this;
        }

        /**
         * Adds the "--incognito" option to run Chrome in incognito mode.
         * @return The builder instance.
         */
        public Builder addOptionIncognito() {
            LOGGER.info("Add \"--incognito\" option.");

            chromeOptions.addArguments("--incognito");

            return this;
        }

        /**
         * Tries to run the browser in stealth mode (not headless, but not visible to the user). This will also enable
         * all options from {@link C4jChromeOptions.Builder#addOptionDisabledAutomationWarningOption}. Notice that the
         * stealth-mode will launch the browser with an actual window. This window is minimized and hidden from the
         * taskbar to achieve the same effect as a headless browser (from a user perspective). To achieve this JNA is
         * used. On Windows the Win32 API is used to hide the browser. On Linux the X11 API is used to hide the browser.
         * Please make sure that your Linux distribution has the X11 server installed. If you use Wayland make sure to
         * install Xwayland or a similar alternative. Note that the browser option "--ozone-platform=x11" is set, to
         * enable the X11 usage on Linux.
         * @return The builder instance.
         */
        public Builder addOptionStealthMode() {
            LOGGER.info("Set flag to launch the browser in stealth mode.");

            enableStealthMode = true;

            addOptionDisabledAutomationWarningOption();

            C4jOsArchitecture c4jOsArchitecture = C4jOsDetectionUtil.detectOsArchitecture();

            if(c4jOsArchitecture == C4jOsArchitecture.LINUX_X86 || c4jOsArchitecture == C4jOsArchitecture.LINUX_X64) {
                LOGGER.info("Platform is set to {}. Add \"--ozenet-platform=x11\" option.", c4jOsArchitecture.name());

                chromeOptions.addArguments("--ozone-platform=x11");
            }
            else {
                LOGGER.info("Platform is set to {}. Skip \"--ozenet-platform=x11\" option.", c4jOsArchitecture.name());
            }

            return this;
        }

        /**
         * Registers common extensions to obtain and install, when the instance is launched. Only missing extensions
         * will be downloaded. Existing extensions won't be updated.
         * @param c4JExtensions The common extensions to register.
         * @return The builder instance.
         */
        public Builder addExtensions(Set<C4jExtension> c4JExtensions) {
            return addExtensions(c4JExtensions, false);
        }

        /**
         * Registers common extensions to obtain and install, when the instance is launched.
         * @param c4JExtensions The common extensions to register.
         * @param reinstallExtensions Whether to reinstall all extensions, even if they were already downloaded.
         * @return The builder instance.
         */
        public Builder addExtensions(Set<C4jExtension> c4JExtensions, boolean reinstallExtensions) {
            c4JExtensions.forEach(tmpCommonExtension ->
                    LOGGER.info("Try to register common extension \"{}\".", tmpCommonExtension.getId()));

            this.c4JExtensions = c4JExtensions;
            this.reinstallExtensions = reinstallExtensions;

            return this;
        }

        /**
         * Sets a custom Chrome driver service builder.
         * @param chromeDriverServiceBuilder The Chrome driver service builder to set.
         * @return The builder instance.
         */
        public Builder addCustomServiceBuilder(ChromeDriverService.Builder chromeDriverServiceBuilder) {
            LOGGER.info("Set custom Chrome driver service builder.");

            this.chromeDriverServiceBuilder = chromeDriverServiceBuilder;

            return this;
        }

        /**
         * Sets a custom environment variables map.
         * @param environmentVariablesMap The custom environment variables map to set.
         * @return The builder instance.
         */
        public Builder addEnvironmentVariablesMap(Map<String, String> environmentVariablesMap) {
            LOGGER.info("Set custom environment variables map: {}",
                    Arrays.toString(environmentVariablesMap.entrySet().toArray()));

            this.environmentVariablesMap = environmentVariablesMap;

            return this;
        }

        /**
         * Creates the {@link C4jChromeOptions} instance with the configured options.
         * @return The configured {@link C4jChromeOptions} instance.
         */
        public C4jChromeOptions build() {
            return new C4jChromeOptions(new InternalData(
                    chromeOptions,
                    c4JExtensions,
                    reinstallExtensions,

                    //Custom driver-window size.
                    customDriverWidth,
                    customDriverHeight,

                    chromeDriverServiceBuilder,
                    environmentVariablesMap,
                    enableStealthMode
            ));
        }
    }

    //App options.

    /**
     * Launches the Chromium browser with app options (the browser has no controls and the favicon is visible in
     * the taskbar).
     * @param appUrl The URL of the app to be launched.
     * @return The preconfigured builder instance.
     */
    public static Builder withAppOptions(String appUrl) {
        return fromBuilder(new ChromeOptions())
                .addOptionDisabledAutomationWarningOption()
                .addOptionApp(appUrl);
    }

    //Headless options.

    /**
     * Launches the Chromium browser in headless mode.
     * @return The preconfigured builder instance.
     */
    public static Builder withHeadlessOptions() {
        return withHeadlessOptions(false);
    }

    /**
     * Launches the Chromium browser in headless mode with specified window size and optionally disables GPU rendering.
     * @param disableGpuRendering True, if GPU rendering should be disabled.
     * @return The preconfigured builder instance.
     */
    public static Builder withHeadlessOptions(boolean disableGpuRendering) {
        return withHeadlessOptions(disableGpuRendering, 1920, 1080);
    }

    /**
     * Launches the Chromium browser in headless mode with specified window size, GPU rendering option, and
     * optionally disables GPU rendering.
     * @param disableGpuRendering True, if GPU rendering should be disabled.
     * @param windowWidth The width of the window.
     * @param windowHeight The height of the window.
     * @return The preconfigured builder instance.
     */
    public static Builder withHeadlessOptions(boolean disableGpuRendering, int windowWidth, int windowHeight) {
        Builder builder = fromBuilder(new ChromeOptions())
                .addOptionHeadless()
                .addOptionDisableDevShmUsage()
                .addOptionDisabledAutomationWarningOption()
                .addOptionWindowSize(windowWidth, windowHeight);

        if(disableGpuRendering)
            builder.addOptionDisableGpu();

        return builder;
    }

    //Stealth options.

    /**
     * Tries to run the browser in stealth mode (not headless, but not visible to the user). This will also enable
     * all options from {@link C4jChromeOptions.Builder#addOptionDisabledAutomationWarningOption}. Notice that the
     * stealth-mode will launch the browser with an actual window. This window is minimized and hidden from the
     * taskbar to achieve the same effect as a headless browser (from a user perspective). To achieve this JNA is
     * used. On Windows the Win32 API is used to hide the browser. On Linux the X11 API is used to hide the browser.
     * Please make sure that your Linux distribution has the X11 server installed. If you use Wayland make sure to
     * install Xwayland or a similar alternative. Note that the browser option "--ozone-platform=x11" is set, to
     * enable the X11 usage on Linux.
     * @return The builder instance.
     */
    public static Builder withStealthOptions() {
        return fromBuilder(new ChromeOptions()).addOptionStealthMode();
    }

    /**
     * Tries to run the browser in stealth mode (not headless, but not visible to the user). This will also enable
     * all options from {@link C4jChromeOptions.Builder#addOptionDisabledAutomationWarningOption}. Notice that the
     * stealth-mode will launch the browser with an actual window. This window is minimized and hidden from the
     * taskbar to achieve the same effect as a headless browser (from a user perspective). To achieve this JNA is
     * used. On Windows the Win32 API is used to hide the browser. On Linux the X11 API is used to hide the browser.
     * Please make sure that your Linux distribution has the X11 server installed. If you use Wayland make sure to
     * install Xwayland or a similar alternative. Note that the browser option "--ozone-platform=x11" is set, to
     * enable the X11 usage on Linux.
     * @param windowWidth The width of the window.
     * @param windowHeight The height of the window.
     * @return The builder instance.
     */
    public static Builder withStealthOptions(int windowWidth, int windowHeight) {
        return fromBuilder(new ChromeOptions())
                .addOptionStealthMode()
                .addOptionWindowSize(windowWidth, windowHeight);
    }

    //Builder options.

    /**
     * Creates a new builder with default ChromeOptions. You can customize the options using the builder methods.
     * @return A new {@link Builder} instance.
     */
    public static Builder fromBuilder() {
        return new Builder(new ChromeOptions());
    }

    /**
     * Creates a new builder with the given ChromeOptions. You can customize the options using the builder methods.
     * @param chromeOptions The initial ChromeOptions.
     * @return A new {@link Builder} instance.
     */
    public static Builder fromBuilder(ChromeOptions chromeOptions) {
        return new Builder(chromeOptions);
    }
}
