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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class FileSearchUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(FileSearchUtil.class);

    /**
     * Finds a file in a directory and its subdirectories.
     * @param directoryFile The directory to search in.
     * @param fileName The name of the file to find.
     * @return The found file if found, null otherwise.
     */
    public static File findFileOrNull(File directoryFile, String fileName) {
        if (!directoryFile.exists() || !directoryFile.isDirectory()) {
            LOGGER.error("Return null. Invalid directory path: {}", directoryFile.getAbsolutePath());

            return null;
        }

        File[] files = directoryFile.listFiles();

        if (files != null) {
            for (File tmpFile : files) {
                if (tmpFile.isDirectory()) {
                    //Add Mac OS specific exception.
                    if(tmpFile.getName().endsWith(".app") && tmpFile.getName().equals(fileName))
                        return tmpFile;

                    File foundFile = findFileOrNull(tmpFile, fileName);

                    if (foundFile != null)
                        return foundFile;
                }
                else if (tmpFile.getName().equals(fileName)) {
                    return tmpFile;
                }
            }
        }

        return null;
    }
}
