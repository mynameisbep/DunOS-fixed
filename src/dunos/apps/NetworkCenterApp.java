package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DunDunDunOS Network Center - View network connections and settings.
 */
public class NetworkCenterApp {

    public Window createWindow() {
        Window window = new Window("Network Center", "networkcenter", null);
        window.setSize(500, 350);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        JPanel listPanel = new JPanel(new GridBagLayout());
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel title = new JLabel("📡 Network Center");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(235, 235, 235));
        listPanel.add(title, gbc);

        // Wi-Fi
        addNetworkRow(listPanel, gbc, "📶 Wi-Fi", "DunOS-Network", "Connected", new Color(80, 200, 80));
        addNetworkRow(listPanel, gbc, "🔌 Ethernet", "Wired Connection", "Connected", new Color(80, 200, 80));
        addNetworkRow(listPanel, gbc, "🌐 IPv4", "192.168.1.100", "", null);
        addNetworkRow(listPanel, gbc, "🌐 IPv6", "fe80::1", "", null);
        addNetworkRow(listPanel, gbc, "🔀 DNS", "8.8.8.8, 8.8.4.4", "", null);
        addNetworkRow(listPanel, gbc, "🛡 Firewall", "Active", "Protected", new Color(80, 200, 80));

        JPanel scrollPanel = new JPanel(new BorderLayout());
        scrollPanel.setOpaque(false);
        scrollPanel.add(listPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(scrollPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(28, 28, 28));
        content.add(scroll, BorderLayout.CENTER);

        return window;
    }

    private void addNetworkRow(JPanel panel, GridBagConstraints gbc, 
                                String icon, String name, String status, Color statusColor) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50)));

        JLabel nameLabel = new JLabel(icon + "  " + name);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setForeground(new Color(200, 200, 200));

        row.add(nameLabel, BorderLayout.WEST);

        if (!status.isEmpty()) {
            JLabel statusLabel = new JLabel(status);
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            statusLabel.setForeground(statusColor != null ? statusColor : new Color(140, 140, 140));
            row.add(statusLabel, BorderLayout.EAST);
        }

        panel.add(row, gbc);
    }
}

