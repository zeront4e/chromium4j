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

import java.util.Map;

/**
 * Enum to represent different Chromium distributions and their supported architectures.
 */
public enum C4jOsChromiumDistribution {
    /**
     * Official latest stable build of the Chromium web browser.
     */
    LATEST_CHROMIUM_BUILD(
            "latest-trunk-build",
            "Official latest trunk build. Downloaded from \"https://download-chromium.appspot.com\".",
            Map.of(
                C4jOsArchitecture.LINUX_X86, "chrome",
                C4jOsArchitecture.LINUX_X64, "chrome",
                C4jOsArchitecture.WINDOWS_X86, "chrome.exe",
                C4jOsArchitecture.WINDOWS_X64, "chrome.exe"
            )
    );

    private final String id;
    private final String description;
    private final Map<C4jOsArchitecture, String> architectureExecutableNameMap;

    C4jOsChromiumDistribution(String name, String description,
                              Map<C4jOsArchitecture, String> architectureExecutableNameMap) {
        this.id = name;
        this.description = description;
        this.architectureExecutableNameMap = architectureExecutableNameMap;
    }

    /**
     * Returns the ID of the Chromium distribution.
     * @return The ID of the Chromium distribution.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the description of the Chromium distribution.
     * @return The description of the Chromium distribution.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a map to get the executable name for a given architecture.
     * @return The map that maps architectures to executable names.
     */
    public Map<C4jOsArchitecture, String> getArchitectureExecutableNameMap() {
        return architectureExecutableNameMap;
    }
}
