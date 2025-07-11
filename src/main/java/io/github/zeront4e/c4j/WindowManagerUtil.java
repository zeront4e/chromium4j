package io.github.zeront4e.c4j;

import java.util.List;

class WindowManagerUtil {
    /**
     * Returns the window instance or null.
     * @param partialWindowTitle The partial window title to match against.
     * @return The found window instance.
     * @throws Exception An unexpected exception.
     */
    public static WindowManagerWindow getWindowOrNull(String partialWindowTitle) throws Exception {
        return getWindows().stream()
                .filter(tmpWindow -> tmpWindow.title().contains(partialWindowTitle))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get window information recursively.
     * @return List of window information.
     * @throws Exception An unexpected exception.
     */
    public static List<WindowManagerWindow> getWindows() throws Exception {
        C4jOsArchitecture c4jOsArchitecture = C4jOsDetectionUtil.detectOsArchitecture();

        if(c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X86 || c4jOsArchitecture == C4jOsArchitecture.WINDOWS_X64)
            return WindowManagerWindowsUtil.getWindows();

        if(c4jOsArchitecture == C4jOsArchitecture.LINUX_X86 || c4jOsArchitecture == C4jOsArchitecture.LINUX_X64)
            return WindowManagerLinuxUtil.getWindows();

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
            WindowManagerWindowsUtil.changeTaskbarVisibility(windowInfo, setVisible);

            return;
        }

        if(c4jOsArchitecture == C4jOsArchitecture.LINUX_X86 || c4jOsArchitecture == C4jOsArchitecture.LINUX_X64) {
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
