package dunos.ui;

import dunos.core.*;
import dunos.apps.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * DunDunDunOS Desktop - The main desktop environment.
 * Manages wallpaper, desktop icons, right-click context menu,
 * virtual desktops, and serves as the parent container for all windows.
 */
public class Desktop extends JPanel {

    private final JPanel iconPanel;
    private final JPanel windowLayer;
    private final JLabel wallpaperLabel;
    private final Taskbar taskbar;
    private final List<DesktopIcon> desktopIcons;
    private final Map<Integer, JPanel> virtualDesktops;
    private int currentDesktop;
    private JPopupMenu desktopContextMenu;
    private String currentWallpaper;

    /**
     * Creates the desktop environment.
     */
    public Desktop() {
        setLayout(new OverlayLayout(this));
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        this.desktopIcons = new ArrayList<>();
        this.virtualDesktops = new HashMap<>();
        this.currentDesktop = 0;

        // Wallpaper layer
        wallpaperLabel = new JLabel();
        wallpaperLabel.setLayout(new BorderLayout());
        add(wallpaperLabel);

        // Icon layer
        iconPanel = new JPanel(null);
        iconPanel.setOpaque(false);
        add(iconPanel);

        // Window layer
        windowLayer = new JPanel(null);
        windowLayer.setOpaque(false);
        add(windowLayer);

        // Set desktop pane for WindowManager
        WindowManager.getInstance().setDesktopPane(windowLayer);

        // Taskbar
        taskbar = new Taskbar(this);
        add(taskbar);

        // Setup context menu
        setupContextMenu();

        // Load wallpaper
        loadWallpaper();

        // Create desktop icons
        createDesktopIcons();

        // Theme listener
        ThemeManager.getInstance().addListener((oldTheme, newTheme) -> {
            revalidate();
            repaint();
        });
    }

    /**
     * Gets the taskbar.
     */
    public Taskbar getTaskbar() {
        return taskbar;
    }

    /**
     * Gets the window layer panel.
     */
    public JPanel getWindowLayer() {
        return windowLayer;
    }

    /**
     * Gets the current wallpaper path.
     */
    public String getCurrentWallpaper() {
        return currentWallpaper;
    }

    /**
     * Gets all desktop icons.
     */
    public List<DesktopIcon> getDesktopIcons() {
        return desktopIcons;
    }

    /**
     * Loads and sets the desktop wallpaper.
     */
    public void loadWallpaper() {
        String wallpaperPath = Registry.getInstance().getString("desktop.wallpaper", "default");
        setWallpaper(wallpaperPath);
    }

    /**
     * Sets the desktop wallpaper.
     */
    public void setWallpaper(String path) {
        ImageIcon wallpaper = null;

        if (path != null && !path.equals("default") && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                wallpaper = new ImageIcon(path);
            }
        }

        // Try default wallpapers
        if (wallpaper == null) {
            File defaultWallpaper = new File("Images/Wallpapers/default.jpg");
            if (defaultWallpaper.exists()) {
                wallpaper = new ImageIcon(defaultWallpaper.getAbsolutePath());
            }
        }

        if (wallpaper != null) {
            Image img = wallpaper.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
            );
            wallpaperLabel.setIcon(new ImageIcon(img));
            currentWallpaper = path;
        } else {
            // Solid color fallback
            wallpaperLabel.setIcon(null);
            wallpaperLabel.setBackground(new Color(18, 18, 28));
            wallpaperLabel.setOpaque(true);
        }

        Registry.getInstance().set("desktop.wallpaper", path);
    }

    /**
     * Creates default desktop icons for applications.
     */
    private void createDesktopIcons() {
        String[][] defaultIcons = {
            {"📁", "File Explorer", "explorer"},
            {"🌐", "Browser", "browser"},
            {"⚙", "Settings", "settings"},
            {"🗑", "Recycle Bin", "recycle"},
            {"💻", "Terminal", "terminal"},
            {"📝", "Notepad", "notepad"},
            {"🧮", "Calculator", "calculator"}
        };

        int x = 20;
        int y = 20;

        for (String[] icon : defaultIcons) {
            DesktopIcon di = new DesktopIcon(icon[0], icon[1], icon[2]);
            di.setBounds(x, y, 80, 90);
            desktopIcons.add(di);
            iconPanel.add(di);
            y += 100;
            if (y + 100 > getHeight() - 100) {
                y = 20;
                x += 90;
            }
        }
    }

    /**
     * Sets up the desktop right-click context menu.
     */
    private void setupContextMenu() {
        desktopContextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View");
        JMenuItem sortByItem = new JMenuItem("Sort by");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem newItem = new JMenuItem("New");

        JMenu newMenu = new JMenu("New");
        newMenu.add(new JMenuItem("Folder"));
        newMenu.add(new JMenuItem("Text Document"));
        newMenu.add(new JMenuItem("Rich Text Document"));

        JMenu viewMenu = new JMenu("View");
        viewMenu.add(new JCheckBoxMenuItem("Large icons"));
        viewMenu.add(new JCheckBoxMenuItem("Medium icons"));
        viewMenu.add(new JCheckBoxMenuItem("Small icons"));
        viewMenu.add(new JCheckBoxMenuItem("Auto arrange icons"));

        JMenu sortMenu = new JMenu("Sort by");
        sortMenu.add(new JMenuItem("Name"));
        sortMenu.add(new JMenuItem("Size"));
        sortMenu.add(new JMenuItem("Item type"));
        sortMenu.add(new JMenuItem("Date modified"));

        desktopContextMenu.add(viewMenu);
        desktopContextMenu.add(sortMenu);
        desktopContextMenu.add(refreshItem);
        desktopContextMenu.addSeparator();
        desktopContextMenu.add(pasteItem);
        desktopContextMenu.add(newMenu);
        desktopContextMenu.addSeparator();
        desktopContextMenu.add(new JMenuItem("Display settings"));
        desktopContextMenu.add(new JMenuItem("Personalize"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
        });
    }

    /**
     * Shows the desktop context menu at the given mouse position.
     */
    public void showContextMenu(MouseEvent e) {
        desktopContextMenu.show(this, e.getX(), e.getY());
    }

    /**
     * Switches to a virtual desktop.
     */
    public void switchVirtualDesktop(int index) {
        // Hide all desktops and show the selected one
        if (index != currentDesktop && index >= 0 && index < virtualDesktops.size()) {
            virtualDesktops.get(currentDesktop).setVisible(false);
            currentDesktop = index;
            virtualDesktops.get(currentDesktop).setVisible(true);
            revalidate();
            repaint();
        }
    }

    /**
     * Adds a new virtual desktop.
     */
    public void addVirtualDesktop() {
        int id = virtualDesktops.size();
        JPanel vDesktop = new JPanel(new BorderLayout());
        vDesktop.setOpaque(false);
        virtualDesktops.put(id, vDesktop);
        add(vDesktop);
        vDesktop.setVisible(id == currentDesktop);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * DesktopIcon - Represents a shortcut icon on the desktop.
     */
    public static class DesktopIcon extends JPanel {
        private final String emoji;
        private final String label;
        private final String appId;
        private boolean dragging;
        private Point dragOffset;

        public DesktopIcon(String emoji, String label, String appId) {
            this.emoji = emoji;
            this.label = label;
            this.appId = appId;
            this.dragging = false;

            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Icon label
            JLabel iconLabel = new JLabel(emoji, SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
            iconLabel.setForeground(Color.WHITE);

            // Text label
            JLabel textLabel = new JLabel(label, SwingConstants.CENTER);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            textLabel.setForeground(Color.WHITE);
            textLabel.setBorder(new EmptyBorder(2, 4, 2, 4));

            add(iconLabel, BorderLayout.CENTER);
            add(textLabel, BorderLayout.SOUTH);

            // Selection highlight
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    setBackground(new Color(0, 120, 212, 80));
                    setOpaque(true);
                    dragOffset = e.getPoint();
                    dragging = true;
                    repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    setOpaque(false);
                    dragging = false;
                    repaint();
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        launchApp();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragging) {
                        Point parentP = SwingUtilities.convertPoint(DesktopIcon.this, e.getPoint(), getParent());
                        setLocation(parentP.x - dragOffset.x, parentP.y - dragOffset.y);
                    }
                }
            });
        }

        /**
         * Launches the application associated with this icon.
         */
        public void launchApp() {
            switch (appId) {
                case "explorer" -> AppLauncher.launch("explorer");
                case "browser" -> AppLauncher.launch("browser");
                case "settings" -> AppLauncher.launch("settings");
                case "terminal" -> AppLauncher.launch("terminal");
                case "notepad" -> AppLauncher.launch("notepad");
                case "calculator" -> AppLauncher.launch("calculator");
                case "recycle" -> AppLauncher.launch("recycle");
                default -> {}
            }
        }

        public String getAppId() { return appId; }

        @Override
        protected void paintComponent(Graphics g) {
            if (isOpaque()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
            }
        }
    }

    /**
     * AppLauncher - Static helper to launch applications.
     */
    public static class AppLauncher {
        private static final Map<String, Window> openApps = new HashMap<>();

        /**
         * Launches an application by ID.
         */
        public static void launch(String appId) {
            SwingUtilities.invokeLater(() -> {
                Window window = createAppWindow(appId);
                if (window != null) {
                    WindowManager.getInstance().openWindow(window);
                    AnimationEngine.getInstance().fadeIn(window, 200);
                }
            });
        }

        private static Window createAppWindow(String appId) {
            return switch (appId) {
                case "calculator" -> new CalculatorApp().createWindow();
                case "browser" -> new BrowserApp().createWindow();
                case "explorer" -> new ExplorerApp().createWindow();
                case "paint" -> new PaintApp().createWindow();
                case "terminal" -> new TerminalApp().createWindow();
                case "settings" -> new SettingsApp().createWindow();
                case "notes" -> new NotesApp().createWindow();
                case "clock" -> new ClockApp().createWindow();
                case "mediaplayer" -> new MediaPlayerApp().createWindow();
                case "taskmanager" -> new TaskManagerApp().createWindow();
                case "calendar" -> new CalendarApp().createWindow();
                case "texteditor" -> new TextEditorApp().createWindow();
                case "stickynotes" -> new StickyNotesApp().createWindow();
                case "weather" -> new WeatherApp().createWindow();
                case "systeminfo" -> new SystemInfoApp().createWindow();
                case "appstore" -> new AppStoreApp().createWindow();
                case "charactermap" -> new CharacterMapApp().createWindow();
                case "diskcleaner" -> new DiskCleanerApp().createWindow();
                case "networkcenter" -> new NetworkCenterApp().createWindow();
                case "notepad" -> new TextEditorApp().createWindow();
                case "recycle" -> new ExplorerApp().createWindow();
                default -> null;
            };
        }

        /**
         * Closes an application.
         */
        public static void close(String appId) {
            Window window = openApps.remove(appId);
            if (window != null) {
                WindowManager.getInstance().closeWindow(window);
            }
        }
    }
}

