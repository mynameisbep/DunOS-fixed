package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DunDunDunOS System Information - Display system details.
 */
public class SystemInfoApp {

    public Window createWindow() {
        Window window = new Window("System Information", "systeminfo", null);
        window.setSize(500, 400);
        window.setResizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(28, 28, 28));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        infoArea.setBackground(new Color(28, 28, 28));
        infoArea.setForeground(new Color(200, 200, 200));
        infoArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        infoArea.setText(buildSystemInfo());

        JScrollPane scroll = new JScrollPane(infoArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(28, 28, 28));
        content.add(scroll, BorderLayout.CENTER);

        return window;
    }

    private String buildSystemInfo() {
        Runtime rt = Runtime.getRuntime();
        return "╔═══════════════════════════════════════╗\n" +
            "║        DunDunDunOS System Info        ║\n" +
            "╚═══════════════════════════════════════╝\n\n" +
            "Operating System:\n" +
            "  OS Name:        DunDunDunOS v1.0\n" +
            "  Kernel:         DunOS Kernel 1.0\n" +
            "  Architecture:   64-bit (Simulated)\n\n" +
            "Hardware:\n" +
            "  Processors:     " + rt.availableProcessors() + "\n" +
            "  Total Memory:   " + (rt.totalMemory() / (1024 * 1024)) + " MB\n" +
            "  Free Memory:    " + (rt.freeMemory() / (1024 * 1024)) + " MB\n" +
            "  Max Memory:     " + (rt.maxMemory() / (1024 * 1024)) + " MB\n\n" +
            "Java:\n" +
            "  Version:        " + System.getProperty("java.version", "N/A") + "\n" +
            "  Vendor:         " + System.getProperty("java.vendor", "N/A") + "\n" +
            "  Home:           " + System.getProperty("java.home", "N/A") + "\n\n" +
            "System:\n" +
            "  User:           " + System.getProperty("user.name", "N/A") + "\n" +
            "  OS:             " + System.getProperty("os.name", "N/A") + "\n" +
            "  Working Dir:    " + System.getProperty("user.dir", "N/A") + "\n\n" +
            "DunOS Services:\n" +
            "  Kernel          [Running]\n" +
            "  Registry        [Running]\n" +
            "  Event Bus       [Running]\n" +
            "  Theme Manager   [Running]\n" +
            "  File System     [Running]\n" +
            "  Window Manager  [Running]\n" +
            "  Desktop         [Running]\n\n" +
            "Build: 2024.1\n";
    }
}

