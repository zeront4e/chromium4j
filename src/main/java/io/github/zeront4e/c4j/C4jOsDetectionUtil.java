package io.github.zeront4e.c4j;

import java.util.Locale;

/**
 * Utility class for detecting the operating system architecture.
 */
public class C4jOsDetectionUtil {
    /**
     * Record representing the detected operating system architecture.
     * @param osName The name of the operating system.
     * @param osArchitecture The architecture of the operating system.
     */
    public record OsInfo(String osName, String osArchitecture) {
        /**
         * Returns a string representation of the operating system properties ("os.name" and "os.arch").
         * @return A string representation of the operating system properties.
         */
        public String getInfoString() {
            return "os.name: " + osName + " os.arch: " + osArchitecture;
        }
    }

    /**
     * Detects the operating system architecture and returns a record containing the operating system properties.
     * @return A record containing the operating system information.
     */
    public static OsInfo getOsArchitectureInfo() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String osArchitecture = System.getProperty("os.arch");

        return new OsInfo(osName, osArchitecture);
    }

    /**
     * Detects the operating system architecture and returns the corresponding architecture enum value.
     * @return The detected operating system architecture.
     */
    public static C4jOsArchitecture detectOsArchitecture() {
        OsInfo osInfo = getOsArchitectureInfo();

        boolean is64BitArchitecture = osInfo.osArchitecture().contains("64");

        String osName = osInfo.osName().toLowerCase(Locale.ENGLISH);

        if (osName.contains("win")) {
            return is64BitArchitecture ? C4jOsArchitecture.WINDOWS_X64 : C4jOsArchitecture.WINDOWS_X86;
        }
        else if (osName.contains("nux") || osName.contains("nix") || osName.contains("bsd")) {
            return is64BitArchitecture ? C4jOsArchitecture.LINUX_X64 : C4jOsArchitecture.LINUX_X86;
        }

        return C4jOsArchitecture.UNSUPPORTED;
    }
}
