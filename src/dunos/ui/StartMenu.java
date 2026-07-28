package dunos.ui;

import dunos.core.Kernel;
import dunos.core.Registry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * DunDunDunOS StartMenu - Windows 11 inspired start menu with search,
 * pinned apps, recently opened, user profile, and power options.
 * Animated and fully theme-aware.
 */
public class StartMenu extends JWindow {

    private final Desktop desktop;
    private final JPanel mainPanel;
    private final JTextField searchField;
    private final JPanel pinnedAppsPanel;
    private final JPanel recentAppsPanel;
    private final JPanel powerPanel;
    private final JLabel userLabel;
    private boolean visible;

    private static final Dimension MENU_SIZE = new Dimension(640, 560);
    private static final Color MENU_COLOR = new Color(32, 32, 32, 245);
    private static final Color SEARCH_COLOR = new Color(45, 45, 45);

    /**
     * Creates the start menu.
     */
    public StartMenu(Desktop desktop) {
        this.desktop = desktop;
        this.visible = false;

        setSize(MENU_SIZE);
        setBackground(new Color(0, 0, 0, 0));

        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(MENU_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- Top: User profile and search ---
        JPanel topPanel = new JPanel(new BorderLayout(8, 0));
        topPanel.setOpaque(false);

        // User profile
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        userPanel.setOpaque(false);
        userLabel = new JLabel("👤  " + Registry.getInstance().getString("user.name", "User"));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(235, 235, 235));
        userLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);
        topPanel.add(userPanel, BorderLayout.WEST);

        // Search field
        searchField = new JTextField("  🔍  Search applications, files, settings...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(new Color(160, 160, 160));
        searchField.setBackground(SEARCH_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setCaretColor(new Color(235, 235, 235));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("  🔍  Search applications, files, settings...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(235, 235, 235));
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("  🔍  Search applications, files, settings...");
                    searchField.setForeground(new Color(160, 160, 160));
                }
            }
        });
        topPanel.add(searchField, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Center: Pinned and recent apps ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        centerPanel.setOpaque(false);

        // Pinned apps
        JPanel pinnedContainer = new JPanel(new BorderLayout());
        pinnedContainer.setOpaque(false);
        JLabel pinnedLabel = new JLabel("Pinned");
        pinnedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pinnedLabel.setForeground(new Color(200, 200, 200));
        pinnedContainer.add(pinnedLabel, BorderLayout.NORTH);

        pinnedAppsPanel = new JPanel(new GridLayout(0, 3, 6, 6));
        pinnedAppsPanel.setOpaque(false);
        addPinnedApps();
        pinnedContainer.add(pinnedAppsPanel, BorderLayout.CENTER);
        centerPanel.add(pinnedContainer);

        // Recent apps
        JPanel recentContainer = new JPanel(new BorderLayout());
        recentContainer.setOpaque(false);
        JLabel recentLabel = new JLabel("Recently Added");
        recentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        recentLabel.setForeground(new Color(200, 200, 200));
        recentContainer.add(recentLabel, BorderLayout.NORTH);

        recentAppsPanel = new JPanel(new GridLayout(0, 3, 6, 6));
        recentAppsPanel.setOpaque(false);
        addRecentApps();
        recentContainer.add(recentAppsPanel, BorderLayout.CENTER);
        centerPanel.add(recentContainer);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom: Power options ---
        powerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        powerPanel.setOpaque(false);

        String[][] powerItems = {
            {"🛌", "Sleep", "sleep"},
            {"🔄", "Restart", "restart"},
            {"⏻", "Shut Down", "shutdown"}
        };

        for (String[] item : powerItems) {
            JButton btn = createPowerButton(item[0], item[1], item[2]);
            powerPanel.add(btn);
        }

        // Lock and sign out
        JPanel leftPowerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        leftPowerPanel.setOpaque(false);

        JButton lockBtn = createPowerButton("🔒", "Lock", "lock");
        JButton signOutBtn = createPowerButton("🚪", "Sign Out", "signout");
        leftPowerPanel.add(lockBtn);
        leftPowerPanel.add(signOutBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(leftPowerPanel, BorderLayout.WEST);
        bottomPanel.add(powerPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Close when clicking outside
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                hideMenu();
            }
        });
    }

    /**
     * Shows the start menu below the taskbar start button.
     */
    public void showMenu(Component relativeTo) {
        Point location = relativeTo.getLocationOnScreen();
        int x = location.x;
        int y = location.y - MENU_SIZE.height - 4;
        setLocation(x, y);
        setVisible(true);
        visible = true;
        searchField.requestFocus();

        AnimationEngine ae = AnimationEngine.getInstance();
        ae.fadeIn(this, 150);
    }

    /**
     * Hides the start menu.
     */
    public void hideMenu() {
        if (visible) {
            visible = false;
            setVisible(false);
        }
    }

    /**
     * Toggles the start menu visibility.
     */
    public boolean toggle(Component relativeTo) {
        if (visible) {
            hideMenu();
            return false;
        } else {
            showMenu(relativeTo);
            return true;
        }
    }

    /**
     * Adds pinned applications to the start menu.
     */
    private void addPinnedApps() {
        String[][] apps = {
            {"🌐", "Browser"},
            {"📁", "Explorer"},
            {"⚙", "Settings"},
            {"🧮", "Calculator"},
            {"📝", "Notes"},
            {"🖌", "Paint"},
            {"💻", "Terminal"},
            {"🎵", "Media Player"},
            {"📷", "Photos"},
            {"📊", "Task Manager"},
            {"📦", "App Store"},
            {"🕐", "Clock"}
        };

        for (String[] app : apps) {
            pinnedAppsPanel.add(createAppItem(app[0], app[1]));
        }
    }

    /**
     * Adds recently added applications.
     */
    private void addRecentApps() {
        String[][] apps = {
            {"📝", "Text Editor"},
            {"🗒", "Sticky Notes"},
            {"🌤", "Weather"},
            {"📅", "Calendar"},
            {"📡", "System Info"},
            {"🧹", "Disk Cleaner"}
        };

        for (String[] app : apps) {
            recentAppsPanel.add(createAppItem(app[0], app[1]));
        }
    }

    /**
     * Creates an app item panel for the start menu grid.
     */
    private JPanel createAppItem(String emoji, String name) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setBorder(new EmptyBorder(8, 4, 8, 4));

        JLabel iconLabel = new JLabel(emoji, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nameLabel.setForeground(new Color(200, 200, 200));

        panel.add(iconLabel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setOpaque(true);
                panel.setBackground(new Color(255, 255, 255, 20));
                panel.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setOpaque(false);
                panel.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                hideMenu();
                Desktop.AppLauncher.launch(name.toLowerCase().replace(" ", ""));
            }
        });

        return panel;
    }

    /**
     * Creates a power button for the bottom panel.
     */
    private JButton createPowerButton(String emoji, String text, String action) {
        JButton btn = new JButton(emoji + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(new Color(45, 45, 45));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> handlePowerAction(action));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(45, 45, 45));
            }
        });

        return btn;
    }

    /**
     * Handles power actions (shutdown, restart, sleep, lock, sign out).
     */
    private void handlePowerAction(String action) {
        Kernel kernel = Kernel.getInstance();
        hideMenu();

        switch (action) {
            case "shutdown" -> kernel.shutdown();
            case "restart" -> kernel.restart();
            case "sleep" -> kernel.sleep();
            case "lock" -> {/* Show lock screen */}
            case "signout" -> System.exit(0);
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}

