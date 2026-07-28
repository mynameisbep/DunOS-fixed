package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DunDunDunOS Weather - Mock weather application.
 */
public class WeatherApp {

    public Window createWindow() {
        Window window = new Window("Weather", "weather", null);
        window.setSize(400, 350);
        window.setResizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new GridBagLayout());
        content.setBackground(new Color(30, 35, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Weather icon
        JLabel iconLabel = new JLabel("☀️");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        gbc.insets = new Insets(20, 0, 10, 0);
        content.add(iconLabel, gbc);

        // Temperature
        JLabel tempLabel = new JLabel("72°F");
        tempLabel.setFont(new Font("Segoe UI", Font.THIN, 48));
        tempLabel.setForeground(new Color(235, 235, 235));
        gbc.insets = new Insets(0, 0, 5, 0);
        content.add(tempLabel, gbc);

        // Condition
        JLabel conditionLabel = new JLabel("Sunny");
        conditionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        conditionLabel.setForeground(new Color(180, 180, 180));
        content.add(conditionLabel, gbc);

        // Location
        JLabel locationLabel = new JLabel("📍 San Francisco, CA");
        locationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        locationLabel.setForeground(new Color(140, 140, 140));
        gbc.insets = new Insets(10, 0, 20, 0);
        content.add(locationLabel, gbc);

        // Details grid
        JPanel details = new JPanel(new GridLayout(2, 2, 15, 8));
        details.setOpaque(false);

        String[][] data = {
            {"💧 Humidity", "45%"},
            {"💨 Wind", "12 mph"},
            {"🌡 Feels Like", "70°F"},
            {"🔆 UV Index", "6"}
        };

        for (String[] d : data) {
            JPanel p = new JPanel(new GridLayout(2, 1));
            p.setOpaque(false);
            JLabel l1 = new JLabel(d[0], SwingConstants.CENTER);
            l1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            l1.setForeground(new Color(140, 140, 140));
            JLabel l2 = new JLabel(d[1], SwingConstants.CENTER);
            l2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            l2.setForeground(new Color(220, 220, 220));
            p.add(l1); p.add(l2);
            details.add(p);
        }

        content.add(details, gbc);

        return window;
    }
}

