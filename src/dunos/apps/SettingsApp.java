package dunos.apps;

import dunos.core.Registry;
import dunos.ui.*;
import dunos.ui.Window;
import dunos.ui.Desktop;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.*;

/**
 * DunDunDunOS Settings - Full settings panel with sections for personalization,
 * themes, wallpaper, system info, and more. Multi-tab interface.
 */
public class SettingsApp {

    private JPanel content;
    private JTabbedPane tabbedPane;

    public Window createWindow() {
        Window window = new Window("Settings", "settings", null);
        window.setSize(800, 550);
        window.setMinimizable(true);
        window.setMaximizable(false);

        content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel titleLabel = new JLabel("⚙ Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(235, 235, 235));
        header.add(titleLabel, BorderLayout.WEST);

        content.add(header, BorderLayout.NORTH);

        // Tabbed settings
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setForeground(new Color(200, 200, 200));
        tabbedPane.setBackground(new Color(30, 30, 30));
        tabbedPane.setBorder(new EmptyBorder(4, 4, 4, 4));

        tabbedPane.addTab("🎨 Personalization", createPersonalizationPanel());
        tabbedPane.addTab("🖥 Desktop", createDesktopPanel());
        tabbedPane.addTab("🔤 Themes", createThemesPanel());
        tabbedPane.addTab("ℹ System Info", createSystemInfoPanel());
        tabbedPane.addTab("🔔 Notifications", createNotificationsPanel());
        tabbedPane.addTab("📡 Network", createNetworkPanel());
        tabbedPane.addTab("📦 Storage", createStoragePanel());

        content.add(tabbedPane, BorderLayout.CENTER);

        return window;
    }

    private JPanel createPersonalizationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Dark Mode toggle
        JPanel darkModePanel = createSettingRow("🌙 Dark Mode", "Toggle dark/light theme", 
            new JToggleButton());
        panel.add(darkModePanel, gbc);

        // Accent color
        JPanel accentPanel = createSettingRow("🎨 Accent Color", "Choose accent color",
            createAccentColorPicker());
        panel.add(accentPanel, gbc);

        // Transparency
        JPanel transparencyPanel = createSettingRow("🪟 Transparency", "Window transparency",
            createSlider(0, 100, (int)(Registry.getInstance().getDouble("theme.transparency", 0.85) * 100)));
        panel.add(transparencyPanel, gbc);

        // Corner radius
        JPanel cornerPanel = createSettingRow("⬜ Corner Radius", "Window corner rounding",
            createSlider(0, 20, Registry.getInstance().getInt("theme.cornerRadius", 8)));
        panel.add(cornerPanel, gbc);

        // Animations
        JPanel animPanel = createSettingRow("✨ Animations", "Enable UI animations",
            new JToggleButton());
        panel.add(animPanel, gbc);

        // Blur effect
        JPanel blurPanel = createSettingRow("🌫 Blur Effect", "Enable background blur",
            new JToggleButton());
        panel.add(blurPanel, gbc);

        // Fill remaining space
        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createDesktopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Wallpaper
        String currentWallpaper = Registry.getInstance().getString("desktop.wallpaper", "default");
        JPanel wallpaperPanel = createSettingRow("🖼 Wallpaper", currentWallpaper,
            createButton("Browse...", e -> chooseWallpaper()));
        panel.add(wallpaperPanel, gbc);

        // Icon size
        JPanel iconSizePanel = createSettingRow("🔲 Icon Size", "Desktop icon size",
            createSlider(24, 96, Registry.getInstance().getInt("desktop.iconSize", 48)));
        panel.add(iconSizePanel, gbc);

        // Show icons
        JPanel showIconsPanel = createSettingRow("👁 Show Desktop Icons", "Toggle desktop icons",
            new JToggleButton());
        panel.add(showIconsPanel, gbc);

        // Taskbar alignment
        JPanel taskbarAlignPanel = createSettingRow("📏 Taskbar Alignment", "Left or centered",
            createCombo("center", "left", "center"));
        panel.add(taskbarAlignPanel, gbc);

        // Auto-hide taskbar
        JPanel autoHidePanel = createSettingRow("🙈 Auto-hide Taskbar", "Automatically hide taskbar",
            new JToggleButton());
        panel.add(autoHidePanel, gbc);

        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createThemesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel themeLabel = new JLabel("Select a Theme");
        themeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        themeLabel.setForeground(new Color(235, 235, 235));
        panel.add(themeLabel, gbc);

        // Theme buttons grid
        JPanel themeGrid = new JPanel(new GridLayout(3, 3, 8, 8));
        themeGrid.setOpaque(false);

        String[][] themes = {
            {"🌑", "Dark", "dark"},
            {"☀", "Light", "light"},
            {"🔵", "Blue", "blue"},
            {"🟣", "Purple", "purple"},
            {"🟢", "Green", "green"},
            {"🟠", "Orange", "orange"},
            {"🔴", "Red", "red"},
            {"🪟", "Glass", "glass"},
            {"✨", "Transparent", "transparent"}
        };

        for (String[] theme : themes) {
            JPanel themeItem = new JPanel(new BorderLayout());
            themeItem.setOpaque(false);
            themeItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(theme[0], SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 28));

            JLabel name = new JLabel(theme[1], SwingConstants.CENTER);
            name.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            name.setForeground(new Color(200, 200, 200));

            themeItem.add(icon, BorderLayout.CENTER);
            themeItem.add(name, BorderLayout.SOUTH);

            String themeName = theme[2];
            themeItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ThemeManager.getInstance().applyTheme(themeName);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    themeItem.setOpaque(true);
                    themeItem.setBackground(new Color(255, 255, 255, 20));
                    themeItem.repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    themeItem.setOpaque(false);
                    themeItem.repaint();
                }
            });

            themeGrid.add(themeItem);
        }

        panel.add(themeGrid, gbc);

        // Font size
        JPanel fontSizePanel = createSettingRow("🔤 Font Size", "UI font size",
            createSlider(10, 20, 14));
        panel.add(fontSizePanel, gbc);

        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createSystemInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        String[][] info = {
            {"OS", "DunDunDunOS v1.0"},
            {"Kernel", "DunOS Kernel 1.0"},
            {"Architecture", "64-bit (Simulated)"},
            {"Java Version", System.getProperty("java.version", "Unknown")},
            {"Java Vendor", System.getProperty("java.vendor", "Unknown")},
            {"User", System.getProperty("user.name", "Unknown")},
            {"OS Name", System.getProperty("os.name", "Unknown")},
            {"Available Processors", String.valueOf(Runtime.getRuntime().availableProcessors())},
            {"Total Memory", formatMemory(Runtime.getRuntime().totalMemory())},
            {"Free Memory", formatMemory(Runtime.getRuntime().freeMemory())},
            {"Max Memory", formatMemory(Runtime.getRuntime().maxMemory())},
            {"Working Directory", System.getProperty("user.dir", "Unknown")}
        };

        for (String[] row : info) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setOpaque(false);

            JLabel keyLabel = new JLabel(row[0]);
            keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            keyLabel.setForeground(new Color(180, 180, 180));

            JLabel valueLabel = new JLabel(row[1]);
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            valueLabel.setForeground(new Color(220, 220, 220));

            rowPanel.add(keyLabel, BorderLayout.WEST);
            rowPanel.add(valueLabel, BorderLayout.EAST);
            rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50)));

            panel.add(rowPanel, gbc);
        }

        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        panel.add(createSettingRow("🔔 Notifications", "Enable notifications", new JToggleButton()), gbc);
        panel.add(createSettingRow("🔊 Sound", "Notification sounds", new JToggleButton()), gbc);
        panel.add(createSettingRow("📱 Do Not Disturb", "Silence notifications", new JToggleButton()), gbc);
        panel.add(createSettingRow("👁 Show on Lock Screen", "Show notifications on lock", new JToggleButton()), gbc);

        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createNetworkPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JPanel wifiPanel = createSettingRow("📶 Wi-Fi", "DunOS-Network (Connected)", createButton("Disconnect", e -> {}));
        panel.add(wifiPanel, gbc);
        JPanel ethernetPanel = createSettingRow("🔌 Ethernet", "Connected", createButton("Details", e -> {}));
        panel.add(ethernetPanel, gbc);
        JPanel proxyPanel = createSettingRow("🌐 Proxy", "Not configured", createButton("Setup", e -> {}));
        panel.add(proxyPanel, gbc);

        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createStoragePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel storageLabel = new JLabel("💾 Local Storage");
        storageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        storageLabel.setForeground(new Color(235, 235, 235));
        panel.add(storageLabel, gbc);

        // Storage bar
        JPanel barPanel = new JPanel(new BorderLayout(8, 0));
        barPanel.setOpaque(false);

        JProgressBar storageBar = new JProgressBar(0, 100);
        storageBar.setValue(35);
        storageBar.setPreferredSize(new Dimension(0, 20));
        storageBar.setBackground(new Color(50, 50, 50));
        storageBar.setForeground(new Color(0, 120, 212));
        storageBar.setStringPainted(true);
        storageBar.setString("35% Used (3.5 GB / 10 GB)");
        storageBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        barPanel.add(storageBar, BorderLayout.CENTER);

        panel.add(barPanel, gbc);

        String[][] items = {
            {"📁 System", "1.2 GB"},
            {"📁 Applications", "0.8 GB"},
            {"📁 Users", "1.0 GB"},
            {"🗑 Recycle Bin", "0.1 GB"},
            {"📁 Other", "0.4 GB"},
            {"⬜ Free Space", "6.5 GB"}
        };

        for (String[] item : items) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            JLabel name = new JLabel(item[0]);
            name.setForeground(new Color(200, 200, 200));
            name.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            JLabel size = new JLabel(item[1]);
            size.setForeground(new Color(150, 150, 150));
            size.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            row.add(name, BorderLayout.WEST);
            row.add(size, BorderLayout.EAST);
            panel.add(row, gbc);
        }

        panel.add(createButton("🧹 Run Disk Cleaner", e -> Desktop.AppLauncher.launch("diskcleaner")), gbc);

        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createSettingRow(String label, String description, JComponent control) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(220, 220, 220));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(new Color(140, 140, 140));

        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(control, BorderLayout.EAST);

        return row;
    }

    private JPanel createAccentColorPicker() {
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        colorPanel.setOpaque(false);

        Color[] colors = {
            new Color(0, 120, 212), new Color(180, 80, 255), 
            new Color(60, 200, 100), new Color(255, 140, 40),
            new Color(255, 60, 60), new Color(255, 200, 50)
        };

        for (Color c : colors) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(22, 22));
            btn.setBackground(c);
            btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                Registry.getInstance().set("desktop.accentColor", "#" + Integer.toHexString(c.getRGB()).substring(2));
            });
            colorPanel.add(btn);
        }

        return colorPanel;
    }

    private JSlider createSlider(int min, int max, int value) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        slider.setPreferredSize(new Dimension(120, 24));
        slider.setOpaque(false);
        return slider;
    }

    private JComboBox<String> createCombo(String selected, String... items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setSelectedItem(selected);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(new Color(45, 45, 45));
        combo.setForeground(new Color(200, 200, 200));
        return combo;
    }

    private JButton createButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(new Color(45, 45, 45));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private void chooseWallpaper() {
        JFileChooser chooser = new JFileChooser("Images/Wallpapers");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Images (*.jpg, *.png)", "jpg", "jpeg", "png", "bmp", "gif"));
        if (chooser.showOpenDialog(content) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            Registry.getInstance().set("desktop.wallpaper", path);
            // Need reference to Desktop - set via event bus
            JOptionPane.showMessageDialog(content, "Wallpaper set. Restart to see changes.");
        }
    }

    private String formatMemory(long bytes) {
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}

