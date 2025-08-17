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

import org.openqa.selenium.Cookie;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v137.network.Network;

import java.util.Set;

class SeleniumUtilBrowserDataClearUtil {
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