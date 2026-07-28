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
        return """
            ╔═══════════════════════════════════════╗
            ║        DunDunDunOS System Info        ║
            ╚═══════════════════════════════════════╝

            Operating System:
              OS Name:        DunDunDunOS v1.0
              Kernel:         DunOS Kernel 1.0
              Architecture:   64-bit (Simulated)

            Hardware:
              Processors:     """ + rt.availableProcessors() + """
              Total Memory:   """ + (rt.totalMemory() / (1024 * 1024)) + """ MB
              Free Memory:    """ + (rt.freeMemory() / (1024 * 1024)) + """ MB
              Max Memory:     """ + (rt.maxMemory() / (1024 * 1024)) + """ MB

            Java:
              Version:        """ + System.getProperty("java.version", "N/A") + """
              Vendor:         """ + System.getProperty("java.vendor", "N/A") + """
              Home:           """ + System.getProperty("java.home", "N/A") + """

            System:
              User:           """ + System.getProperty("user.name", "N/A") + """
              OS:             """ + System.getProperty("os.name", "N/A") + """
              Working Dir:    """ + System.getProperty("user.dir", "N/A") + """

            DunOS Services:
              Kernel          [Running]
              Registry        [Running]
              Event Bus       [Running]
              Theme Manager   [Running]
              File System     [Running]
              Window Manager  [Running]
              Desktop         [Running]

            Build: 2024.1
            """;
    }
}

