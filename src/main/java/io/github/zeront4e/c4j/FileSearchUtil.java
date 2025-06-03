package io.github.zeront4e.c4j;

import java.io.File;

class FileSearchUtil {
    /**
     * Finds a file in a directory and its subdirectories.
     * @param directoryFile The directory to search in.
     * @param fileName The name of the file to find.
     * @return The found file if found, null otherwise.
     */
    public static File findFileOrNull(File directoryFile, String fileName) {
        if (!directoryFile.exists() || !directoryFile.isDirectory()) {
            System.out.println("Invalid directory path.");

            return null;
        }

        File[] files = directoryFile.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    //Add Mac OS specific exception.
                    if(file.getName().endsWith(".app") && file.getName().equals(fileName))
                        return file;

                    File foundFile = findFileOrNull(file, fileName);

                    if (foundFile != null)
                        return foundFile;
                }
                else if (file.getName().equals(fileName)) {
                    return file;
                }
            }
        }

        return null;
    }
}
