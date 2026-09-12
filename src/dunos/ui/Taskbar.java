package dunos.ui;

import dunos.core.Registry;
import dunos.ui.Desktop;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * DunDunDunOS Taskbar - Windows 11 inspired taskbar with centered icons,
 * start button, pinned/running apps, system tray (clock, battery, network, volume),
 * search, and notification center.
 */
public class Taskbar extends JPanel {

    private final Desktop desktop;
    private final JPanel startButton;
    private JPanel taskbarItems;
    private final JPanel systemTray;
    private final JLabel clockLabel;
    private final JLabel batteryLabel;
    private final JLabel networkLabel;
    private final JLabel volumeLabel;
    private final JLabel notificationLabel;
    private JButton searchButton;
    private final StartMenu startMenu;
    private final Timer clockTimer;
    private final List<TaskbarButton> pinnedApps;
    private final List<TaskbarButton> runningApps;
    private final JPanel runningAppsPanel;

    private static final int TASKBAR_HEIGHT = 48;
    private static final Color TASKBAR_COLOR = new Color(28, 28, 28, 235);
    private static final Color TASKBAR_HOVER = new Color(60, 60, 60, 150);
    private static final Color TASKBAR_ACTIVE = new Color(0, 120, 212, 100);
    private static final Color TEXT_COLOR = new Color(235, 235, 235);

    /**
     * Creates the taskbar.
     */
    public Taskbar(Desktop desktop) {
        this.desktop = desktop;
        this.pinnedApps = new ArrayList<>();
        this.runningApps = new ArrayList<>();
        this.taskbarItems = new JPanel();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, TASKBAR_HEIGHT));
        setBackground(TASKBAR_COLOR);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 50)));

        // Start button
        startButton = createStartButton();
        add(startButton, BorderLayout.WEST);

        // Search button
        searchButton = createSearchButton();
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(startButton);
        leftPanel.add(searchButton);
        add(leftPanel, BorderLayout.WEST);

        // Center - Pinned and running apps
        runningAppsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 4));
        runningAppsPanel.setOpaque(false);

        // Add pinned apps
        addPinnedApps();

        add(runningAppsPanel, BorderLayout.CENTER);

        // System tray
        systemTray = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        systemTray.setOpaque(false);

        // Search bar (compact) - already created above, don't reassign final
        // systemTray already has search functionality via leftPanel

        batteryLabel = createTrayLabel("🔋 100%");
        networkLabel = createTrayLabel("📶");
        volumeLabel = createTrayLabel("🔊");
        notificationLabel = createTrayLabel("🔔");
        clockLabel = createTrayLabel(getCurrentTime());

        systemTray.add(networkLabel);
        systemTray.add(volumeLabel);
        systemTray.add(batteryLabel);
        systemTray.add(notificationLabel);
        systemTray.add(clockLabel);

        add(systemTray, BorderLayout.EAST);

        // Clock update timer
        clockTimer = new Timer(1000, e -> {
            clockLabel.setText(getCurrentTime());
        });
        clockTimer.start();

        // Start menu (popup)
        startMenu = new StartMenu(desktop);

        // Listen for window events to update running apps
        setupWindowListeners();
    }

    private JPanel createStartButton() {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel("🏠");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.add(icon);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleStartMenu();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(TASKBAR_HOVER);
                btn.setOpaque(true);
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
                btn.repaint();
            }
        });

        btn.setPreferredSize(new Dimension(48, TASKBAR_HEIGHT));
        return btn;
    }

    private JButton createSearchButton() {
        JButton btn = new JButton("🔍  Search");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(new Color(40, 40, 40));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(4, 10, 4, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        btn.setPreferredSize(new Dimension(120, 28));
        return btn;
    }

    private JLabel createTrayLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTrayClick(label);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setOpaque(true);
                label.setBackground(TASKBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                label.setOpaque(false);
            }
        });

        return label;
    }

    /**
     * Creates a taskbar button for an application.
     */
    private TaskbarButton createTaskbarButton(String emoji, String tooltip, String appId) {
        return new TaskbarButton(emoji, tooltip, appId);
    }

    /**
     * Adds pinned application buttons to the taskbar.
     */
    private void addPinnedApps() {
        String[][] apps = {
            {"📁", "File Explorer", "explorer"},
            {"🌐", "Browser", "browser"},
            {"⚙", "Settings", "settings"},
            {"🧮", "Calculator", "calculator"},
            {"🎵", "Music", "mediaplayer"},
            {"🖌", "Paint", "paint"},
            {"📝", "Notes", "notes"},
            {"💻", "Terminal", "terminal"},
            {"📊", "Task Manager", "taskmanager"}
        };

        for (String[] app : apps) {
            TaskbarButton btn = createTaskbarButton(app[0], app[1], app[2]);
            pinnedApps.add(btn);
            runningAppsPanel.add(btn);
        }
    }

    /**
     * Adds a running application to the taskbar.
     */
    public void addRunningApp(String appId, String emoji, String title) {
        TaskbarButton btn = createTaskbarButton(emoji, title, appId);
        btn.setRunning(true);
        runningApps.add(btn);
        runningAppsPanel.add(btn);
        runningAppsPanel.revalidate();
        runningAppsPanel.repaint();
    }

    /**
     * Removes a running application from the taskbar.
     */
    public void removeRunningApp(String appId) {
        runningApps.removeIf(btn -> btn.getAppId().equals(appId));
        runningAppsPanel.revalidate();
        runningAppsPanel.repaint();
    }

    /**
     * Toggles the start menu visibility.
     */
    public void toggleStartMenu() {
        if (startMenu.isVisible()) {
            startMenu.hideMenu();
        } else {
            startMenu.showMenu(this);
        }
    }

    /**
     * Handles clicks on system tray icons.
     */
    private void handleTrayClick(JLabel label) {
        if (label == clockLabel) {
            // Show calendar/clock popup
            JOptionPane.showMessageDialog(this,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy\nHH:mm:ss")),
                "Date & Time", JOptionPane.PLAIN_MESSAGE);
        } else if (label == notificationLabel) {
            // Show notification center
            showNotificationCenter();
        }
    }

    /**
     * Shows the notification center popup.
     */
    private void showNotificationCenter() {
        // Placeholder for notification center panel
        JDialog nc = new JDialog();
        nc.setTitle("Notification Center");
        nc.setSize(300, 400);
        nc.setLocationRelativeTo(this);
        nc.setVisible(true);
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void setupWindowListeners() {
        // This would connect to EventBus for window events
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(TASKBAR_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    /**
     * TaskbarButton - A styled button for the taskbar representing an app.
     */
    public static class TaskbarButton extends JPanel {
        private final String emoji;
        private final String appId;
        private boolean running;

        public TaskbarButton(String emoji, String tooltip, String appId) {
            this.emoji = emoji;
            this.appId = appId;
            this.running = false;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(40, TASKBAR_HEIGHT - 8));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(tooltip);

            JLabel iconLabel = new JLabel(emoji, SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            add(iconLabel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Desktop.AppLauncher.launch(appId);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    setOpaque(true);
                    setBackground(TASKBAR_HOVER);
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setOpaque(false);
                    repaint();
                }
            });
        }

        public String getAppId() { return appId; }
        public void setRunning(boolean r) { this.running = r; }
        public boolean isRunning() { return running; }

        @Override
        protected void paintComponent(Graphics g) {
            if (isOpaque()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2d.dispose();
            }
            // Running indicator
            if (running) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 120, 212));
                g2d.fillRoundRect(getWidth()/2 - 1, getHeight() - 3, 4, 3, 2, 2);
                g2d.dispose();
            }
            super.paintComponent(g);
        }
    }
}

