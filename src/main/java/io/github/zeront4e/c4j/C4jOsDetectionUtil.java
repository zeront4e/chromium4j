package io.github.zeront4e.c4j;

import java.util.Locale;

public class C4jOsDetectionUtil {
    public record OsInfo(String osName, String osArchitecture) {
        @Override
        public String toString() {
            return "OsInfo{" +
                    "osName='" + osName + '\'' +
                    ", osArchitecture='" + osArchitecture + '\'' +
                    '}';
        }

        public String getInfoString() {
            return "os.name: " + osName + " os.arch: " + osArchitecture;
        }
    }

    public static OsInfo getOsArchitectureInfo() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String osArchitecture = System.getProperty("os.arch");

        return new OsInfo(osName, osArchitecture);
    }

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
