package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DunDunDunOS Disk Cleaner - Analyze and clean disk space.
 */
public class DiskCleanerApp {

    public Window createWindow() {
        Window window = new Window("Disk Cleaner", "diskcleaner", null);
        window.setSize(500, 400);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        JPanel listPanel = new JPanel(new GridBagLayout());
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel title = new JLabel("🧹 Disk Cleaner");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(235, 235, 235));
        listPanel.add(title, gbc);

        JLabel subtitle = new JLabel("Select files to clean. Total space that can be freed: 1.2 GB");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(140, 140, 140));
        gbc.insets = new Insets(0, 4, 15, 4);
        listPanel.add(subtitle, gbc);

        String[][] items = {
            {"🗑 Recycle Bin", "0.1 GB", "false"},
            {"📁 Temp Files", "0.4 GB", "true"},
            {"🌐 Browser Cache", "0.3 GB", "true"},
            {"📦 Log Files", "0.2 GB", "true"},
            {"🖼 Thumbnails", "0.1 GB", "true"},
            {"📄 Old Downloads", "0.1 GB", "false"}
        };

        gbc.insets = new Insets(2, 4, 2, 4);
        for (String[] item : items) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);

            JCheckBox check = new JCheckBox(item[0]);
            check.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            check.setForeground(new Color(200, 200, 200));
            check.setOpaque(false);
            check.setSelected(Boolean.parseBoolean(item[2]));

            JLabel sizeLabel = new JLabel(item[1]);
            sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sizeLabel.setForeground(new Color(140, 140, 140));

            row.add(check, BorderLayout.WEST);
            row.add(sizeLabel, BorderLayout.EAST);
            listPanel.add(row, gbc);
        }

        JButton cleanBtn = new JButton("🧹 Clean Selected Files");
        cleanBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cleanBtn.setForeground(Color.WHITE);
        cleanBtn.setBackground(new Color(0, 100, 180));
        cleanBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        cleanBtn.setFocusPainted(false);
        cleanBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(15, 4, 0, 4);
        cleanBtn.addActionListener(e -> JOptionPane.showMessageDialog(window, "Cleaning complete!\nFreed: 1.1 GB"));
        listPanel.add(cleanBtn, gbc);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(28, 28, 28));
        content.add(scroll, BorderLayout.CENTER);

        return window;
    }
}

