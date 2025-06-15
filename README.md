# chromium4j

A Java library for downloading and controlling Chromium browsers. You can use it to render a GUI (see app-mode), for 
data extraction or automated interactions (see headless-mode) and anything that can be achieved by controlling a modern 
browser.

You (probably) also don't have to worry about some restrictive licenses, because you don't distribute the Chromium 
browser (or any extension) by including this library in your project. Everything is downloaded "just-in-time". Just 
make sure to notify the user about the software that will be installed and the corresponding licenses. The library 
itself is licensed under the Apache 2 license (see LICENSE) for details.

chromium4j provides a simple way to:
- 📥 Download the latest Chromium build for different operating systems and architectures
- 🔧 Manage the local Chromium installation
- 🎮 Launch and control Chromium instances using Selenium
- ⚙️ Configure browser options
- 🧩 Install browser addons (custom CRX extensions)

## Overview

Currently supported platforms:
- 🪟 Windows (x32 and x64)
- 🐧 Linux (x32 and x64)

Currently supported Chromium distributions:
- Official Chromium builds from the build-trunk (see https://download-chromium.appspot.com)

Referenced Chromium extensions (can be easily obtained without manual steps):
- uBlock Origin Lite (see https://chromewebstore.google.com/detail/ublock-origin-lite/ddkjiahejlhfcafbddmgiahcphecmpfh)

## Installation 📦

Add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>io.github.zeront4e.c4j</groupId>
    <artifactId>chromium4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or for Gradle:

```groovy
implementation 'io.github.zeront4e.c4j:chromium4j:1.0.0'
```

## Usage examples 💻

### Basic usage  🚀

The following code creates a Chromium instance with default options. It just opens the Chromium browser when it is
ready.

chromium4j checks first if a local installation already exists (stored in the user-home directory).
If there is an existing installation the browser is launched. chromium4j downloads the latest available version if no
local installation was found.

```java
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;
import org.openqa.selenium.WebDriver;

public static void main(String[] args) {
    //"LATEST_CHROMIUM_BUILD" signalizes that we want to download the latest Chromium build from the official Chromium 
    //trunk. The OS (platform) and architecture is automatically detected. Note that you can customize the platform and 
    //architecture, if you want to. See the "C4j" class for details.

    C4jRemoteChromium c4jRemoteChromium = C4j.createInstance(C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD);

    //Control the Chromium instance with Selenium.

    WebDriver driver = c4jRemoteChromium.getDriver();
    driver.get("https://www.example.com");

    //Close the browser when done.

    c4jRemoteChromium.close();
}
```

### Custom options ⚙️

The following code creates a Chromium instance with fully customizable Selenium options (ChromeOptions).

You can find a summary of the available Chromium options here: 
https://peter.sh/experiments/chromium-command-line-switches/

```java
import io.github.zeront4e.c4j.C4j;
import io.github.zeront4e.c4j.C4jChromeOptions;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Example {
    public static void main(String[] args) {
        //Configure your custom ChromeOptions.

        ChromeOptions chromeOptions = new ChromeOptions();

        //Create a Chromium instance with custom options.

        C4jChromeOptions c4jChromeOptions = C4jChromeOptions.fromBuilder(chromeOptions).build();

        C4jRemoteChromium c4jRemoteChromium = C4j.createInstance(C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD,
                c4jChromeOptions);

        //...
    }
}
```

### App mode

The following code creates a Chromium instance with app-options. This opens the browser without controls and shows the
app icon instead of the Chromium icon in the taskbar. The browser window looks like a native "app". Note that the 
warnings about the remote-controlled browser are also hidden by default.

```java
import io.github.zeront4e.c4j.C4j;
import io.github.zeront4e.c4j.C4jChromeOptions;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;

public class Example {
    public static void main(String[] args) {
        //Launch Chromium in app mode.

        C4jChromeOptions c4jChromeOptions = C4jChromeOptions.withAppOptions("https://www.example.com")
                .build();

        C4jRemoteChromium c4jRemoteChromium = C4j.createInstance(C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD,
                c4jChromeOptions);

        //...
    }
}
```

### Headless mode

The following code creates a Chromium instance that runs in the background (it isn't visible in the taskbar). 
This is ideal for automated tasks (e.g. data extraction).

```java
import io.github.zeront4e.c4j.C4j;
import io.github.zeront4e.c4j.C4jChromeOptions;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;

public class Example {
    public static void main(String[] args) {
        //Launch Chromium in headless mode.

        C4jChromeOptions c4jChromeOptions = C4jChromeOptions.withHeadlessOptions().build();

        C4jRemoteChromium c4jRemoteChromium = C4j.createInstance(C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD,
                c4jChromeOptions);

        //...
    }
}
```

### Force the download of the latest version

The following code downloads the newest available Chromium version, even if there is an existing local installation.

```java
import io.github.zeront4e.c4j.C4j;
import io.github.zeront4e.c4j.C4jChromeOptions;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;

public class Example {
    public static void main(String[] args) {
        //Force the download of the latest Chromium build.

        C4jRemoteChromium remoteChromium = C4j.createInstance(
                C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD,
                C4jChromeOptions.fromBuilder().build(),
                true  //Overwrites an existing installation, if present.
        );

        //...
    }
}
```

The following code prints status updates during the launch attempt. This is useful to display updates to the user, until
the browser is finally launched.

### Receive status updates

```java
import io.github.zeront4e.c4j.C4j;
import io.github.zeront4e.c4j.C4jChromeOptions;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;

public class Example {
    public static void main(String[] args) {
        //Get status updates during the download and installation.

        C4jRemoteChromium remoteChromium = C4j.createInstance(
                C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD,
                C4jChromeOptions.fromBuilder().build(),
                false, //Don't overwrite an existing installation.
                status -> System.out.println("Status: " + status)
        );

        //...
    }
}
```

### Install extensions

The following code demonstrates how to download and install custom Chrome extensions. 

This example shows how to obtain a built-in extension and how to add a custom extension.

```java
import io.github.zeront4e.c4j.C4j;
import io.github.zeront4e.c4j.C4jChromeOptions;
import io.github.zeront4e.c4j.C4jExtension;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;

import java.util.Set;

public class Example {
    public static void main(String[] args) {
        //Create Chrome options with a built-in extension reference.

        C4jChromeOptions optionsWithUblock = C4jChromeOptions.fromBuilder().addExtensions(
                Set.of(C4jExtension.U_BLOCK_ORIGIN_LITE_EXTENSION),
                false //Set to true to update/reinstall already downloaded extensions.
        ).build();

        //Create Chrome options with a custom extension reference.

        C4jExtension darkReaderExtension = C4jExtension.createCustomExtension(
                "dark-reader", //Extension ID
                "Dark Reader", //Extension name
                "Dark mode for every website", //Extension description
                "https://your-site.com/your.crx", //Download URL
                "4fc5e69b7b5fcc1c86b4e754a6e71f742d0fb6ffdab3ee725f7fbe722a1aa8b6" //Hash to verify the integrity (optional)
        );

        C4jChromeOptions optionsWithCustomExtension = C4jChromeOptions.fromBuilder().addExtensions(
                Set.of(darkReaderExtension),
                false
        ).build();
    }
}
```

### Obtain the Chromium Version

The following code demonstrates how to obtain the version information of your Chromium instance.

```java
import io.github.zeront4e.c4j.C4j;
import io.github.zeront4e.c4j.C4jOsChromiumDistribution;
import io.github.zeront4e.c4j.C4jRemoteChromium;
import io.github.zeront4e.c4j.C4jRemoteChromium.ChromiumVersion;

public class Example {
    public static void main(String[] args) throws Exception {
        //Create a Chromium instance.
        
        C4jRemoteChromium remoteChromium = C4j.createInstance(C4jOsChromiumDistribution.LATEST_CHROMIUM_BUILD);
        
        //Get the version information.
        
        ChromiumVersion chromiumVersion = remoteChromium.getChromiumVersion();
        
        //Display version information.
        
        System.out.println("Version ID (first version part): " + chromiumVersion.guessedVersionId());
        System.out.println("Full Version: " + chromiumVersion.fullVersionString());
        
        //...
    }
}
```