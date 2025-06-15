package io.github.zeront4e.c4j;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Helper class to obtain the Chromium version.
 */
class ChromiumVersionObtainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChromiumVersionObtainer.class);

    private boolean versionWasObtained = false;

    private C4jRemoteChromium.ChromiumVersion chromiumVersion = null;

    private final File chromeBinaryFile;

    public ChromiumVersionObtainer(File chromeBinaryFile) {
        this.chromeBinaryFile = chromeBinaryFile;
    }

    /**
     * Obtains the Chromium version of returns null on a failure.
     * @return The obtained Chromium version, or null if failure occurred.
     */
    public C4jRemoteChromium.ChromiumVersion obtainChromiumVersionOrNull() {
        if(!versionWasObtained) {
            versionWasObtained = true;

            chromiumVersion = obtainChromiumVersionOrNull(chromeBinaryFile);
        }

        return chromiumVersion;
    }

    private C4jRemoteChromium.ChromiumVersion obtainChromiumVersionOrNull(File chromeBinaryFile) {
        ChromeOptions chromeOptions = C4jChromeOptions.withHeadlessOptions().build().getChromeOptions();

        chromeOptions.setBinary(chromeBinaryFile);

        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);

        try {
            //Navigate to the internal Chromium version page.

            chromeDriver.get("chrome://version/");

            //Extract the version and build details.

            String fullVersion = chromeDriver.findElement(By.id("version")).getText();

            //Try to obtain the pure version ID, if present.

            String versionId;

            if(fullVersion.contains(" ")) {
                versionId = fullVersion.substring(0, fullVersion.indexOf(" "));
            }
            else {
                versionId = fullVersion;
            }

            return new C4jRemoteChromium.ChromiumVersion(versionId, fullVersion);
        }
        catch (Exception exception) {
            LOGGER.warn("Unable to obtain Chromium version.", exception);
        }

        return null;
    }
}
