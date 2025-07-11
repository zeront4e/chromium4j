package io.github.zeront4e.c4j;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.win32.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class WindowManagerWindowsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowManagerWindowsUtil.class);

    private static final boolean IGNORE_WINDOWS_WITHOUT_TITLE = true;

    private static final int SW_MINIMIZE = 6;
    private static final int SW_RESTORE = 9;
    private static final int GWL_EXSTYLE = -20;
    private static final int WS_EX_TOOLWINDOW = 0x00000080; // Hide from taskbar

    private interface User32Ex extends StdCallLibrary {
        boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer userData);
        int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);
        int GetWindowTextLengthA(HWND hWnd);
        boolean IsWindowVisible(HWND hWnd);
        HWND GetShellWindow();
        boolean ShowWindow(HWND hWnd, int nCmdShow);
        long GetWindowLongA(HWND hWnd, int nIndex);
        long SetWindowLongA(HWND hWnd, int nIndex, long dwNewLong);
    }

    private static User32Ex user32Cache = null;

    private synchronized static User32Ex getUser32Instance() throws Exception {
        try {
            if(user32Cache == null)
                user32Cache = Native.load("user32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS);
        }
        catch (Throwable throwable) {
            throw new Exception("Failed to load user32.dll", throwable);
        }

        return user32Cache;
    }

    /**
     * Get window information recursively.
     * @return List of window information.
     * @throws Exception An unexpected exception.
     */
    public static List<WindowManagerWindow> getWindows() throws Exception {
        List<WindowManagerWindow> windowInfos = new ArrayList<>();

        User32Ex user32Ex = getUser32Instance();

        HWND shellWindow = user32Ex.GetShellWindow();

        user32Ex.EnumWindows((hWnd, data) -> {
            try {
                //Skip invisible windows and the shell window.

                if (!user32Ex.IsWindowVisible(hWnd) || hWnd.equals(shellWindow))
                    return true;

                //Get window title.
                int length = user32Ex.GetWindowTextLengthA(hWnd);

                if (length == 0 && IGNORE_WINDOWS_WITHOUT_TITLE)
                    return true;

                byte[] buffer = new byte[length + 1];

                user32Ex.GetWindowTextA(hWnd, buffer, buffer.length);

                String title = Native.toString(buffer);

                if (!IGNORE_WINDOWS_WITHOUT_TITLE || !title.isEmpty())
                    windowInfos.add(new WindowManagerWindow(Pointer.nativeValue(hWnd.getPointer()), title));

                return true;
            }
            catch (Exception exception) {
                LOGGER.error("Failed to get window title.", exception);

                return true;
            }
        }, null);

        return windowInfos;
    }

    /**
     * Adds or removes the taskbar icon of the specified window.
     * @param windowInfo The window to change the taskbar icon for.
     * @param setVisible Whether to make the window visible or not.
     * @throws Exception An unexpected exception.
     */
    public static void changeTaskbarVisibility(WindowManagerWindow windowInfo, boolean setVisible) throws Exception {
        HWND hWnd = new HWND(Pointer.createConstant(windowInfo.id()));

        User32Ex user32Ex = getUser32Instance();

        long style = user32Ex.GetWindowLongA(hWnd, GWL_EXSTYLE);

        if (setVisible) {
            //Remove WS_EX_TOOLWINDOW flag to show the window in the taskbar.
            style &= ~WS_EX_TOOLWINDOW;
        } else {
            //Add WS_EX_TOOLWINDOW flag to hide the window from the taskbar
            style |= WS_EX_TOOLWINDOW;
        }

        user32Ex.SetWindowLongA(hWnd, GWL_EXSTYLE, style);

        //Force window to update.
        user32Ex.ShowWindow(hWnd, SW_RESTORE);
    }

    /**
     * Minimizes the specified window.
     * @param windowInfo The window to minimize.
     * @throws Exception An unexpected exception.
     */
    public static void minimizeWindow(WindowManagerWindow windowInfo) throws Exception {
        changeWindowVisibility(windowInfo, false);
    }

    /**
     * Minimizes the specified window or makes it visible again.
     * @param windowInfo The window to hide/show.
     * @param setVisible Whether to make the window visible or not.
     * @throws Exception An unexpected exception.
     */
    public static void changeWindowVisibility(WindowManagerWindow windowInfo, boolean setVisible) throws Exception {
        HWND hWnd = new HWND(Pointer.createConstant(windowInfo.id()));

        getUser32Instance().ShowWindow(hWnd, setVisible ? SW_RESTORE : SW_MINIMIZE);
    }
}