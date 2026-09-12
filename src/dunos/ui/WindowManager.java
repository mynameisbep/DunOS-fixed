package dunos.ui;

import dunos.core.EventBus;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * DunDunDunOS WindowManager - Manages all application windows.
 * Handles z-ordering, focus, snap layouts, resize, minimize/maximize,
 * and window lifecycle. Provides a Windows 11-style window management experience.
 */
public class WindowManager {

    private static volatile WindowManager instance;
    private final List<Window> windows;
    private final List<Window> minimizedWindows;
    private Window activeWindow;
    private JPanel desktopPane;
    private int nextX = 100, nextY = 100;
    private static final int WINDOW_OFFSET = 30;

    private WindowManager() {
        this.windows = new ArrayList<>();
        this.minimizedWindows = new ArrayList<>();
    }

    /**
     * Gets the singleton WindowManager instance.
     */
    public static WindowManager getInstance() {
        if (instance == null) {
            synchronized (WindowManager.class) {
                if (instance == null) {
                    instance = new WindowManager();
                }
            }
        }
        return instance;
    }

    /**
     * Sets the desktop panel that will host all windows.
     */
    public void setDesktopPane(JPanel pane) {
        this.desktopPane = pane;
    }

    /**
     * Opens a new window and adds it to the desktop.
     */
    public void openWindow(Window window) {
        if (desktopPane == null) {
            System.err.println("[WindowManager] No desktop pane set!");
            return;
        }

        windows.add(window);
        window.setVisible(true);

        // Cascade window positions
        if (nextX + window.getWidth() > desktopPane.getWidth()) {
            nextX = 100;
            nextY += WINDOW_OFFSET;
        }
        if (nextY + window.getHeight() > desktopPane.getHeight()) {
            nextY = 100;
        }

        window.setLocation(nextX, nextY);
        nextX += WINDOW_OFFSET;
        nextY += WINDOW_OFFSET;

        desktopPane.add(window);
        desktopPane.setComponentZOrder(window, 0);
        bringToFront(window);

        EventBus.getInstance().emitAsync(WindowEvent.class, 
            new WindowEvent(window, "opened"), "WindowManager");
    }

    /**
     * Closes a window and removes it from the desktop.
     */
    public void closeWindow(Window window) {
        windows.remove(window);
        minimizedWindows.remove(window);
        desktopPane.remove(window);
        desktopPane.repaint();

        if (activeWindow == window) {
            activeWindow = windows.isEmpty() ? null : windows.get(0);
            if (activeWindow != null) bringToFront(activeWindow);
        }

        EventBus.getInstance().emitAsync(WindowEvent.class,
            new WindowEvent(window, "closed"), "WindowManager");
    }

    /**
     * Brings a window to the front (top of z-order).
     */
    public void bringToFront(Window window) {
        if (activeWindow == window) return;

        if (desktopPane != null && windows.contains(window)) {
            desktopPane.setComponentZOrder(window, 0);
            window.repaint();

            if (activeWindow != null) {
                // Dim the previous active window's title bar
                activeWindow.getTitleBar().setBackground(
                    activeWindow.getTitleBar().getBackground().darker());
            }

            activeWindow = window;

            // Highlight the new active window's title bar
            activeWindow.getTitleBar().setBackground(
                ThemeManager.getInstance().getCurrentTheme().getSurface());

            desktopPane.repaint();
        }
    }

    /**
     * Minimizes a window.
     */
    public void minimizeWindow(Window window) {
        minimizedWindows.add(window);
        window.setMinimized(true);
        window.setVisible(false);

        if (activeWindow == window) {
            // Focus next available window
            for (Window w : windows) {
                if (!w.isMinimized() && w != window) {
                    bringToFront(w);
                    return;
                }
            }
            activeWindow = null;
        }
    }

    /**
     * Restores a minimized window.
     */
    public void restoreWindow(Window window) {
        minimizedWindows.remove(window);
        window.setMinimized(false);
        window.setVisible(true);
        bringToFront(window);
    }

    /**
     * Maximizes a window to fill the desktop.
     */
    public void maximizeWindow(Window window) {
        if (desktopPane != null) {
            Insets insets = desktopPane.getInsets();
            // Account for taskbar height (will be set dynamically)
            int taskbarHeight = getTaskbarHeight();
            window.setBounds(insets.left, insets.top,
                desktopPane.getWidth() - insets.left - insets.right,
                desktopPane.getHeight() - insets.top - insets.bottom - taskbarHeight);
        }
    }

    /**
     * Restores a maximized window to its normal bounds.
     */
    public void restoreMaximizedWindow(Window window) {
        // The window itself stores normalBounds
    }

    /**
     * Snaps a window to a position on the screen.
     */
    public void snapWindow(Window window, Window.SnapPosition position) {
        if (desktopPane == null) return;

        int taskbarHeight = getTaskbarHeight();
        int w = desktopPane.getWidth();
        int h = desktopPane.getHeight() - taskbarHeight;

        Rectangle bounds = switch (position) {
            case LEFT -> new Rectangle(0, 0, w / 2, h);
            case RIGHT -> new Rectangle(w / 2, 0, w / 2, h);
            case TOP_LEFT -> new Rectangle(0, 0, w / 2, h / 2);
            case TOP_RIGHT -> new Rectangle(w / 2, 0, w / 2, h / 2);
            case BOTTOM_LEFT -> new Rectangle(0, h / 2, w / 2, h / 2);
            case BOTTOM_RIGHT -> new Rectangle(w / 2, h / 2, w / 2, h / 2);
            case FULLSCREEN -> new Rectangle(0, 0, w, h);
            default -> window.getBounds();
        };

        window.setBounds(bounds);
        window.snapPosition = position;
    }

    /**
     * Gets the currently active (focused) window.
     */
    public Window getActiveWindow() {
        return activeWindow;
    }

    /**
     * Gets all open windows.
     */
    public List<Window> getWindows() {
        return new ArrayList<>(windows);
    }

    /**
     * Gets all minimized windows.
     */
    public List<Window> getMinimizedWindows() {
        return new ArrayList<>(minimizedWindows);
    }

    /**
     * Gets windows of a specific application type.
     */
    public List<Window> getWindowsByAppId(String appId) {
        List<Window> result = new ArrayList<>();
        for (Window w : windows) {
            if (w.getAppId() != null && w.getAppId().equalsIgnoreCase(appId)) {
                result.add(w);
            }
        }
        return result;
    }

    /**
     * Returns the count of open windows.
     */
    public int getWindowCount() {
        return windows.size();
    }

    /**
     * Closes all windows.
     */
    public void closeAllWindows() {
        List<Window> copy = new ArrayList<>(windows);
        for (Window w : copy) {
            closeWindow(w);
        }
    }

    /**
     * Updates all windows when the theme changes.
     */
    public void updateAllThemes(Theme theme) {
        for (Window w : windows) {
            w.updateTheme(theme);
        }
    }

    // Notification callbacks from Window
    public void notifyClose(Window window) { closeWindow(window); }
    public void notifyMinimize(Window window) { minimizeWindow(window); }
    public void notifyMaximize(Window window) { maximizeWindow(window); }
    public void notifyRestore(Window window) { restoreWindow(window); }
    public void notifyResize(Window window, MouseEvent e) {
        // Resize logic handled in Window class
    }

    private int getTaskbarHeight() {
        return 48; // Default taskbar height
    }

    /**
     * Event class for window-related events.
     */
    public static class WindowEvent {
        private final Window window;
        private final String type;

        public WindowEvent(Window window, String type) {
            this.window = window;
            this.type = type;
        }

        public Window getWindow() { return window; }
        public String getType() { return type; }
    }
}

