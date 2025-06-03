package io.github.zeront4e.c4j;

import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Class to configure the Chromium instance to launch. It contains (common) preset functions to create a preconfigured Chromium instance.
 * There is also a Builder class to create a fully customized Chromium instance.
 */
public class C4jChromeOptions {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4jChromeOptions.class);

    //Internal data.

    private final ChromeOptions chromeOptions;
    private final Set<C4jCommonExtension> c4jCommonExtensions;
    private final Properties customExtensionProperties;

    private C4jChromeOptions(ChromeOptions chromeOptions, Set<C4jCommonExtension> c4jCommonExtensions,
                             Properties customExtensionProperties) {
        this.chromeOptions = chromeOptions;
        this.c4jCommonExtensions = c4jCommonExtensions;
        this.customExtensionProperties = customExtensionProperties;
    }

    /**
     * Returns the {@link ChromeOptions} instance.
     * @return The ChromeOptions instance.
     */
    public ChromeOptions getChromeOptions() {
        return chromeOptions;
    }

    /**
     * Returns the set of registered common extensions.
     * @return The set of common extensions.
     */
    public Set<C4jCommonExtension> getC4jCommonExtensions() {
        return c4jCommonExtensions;
    }

    /**
     * Returns the custom extension properties.
     * @return The custom extension properties.
     */
    public Properties getCustomExtensionProperties() {
        return customExtensionProperties;
    }

    //Builder.

    public static class Builder {
        private final Set<C4jCommonExtension> c4jCommonExtensions = new HashSet<>();
        private Properties customExtensionProperties;

        private final ChromeOptions chromeOptions;

        Builder(ChromeOptions chromeOptions) {
            this.chromeOptions = chromeOptions;
        }

        /**
         * Adds experimental options to disable automation warning ("excludeSwitches" set to "enable-automation"
         * and "useAutomationExtension" set to "false").
         * @return The builder instance.
         */
        public Builder addOptionDisabledAutomationWarningOption() {
            LOGGER.info("Try to disable automation warning. Set experimental option \"excludeSwitches\" to " +
                    "\"enable-automation\" and \"useAutomationExtension\" to \"false\".");

            chromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
            chromeOptions.setExperimentalOption("useAutomationExtension", false);

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
            LOGGER.info("Add \"--window-size\" option. Width: {} Height: {}", width, height);

            chromeOptions.addArguments("--window-size=" + width + "x" + height);

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
         * Registers common extensions to obtain and install, when the instance is launched.
         * @param c4jCommonExtensions The common extensions to register.
         * @return The builder instance.
         */
        public Builder addCommonExtensions(Set<C4jCommonExtension> c4jCommonExtensions) {
            return addCommonExtensions(c4jCommonExtensions, System.getProperties());
        }

        /**
         * Registers common extensions to obtain and install, when the instance is launched.
         * @param c4jCommonExtensions The common extensions to register.
         * @param customExtensionProperties The custom properties for the common extensions.
         * @return The builder instance.
         */
        public Builder addCommonExtensions(Set<C4jCommonExtension> c4jCommonExtensions, Properties customExtensionProperties) {
            c4jCommonExtensions.forEach(tmpCommonExtension ->
                    LOGGER.info("Try to register common extension \"{}\".", tmpCommonExtension.getId()));

            this.c4jCommonExtensions.addAll(c4jCommonExtensions);
            this.customExtensionProperties = customExtensionProperties;

            return this;
        }

        /**
         * Creates the {@link C4jChromeOptions} instance with the configured options.
         * @return The configured {@link C4jChromeOptions} instance.
         */
        public C4jChromeOptions build() {
            return new C4jChromeOptions(chromeOptions, c4jCommonExtensions, customExtensionProperties);
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
                .addOptionDisableDevShmUsage();

         builder.addOptionWindowSize(windowWidth, windowHeight);

         if(disableGpuRendering)
            builder.addOptionDisableGpu();

         return builder;
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
