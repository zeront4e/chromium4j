package io.github.zeront4e.c4j;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class C4jRemoteChromium {
    private static final Logger LOGGER = LoggerFactory.getLogger(C4jRemoteChromium.class);

    private final ChromeDriver chromeDriver;

    C4jRemoteChromium(File chromeBinaryFile, ChromeOptions chromeOptions) {
        chromeOptions.setBinary(chromeBinaryFile);

        chromeDriver = new ChromeDriver(chromeOptions);

        //Add a shutdown hook to quit the Chromium instance when the VM is terminated.

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                chromeDriver.quit();
            }
            catch (Exception exception) {
                LOGGER.error("Unable to quit Chrome driver.", exception);
            }
        }));
    }

    public ChromeDriver getChromeDriver() {
        return chromeDriver;
    }
}
