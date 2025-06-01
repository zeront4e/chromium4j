package io.github.zeront4e.c4j;

import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class C4jChromeOptions {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4jChromeOptions.class);

    //Internal data.

    private final ChromeOptions chromeOptions;

    private C4jChromeOptions(ChromeOptions chromeOptions) {
        this.chromeOptions = chromeOptions;
    }

    public ChromeOptions getChromeOptions() {
        return chromeOptions;
    }

    //Builder.

    public static class Builder {
        private final ChromeOptions chromeOptions;

        Builder(ChromeOptions chromeOptions) {
            this.chromeOptions = chromeOptions;
        }

        private Builder addAutomationWarningOption() {
            LOGGER.info("Try to disable automation warning. Set experimental option \"excludeSwitches\" to " +
                    "\"enable-automation\".");

            chromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));

            return this;
        }

        private Builder addAppOption(String appUrl) {
            LOGGER.info("Add \"--app\" option. URL: {}", appUrl);

            chromeOptions.addArguments("--app=" + appUrl);

            return this;
        }

        public C4jChromeOptions build() {
            return new C4jChromeOptions(chromeOptions);
        }
    }

    //Public options.

    public static C4jChromeOptions withAppOptions(String appUrl) {
        return fromBuilder(new ChromeOptions())
                .addAutomationWarningOption()
                .addAppOption(appUrl)
                .build();
    }

    public static Builder fromBuilder(ChromeOptions chromeOptions) {
        return new Builder(chromeOptions);
    }
}
