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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class WindowManagerUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowManagerUtil.class);

    public static final String JAVA_EXECUTABLE_DIRECTORY_PATH_PROPERTY = "chromium4j.java-executable-directory-path";

    private static final String CLI_VERSION_FILE_PATH = "/jna-window-manager-cli-version.txt";

    private static final String CLI_RESOURCE_PATH = "/jna-window-manager-cli.jar";

    private static final String DO_NOT_DELETE_README_RESOURCE_PATH = "/DO-NOT-DELETE-README.txt";

    private static Path extractWindowManagerCli() throws Exception {
        String pomVersion = PomVersionExtractionUtil.getPomVersion();

        Path versionFilePath = Path.of(Constants.CHROMIUM4J_HOME_DIRECTORY_PATH + "/" +
                Constants.DEFAULT_HOME_DOWNLOAD_DIRECTORY + CLI_VERSION_FILE_PATH);

        boolean overwriteFile;

        if(Files.exists(versionFilePath)) {
            String versionString = Files.readString(versionFilePath, StandardCharsets.UTF_8);

            try {
                overwriteFile = VersionComparisonUtil.isNewerVersion(versionString, pomVersion);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to compare existing version with current library version.", exception);

                overwriteFile = true;
            }
        }
        else {
            overwriteFile = true;
        }

        Path targetFilePath = Path.of(Constants.CHROMIUM4J_HOME_DIRECTORY_PATH + "/" +
                Constants.DEFAULT_HOME_DOWNLOAD_DIRECTORY + CLI_RESOURCE_PATH);

        if(!overwriteFile && Files.exists(targetFilePath)) {
            LOGGER.info("Window manager CLI already exists. Path: {}", targetFilePath);
        }
        else {
            LOGGER.info("Try to (re)extract window manager CLI. Path: {}", targetFilePath);

            //Write version file.

            Files.writeString(versionFilePath, pomVersion, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            //Write CLI file.

            ResourcesUtil.extractFile(CLI_RESOURCE_PATH, targetFilePath);

            //Write readme file.

            Path readmeFilePath = Path.of(Constants.CHROMIUM4J_HOME_DIRECTORY_PATH + "/" +
                    Constants.DEFAULT_HOME_DOWNLOAD_DIRECTORY + DO_NOT_DELETE_README_RESOURCE_PATH);

            ResourcesUtil.extractFile(DO_NOT_DELETE_README_RESOURCE_PATH, readmeFilePath);

            LOGGER.info("Extracted window manager CLI.");
        }

        return targetFilePath;
    }

    private static Path getJavaExecutablePathOrNull(Path javaDirectoryPath) {
        if(Files.isDirectory(javaDirectoryPath)) {
            Path javaExecutablePath = javaDirectoryPath.resolve("java.exe");

            if(Files.exists(javaExecutablePath)) {
                LOGGER.info("Windows Java executable is available. Path: {}", javaExecutablePath);

                return javaExecutablePath;
            }

            javaExecutablePath = javaDirectoryPath.resolve("java");

            if(Files.exists(javaExecutablePath)) {
                LOGGER.info("Unix-like Java executable is available. Path: {}", javaExecutablePath);

                return javaExecutablePath;
            }

            LOGGER.info("Custom Java directory is set. Path: {}", javaDirectoryPath);

            return javaDirectoryPath;
        }

        LOGGER.info("Java directory was not found.");

        return null;
    }

    private static String runCliCommandOrNull(List<String> args) throws Exception {
        LOGGER.info("Try to run CLI. Args: {}", Arrays.toString(args.toArray()));

        //Construct paths.

        Path jarCliExecutablePath = extractWindowManagerCli();

        String javaHome = System.getProperty("java.home", "");

        String configuredJavaPath = System.getProperty(JAVA_EXECUTABLE_DIRECTORY_PATH_PROPERTY, javaHome.isBlank() ?
                "" : javaHome + "/bin");

        Path javaDirectoryPath = Path.of(configuredJavaPath);

        Path javaExecutablePath = getJavaExecutablePathOrNull(javaDirectoryPath);

        if(javaExecutablePath == null)
            throw new Exception("Java executable is not available.");

        //Construct arguments.

        List<String> finalArguments = new ArrayList<>();

        finalArguments.add(javaExecutablePath.toAbsolutePath().toString());
        finalArguments.add("--enable-native-access=ALL-UNNAMED");
        finalArguments.add("-jar");
        finalArguments.add(jarCliExecutablePath.toAbsolutePath().toString());

        finalArguments.addAll(args);

        //Create process builder.

        ProcessBuilder processBuilder = new ProcessBuilder(finalArguments.toArray(new String[0]));
        processBuilder.redirectErrorStream(true);

        //Start process.

        Process process = processBuilder.start();

        String output = null;

        try {
            byte[] readBytes = process.getInputStream().readAllBytes();

            int exitCode = process.waitFor();

            if(exitCode == 0) {
                output = new String(readBytes, StandardCharsets.UTF_8);
            }
            else {
                String error = new String(readBytes, StandardCharsets.UTF_8);

                LOGGER.error("Failed to run CLI. Exit code: {} Error: {}", exitCode, error);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to run CLI.", exception);
        }

        return output;
    }

    private static List<WindowManagerWindow> extractWindows(String response) throws Exception {
        String[] lines = response.split("\n");

        if(lines.length > 1) {
            for(int tmpIndex = 0; tmpIndex < lines.length - 1; tmpIndex++) {
                String tmpLine = lines[tmpIndex];

                if(!tmpLine.isBlank())
                    LOGGER.info("CLI: {}", tmpLine);
            }
        }

        String payloadCandidate = lines[lines.length - 1];

        JsonElement jsonElement = JsonParser.parseString(payloadCandidate);

        if(!jsonElement.isJsonArray())
            throw new Exception("Response is not a JSON array.");

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        List<WindowManagerWindow> windows = new ArrayList<>();

        for(int i = 0; i < jsonArray.size(); i++) {
            JsonElement tmpArrayJsonElement = jsonArray.get(i);

            if (!tmpArrayJsonElement.isJsonObject())
                throw new Exception("Response is not a JSON object.");

            JsonObject jsonObject = tmpArrayJsonElement.getAsJsonObject();

            WindowManagerWindow windowManagerWindow = new WindowManagerWindow(jsonObject.get("id").getAsLong(),
                    jsonObject.get("title").getAsString());

            windows.add(windowManagerWindow);
        }

        LOGGER.debug("Extracted {} windows.", windows.size());

        return windows;
    }

    /**
     * Returns the matching window instances.
     * @param partialWindowTitle The partial window title to match against.
     * @return List of available window information.
     * @throws Exception An unexpected exception.
     */
    public static List<WindowManagerWindow> getWindows(String partialWindowTitle) throws Exception {
        LOGGER.debug("Searching for windows with partial title: {}", partialWindowTitle);

        String response = runCliCommandOrNull(List.of("get-windows", partialWindowTitle));

        if(response == null)
            return List.of();

        return extractWindows(response);
    }

    /**
     * Get window information recursively.
     * @return List of available window information.
     * @throws Exception An unexpected exception.
     */
    public static List<WindowManagerWindow> getWindows() throws Exception {
        LOGGER.debug("Searching for all windows.");

        String response = runCliCommandOrNull(List.of("get-windows"));

        if(response == null)
            return List.of();

        return extractWindows(response);
    }

    /**
     * Adds or removes the taskbar icon of the specified window.
     * @param windowId The window ID.
     * @param setVisible Whether to make the window visible or not.
     * @return True if the visibility was changed, false otherwise.
     * @throws Exception An unexpected exception.
     */
    public static boolean changeTaskbarVisibility(long windowId, boolean setVisible) throws Exception {
        LOGGER.debug("Try to change taskbar visibility. ID: {} Set visible: {}", windowId, setVisible);

        String response = runCliCommandOrNull(List.of("toggle-window-taskbar-icon", String.valueOf(windowId),
                String.valueOf(setVisible)));

        if(response == null) {
            LOGGER.warn("Unable to change taskbar visibility.");

            return false;
        }

        return true;
    }

    /**
     * Minimizes the specified window.
     * @param windowId The window ID.
     * @return True if the window was minimized, false otherwise.
     * @throws Exception An unexpected exception.
     */
    public static boolean minimizeWindow(long windowId) throws Exception {
        LOGGER.debug("Try to minimize window. ID: {}", windowId);

        String response = runCliCommandOrNull(List.of("minimize-window", String.valueOf(windowId)));

        if(response == null) {
            LOGGER.warn("Unable to minimize window.");

            return false;
        }

        return true;
    }
}
