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

public class VersionComparisonUtil {
    /**
     * Returns true if the new version is newer than the current version.
     * @param currentVersion The current version.
     * @param newVersion The new version.
     * @return True, if the new version is newer than the current version.
     * @throws Exception An unexpected exception.
     */
    public static boolean isNewerVersion(String currentVersion, String newVersion) throws Exception {
        int[] currentSegments = parseVersion(currentVersion);
        int[] newerSegments = parseVersion(newVersion);

        for (int tmpSegmentIndex = 0; tmpSegmentIndex < 3; tmpSegmentIndex++) {
            if (newerSegments[tmpSegmentIndex] > currentSegments[tmpSegmentIndex]) {
                return true;
            }
            else if (newerSegments[tmpSegmentIndex] < currentSegments[tmpSegmentIndex]) {
                return false;
            }
        }

        return false;
    }

    private static int[] parseVersion(String version) throws Exception {
        String[] versionParts = version.replace("\n", "").trim().split("\\.");

        if (versionParts.length != 3)
            throw new Exception("Version must be in MAJOR.MINOR.PATCH format.");

        int[] segments = new int[3];

        for (int tmpSegmentIndex = 0; tmpSegmentIndex < 3; tmpSegmentIndex++) {
            segments[tmpSegmentIndex] = Integer.parseInt(versionParts[tmpSegmentIndex]);
        }

        return segments;
    }
}
