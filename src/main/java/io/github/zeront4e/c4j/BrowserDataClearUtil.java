package io.github.zeront4e.c4j;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v137.network.Network;

import java.util.Set;

class BrowserDataClearUtil {
    public static void clearDataForUrlPath(ChromiumDriver chromiumDriver, DevTools devTools, String urlPath) {
        chromiumDriver.get(urlPath);

        String domain = urlPath.replace("https://", "")
                .replace("http://", "")
                .split("/")[0];

        devTools.send(Network.clearBrowserCache());

        Set<Cookie> cookies = chromiumDriver.manage().getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getDomain() != null && cookie.getDomain().contains(domain)) {
                chromiumDriver.manage().deleteCookie(cookie);
            }
        }

        chromiumDriver.executeScript("localStorage.clear(); sessionStorage.clear();");

        chromiumDriver.executeScript("indexedDB.databases().then(dbs => " +
                "dbs.forEach(db => indexedDB.deleteDatabase(db.name)));");

        chromiumDriver.executeScript("caches.keys().then(keys => keys.forEach(key => caches.delete(key)));");

        chromiumDriver.executeScript("navigator.serviceWorker.getRegistrations().then(regs => " +
                "regs.forEach(reg => reg.unregister()));");
    }
}