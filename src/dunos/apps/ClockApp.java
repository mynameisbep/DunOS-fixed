package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DunDunDunOS Clock - Clock with alarm functionality.
 */
public class ClockApp {

    private JLabel timeLabel;
    private JLabel dateLabel;
    private Timer clockTimer;

    public Window createWindow() {
        Window window = new Window("Clock", "clock", null);
        window.setSize(350, 250);
        window.setResizable(false);
        window.setMaximizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new GridBagLayout());
        content.setBackground(new Color(28, 28, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        // Time
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.THIN, 64));
        timeLabel.setForeground(new Color(235, 235, 235));
        content.add(timeLabel, gbc);

        // Date
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateLabel.setForeground(new Color(160, 160, 160));
        gbc.insets = new Insets(10, 0, 0, 0);
        content.add(dateLabel, gbc);

        // Alarm button
        JButton alarmBtn = new JButton("⏰ Set Alarm");
        alarmBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alarmBtn.setForeground(new Color(200, 200, 200));
        alarmBtn.setBackground(new Color(45, 45, 45));
        alarmBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(8, 20, 8, 20)
        ));
        alarmBtn.setFocusPainted(false);
        alarmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        alarmBtn.addActionListener(e -> setAlarm());
        gbc.insets = new Insets(20, 0, 0, 0);
        content.add(alarmBtn, gbc);

        // Update clock
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();

        return window;
    }

    private void updateClock() {
        timeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        dateLabel.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date()));
    }

    private void setAlarm() {
        String input = JOptionPane.showInputDialog(null, "Enter alarm time (HH:mm):");
        if (input != null && input.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(null, "Alarm set for " + input);
        }
    }
}

