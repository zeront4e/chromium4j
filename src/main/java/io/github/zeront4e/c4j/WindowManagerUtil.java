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

import java.util.List;

class WindowManagerUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowManagerUtil.class);

    /**
     * Returns the window instance or null.
     * @param partialWindowTitle The partial window title to match against.
     * @return The found window instance.
     * @throws Exception An unexpected exception.
     */
    public static WindowManagerWindow getWindowOrNull(String partialWindowTitle) throws Exception {
        LOGGER.debug("Searching for window with partial title: {}", partialWindowTitle);

        WindowManagerWindow windowManagerWindow = getWindows().stream()
                .filter(tmpWindow -> tmpWindow.title().contains(partialWindowTitle))
                .findFirst()
                .orElse(null);

        LOGGER.debug("Found window: {}", windowManagerWindow != null ? windowManagerWindow.title() : "null");

        return windowManagerWindow;
    }

    /**
     * Get window information recursively.
     * @return List of window information.
     * @throws Exception An unexpected exception.
     */
    public static List<WindowManagerWindow> getWindows() throws Exception {
        C4jOsArchitecture c4jOsArchitecture = C4jOsDetectionUtil.detectOsArchitecture();

        LOGGER.debug("Try to find available windows. Detected OS architecture: {}", c4jOsArchitecture);

        List<WindowManagerWindow> windowList = null;

        if(c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X86 || c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X64)
            windowList = WindowManagerWindowsUtil.getWindows();

        if(c4jOsArchitecture == C4jOsArchitecture.LINUX_X86 || c4jOsArchitecture == C4jOsArchitecture.LINUX_X64)
            windowList = WindowManagerLinuxUtil.getWindows();

        LOGGER.debug("Found windows: {}", windowList != null ? windowList.size() : 0);

        if(windowList != null)
            return windowList;

        throw new Exception("Unsupported OS architecture.");
    }

    /**
     * Adds or removes the taskbar icon of the specified window.
     * @param windowInfo The window to change the taskbar icon for.
     * @param setVisible Whether to make the window visible or not.
     * @throws Exception An unexpected exception.
     */
    public static void changeTaskbarVisibility(WindowManagerWindow windowInfo, boolean setVisible) throws Exception {
        C4jOsArchitecture c4jOsArchitecture = C4jOsDetectionUtil.detectOsArchitecture();

        if(c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X86 || c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X64) {
            LOGGER.debug("Changing taskbar visibility for Windows OS. New target window state: {}", setVisible);

            WindowManagerWindowsUtil.changeTaskbarVisibility(windowInfo, setVisible);

            return;
        }

        if(c4jOsArchitecture == C4jOsArchitecture.LINUX_X86 || c4jOsArchitecture == C4jOsArchitecture.LINUX_X64) {
            LOGGER.debug("Changing taskbar visibility for Linux OS. New target window state: {}", setVisible);

            WindowManagerLinuxUtil.changeTaskbarVisibility(windowInfo, setVisible);

            return;
        }

        throw new Exception("Unsupported OS architecture.");
    }

    /**
     * Minimizes the specified window.
     * @param windowInfo The window to minimize.
     * @throws Exception An unexpected exception.
     */
    public static void minimizeWindow(WindowManagerWindow windowInfo) throws Exception {
        C4jOsArchitecture c4jOsArchitecture = C4jOsDetectionUtil.detectOsArchitecture();

        LOGGER.debug("Minimizing window. OS architecture: {} Window title: {}", c4jOsArchitecture, windowInfo.title());

        if(c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X86 || c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X64) {
            WindowManagerWindowsUtil.minimizeWindow(windowInfo);

            return;
        }

        if(c4jOsArchitecture == C4jOsArchitecture.LINUX_X86 || c4jOsArchitecture == C4jOsArchitecture.LINUX_X64) {
            WindowManagerLinuxUtil.minimizeWindow(windowInfo);

            return;
        }

        throw new Exception("Unsupported OS architecture.");
    }
}
