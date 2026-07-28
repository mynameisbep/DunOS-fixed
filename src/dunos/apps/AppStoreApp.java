package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * DunDunDunOS App Store - Mock application store for browsing apps.
 */
public class AppStoreApp {

    public Window createWindow() {
        Window window = new Window("App Store", "appstore", null);
        window.setSize(650, 450);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 15, 10, 15));

        JLabel title = new JLabel("📦 App Store");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(235, 235, 235));

        JTextField searchField = new JTextField("  🔍 Search apps...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(new Color(140, 140, 140));
        searchField.setBackground(new Color(40, 40, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(8, 10, 8, 10)
        ));

        header.add(title, BorderLayout.NORTH);
        header.add(searchField, BorderLayout.SOUTH);
        content.add(header, BorderLayout.NORTH);

        // App grid
        JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(0, 15, 15, 15));

        String[][] apps = {
            {"🧮", "Calculator", "Standard calculator", "Free"},
            {"🌐", "Browser", "Web browser", "Free"},
            {"📁", "Explorer", "File manager", "Free"},
            {"🖌", "Paint", "Drawing app", "Free"},
            {"💻", "Terminal", "Command line", "Free"},
            {"📝", "Notes", "Take notes", "Free"},
            {"🎵", "Media Player", "Play music", "Free"},
            {"📊", "Task Manager", "System monitor", "Free"},
            {"📅", "Calendar", "Date planner", "Free"}
        };

        for (String[] app : apps) {
            JPanel item = new JPanel(new BorderLayout());
            item.setOpaque(false);
            item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50)),
                new EmptyBorder(10, 10, 10, 10)
            ));
            item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(app[0], SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 28));

            JLabel name = new JLabel(app[1], SwingConstants.CENTER);
            name.setFont(new Font("Segoe UI", Font.BOLD, 12));
            name.setForeground(new Color(220, 220, 220));

            JLabel desc = new JLabel(app[2], SwingConstants.CENTER);
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            desc.setForeground(new Color(140, 140, 140));

            JLabel price = new JLabel(app[3], SwingConstants.CENTER);
            price.setFont(new Font("Segoe UI", Font.BOLD, 11));
            price.setForeground(new Color(0, 180, 80));

            item.add(icon, BorderLayout.NORTH);
            JPanel textPanel = new JPanel(new GridLayout(3, 1));
            textPanel.setOpaque(false);
            textPanel.add(name); textPanel.add(desc); textPanel.add(price);
            item.add(textPanel, BorderLayout.CENTER);

            grid.add(item);
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(28, 28, 28));
        content.add(scroll, BorderLayout.CENTER);

        return window;
    }
}

