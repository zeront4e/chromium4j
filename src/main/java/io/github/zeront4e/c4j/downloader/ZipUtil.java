package io.github.zeront4e.c4j.downloader;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ZipUtil {
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
