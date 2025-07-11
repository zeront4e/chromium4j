package io.github.zeront4e.c4j;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.platform.unix.X11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class WindowManagerLinuxUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowManagerLinuxUtil.class);

    private record X11Data(X11 x11Instance, X11.Display x11Display, X11.Window rootWindow) {
        //Ignore...
    }

    private static final boolean IGNORE_WINDOWS_WITHOUT_TITLE = true;

    private static final int ICONIC_STATE = 3; // X11 convention for minimized

    private static X11Data x11DataInstance = null;

    private synchronized static X11Data getX11DataOrFail() throws Exception {
        try {
            if(x11DataInstance == null) {
                X11 x11DataInstance = X11.INSTANCE;
                X11.Display dataInstance = x11DataInstance.XOpenDisplay(null);
                X11.Window rootWindow = x11DataInstance.XDefaultRootWindow(dataInstance);

                WindowManagerLinuxUtil.x11DataInstance = new X11Data(x11DataInstance, dataInstance, rootWindow);
            }
        }
        catch (Throwable throwable) {
            LOGGER.error("Unable to create X11 data instance.", throwable);

            throw new Exception("Unable to create X11 data instance.", throwable);
        }

        return x11DataInstance;
    }

    private static void cleanup() {
        try {
            if (x11DataInstance != null && x11DataInstance.x11Instance() != null) {
                x11DataInstance.x11Instance().XCloseDisplay(x11DataInstance.x11Display());
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Error cleaning up X11 data instance.", exception);
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(WindowManagerLinuxUtil::cleanup));
    }

    /**
     * Get window information recursively.
     * @return List of window information.
     * @throws Exception An unexpected exception.
     */
    public static List<WindowManagerWindow> getWindows() throws Exception {
        X11Data x11Data = getX11DataOrFail();

        return getRecurseWindowInfos(x11Data.x11Instance(), x11Data.x11Display(), x11Data.rootWindow(), 0);
    }

    /**
     * Adds or removes the taskbar icon of the specified window.
     * @param windowInfo The window to change the taskbar icon for.
     * @param setVisible Whether to make the window visible or not.
     * @throws Exception An unexpected exception.
     */
    public static void changeTaskbarVisibility(WindowManagerWindow windowInfo, boolean setVisible) throws Exception {
        changeTaskbarVisibility(new X11.Window(windowInfo.id()), setVisible);
    }

    /**
     * Minimizes the specified window.
     * @param windowInfo The window to minimize.
     * @throws Exception An unexpected exception.
     */
    public static void minimizeWindow(WindowManagerWindow windowInfo) throws Exception {
        minimizeWindow(new X11.Window(windowInfo.id()));
    }

    /**
     * Returns true if X11 is available, false otherwise.
     * @return True if X11 is available, false otherwise.
     */
    public static boolean isX11Available() {
        X11.Display display = null;

        try {
            display = X11.INSTANCE.XOpenDisplay(null);

            return display != null;
        }
        catch (Throwable throwable) {
            LOGGER.warn("Unable to open X11 display.", throwable);

            return false;
        }
        finally {
            try {
                if (display != null)
                    X11.INSTANCE.XCloseDisplay(display);
            }
            catch (Throwable throwable) {
                //Ignore...
            }
        }
    }

    private static List<WindowManagerWindow> getRecurseWindowInfos(X11 x11, X11.Display display, X11.Window rootWindow,
                                                          int depth) throws Exception {
        List<WindowManagerWindow> windowInfos = new ArrayList<>();

        getRecurseWindowInfos(windowInfos, x11, display, rootWindow, depth + 1);

        return windowInfos;
    }

    private static void getRecurseWindowInfos(List<WindowManagerWindow> windowInfos, X11 x11, X11.Display display,
                                              X11.Window rootWindow, int depth) throws Exception {
        X11.WindowByReference windowRef = new X11.WindowByReference();
        X11.WindowByReference parentRef = new X11.WindowByReference();
        PointerByReference childrenRef = new PointerByReference();
        IntByReference childCountRef = new IntByReference();

        x11.XQueryTree(display, rootWindow, windowRef, parentRef, childrenRef, childCountRef);

        if (childrenRef.getValue() == null) {
            return;
        }

        long[] ids;

        try {
            if (Native.LONG_SIZE == Long.BYTES) {
                ids = childrenRef.getValue().getLongArray(0, childCountRef.getValue());
            }
            else if (Native.LONG_SIZE == Integer.BYTES) {
                int[] intIds = childrenRef.getValue().getIntArray(0, childCountRef.getValue());

                ids = new long[intIds.length];

                for (int tmpId = 0; tmpId < intIds.length; tmpId++) {
                    ids[tmpId] = intIds[tmpId];
                }
            }
            else {
                throw new Exception("Unexpected size for Native.LONG_SIZE" + Native.LONG_SIZE);
            }

            for (long tmpId : ids) {
                if (tmpId == 0) {
                    continue;
                }

                X11.Window window = new X11.Window(tmpId);
                X11.XTextProperty name = new X11.XTextProperty();

                x11.XGetWMName(display, window, name);
                x11.XFree(name.getPointer());

                String windowTitle = name.value != null ? name.value : "null";

                if(!IGNORE_WINDOWS_WITHOUT_TITLE || name.value != null) {
                    windowInfos.add(new WindowManagerWindow(tmpId, windowTitle));
                }

                getRecurseWindowInfos(windowInfos, x11, display, window, depth + 1);
            }
        }
        finally {
            if (childrenRef.getValue() != null) {
                x11.XFree(childrenRef.getValue());
            }
        }
    }

    private static void changeTaskbarVisibility(X11.Window window, boolean setVisible) throws Exception {
        X11Data x11Data = getX11DataOrFail();

        String atomName = "_NET_WM_STATE_SKIP_TASKBAR";

        X11.Atom skipTaskbarAtom = x11Data.x11Instance().XInternAtom(x11Data.x11Display(), atomName, false);

        changeWindowState(window, skipTaskbarAtom, setVisible ? 0 : 1);
    }

    private static void changeWindowState(X11.Window window, X11.Atom stateAtom, int action) throws Exception {
        X11Data x11Data = getX11DataOrFail();

        X11.Atom netWmState = x11Data.x11Instance().XInternAtom(x11Data.x11Display(), "_NET_WM_STATE",
                false);

        X11.XClientMessageEvent clientMessage = new X11.XClientMessageEvent();
        clientMessage.type = X11.ClientMessage;
        clientMessage.display = x11Data.x11Display();
        clientMessage.window = window;
        clientMessage.message_type = netWmState;
        clientMessage.format = 32;

        clientMessage.data.setType(NativeLong[].class);
        clientMessage.data.l[0] = new NativeLong(action);
        clientMessage.data.l[1] = new NativeLong(stateAtom.longValue());
        clientMessage.data.l[2] = new NativeLong(0);
        clientMessage.data.l[3] = new NativeLong(0);
        clientMessage.data.l[4] = new NativeLong(0);

        X11.XEvent event = new X11.XEvent();
        event.setTypedValue(clientMessage);

        x11Data.x11Instance().XSendEvent(
                x11Data.x11Display(),
                x11Data.x11Instance().XDefaultRootWindow(x11Data.x11Display()),
                0,
                new NativeLong(X11.SubstructureNotifyMask | X11.SubstructureRedirectMask),
                event
        );

        x11Data.x11Instance().XFlush(x11Data.x11Display());
    }

    private static void minimizeWindow(X11.Window window) throws Exception {
        X11Data x11Data = getX11DataOrFail();

        //Create and initialize the XEvent structure for the window state change.
        X11.XEvent event = new X11.XEvent();

        //Set up the client message event.
        X11.XClientMessageEvent xClientMessageEvent = new X11.XClientMessageEvent();
        xClientMessageEvent.type = X11.ClientMessage;
        xClientMessageEvent.display = x11Data.x11Display();
        xClientMessageEvent.window = window;
        xClientMessageEvent.message_type = x11Data.x11Instance().XInternAtom(x11Data.x11Display(),
                "WM_CHANGE_STATE", false);
        xClientMessageEvent.format = 32;
        xClientMessageEvent.data.setType(NativeLong[].class);
        xClientMessageEvent.data.l[0] = new NativeLong(ICONIC_STATE); // IconicState = 3, for minimized windows

        //Set the event to the client message.
        event.setTypedValue(xClientMessageEvent);

        //Send the event to the root window.
        x11Data.x11Instance().XSendEvent(
                x11Data.x11Display(),
                x11Data.x11Instance.XDefaultRootWindow(x11Data.x11Display()),
                0,
                new NativeLong(X11.SubstructureRedirectMask | X11.SubstructureNotifyMask),
                event
        );

        //Flush the display to ensure the command is processed.
        x11Data.x11Instance().XFlush(x11Data.x11Display());
    }
}
