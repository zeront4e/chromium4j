package io.github.zeront4e.c4j;

import java.util.Map;

public enum C4jOsChromiumDistribution {
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

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Map<C4jOsArchitecture, String> getArchitectureExecutableNameMap() {
        return architectureExecutableNameMap;
    }
}
