package io.github.zeront4e.c4j;

import java.io.File;

class ChromiumDetectionUtil {
    public static File findChromiumExecutableOrNull(C4jOsChromiumDistribution c4jOsChromiumDistribution,
                                                    C4jOsArchitecture c4jOsArchitecture, File directoryFile) {
        if(c4jOsArchitecture == C4jOsArchitecture.UNSUPPORTED)
            return null;

        String executableName = c4jOsChromiumDistribution.getArchitectureExecutableNameMap().get(c4jOsArchitecture);

        if(executableName == null)
            return null;

        return FileSearchUtil.findFileOrNull(directoryFile, executableName);
    }
}
