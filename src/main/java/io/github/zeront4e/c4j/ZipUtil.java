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

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for working with ZIP files.
 */
class ZipUtil {
    /**
     * Extracts a ZIP file to the specified destination directory.
     * @param zipFile The ZIP file to be extracted.
     * @param destinationFile The destination directory where the ZIP file will be extracted.
     * @throws IOException An unexpected exception.
     */
    public static void unzip(File zipFile, File destinationFile) throws IOException {
        if (!destinationFile.exists())
            destinationFile.mkdirs();

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zipInputStream.getNextEntry();

            while (entry != null) {
                String filePath = destinationFile.getAbsolutePath() + "/" + entry.getName();

                if (!entry.isDirectory()) {
                    extractFile(zipInputStream, filePath);
                }
                else {
                    File directoryFile = new File(filePath);

                    directoryFile.mkdirs();
                }

                zipInputStream.closeEntry();

                entry = zipInputStream.getNextEntry();
            }
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File file = new File(filePath);

        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] byteBuffer = new byte[4096];

            int readBytes;

            while ((readBytes = zipIn.read(byteBuffer)) != -1) {
                bufferedOutputStream.write(byteBuffer, 0, readBytes);
            }
        }
    }
}
