package dunos.apps;

import dunos.ui.*;
import dunos.ui.Window;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DunDunDunOS Calendar - Monthly calendar view.
 */
public class CalendarApp {

    private JLabel monthLabel;
    private JPanel daysPanel;
    private Calendar calendar;

    public Window createWindow() {
        Window window = new Window("Calendar", "calendar", null);
        window.setSize(400, 400);
        window.setResizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(28, 28, 28));

        calendar = Calendar.getInstance();

        // Navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JButton prevBtn = new JButton("◀");
        JButton nextBtn = new JButton("▶");
        prevBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nextBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prevBtn.setForeground(new Color(200, 200, 200));
        nextBtn.setForeground(new Color(200, 200, 200));
        prevBtn.setOpaque(false);
        nextBtn.setOpaque(false);
        prevBtn.setBorder(null);
        nextBtn.setBorder(null);
        prevBtn.setFocusPainted(false);
        nextBtn.setFocusPainted(false);
        prevBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        monthLabel.setForeground(new Color(235, 235, 235));

        prevBtn.addActionListener(e -> { calendar.add(Calendar.MONTH, -1); updateCalendar(); });
        nextBtn.addActionListener(e -> { calendar.add(Calendar.MONTH, 1); updateCalendar(); });

        navPanel.add(prevBtn, BorderLayout.WEST);
        navPanel.add(monthLabel, BorderLayout.CENTER);
        navPanel.add(nextBtn, BorderLayout.EAST);
        content.add(navPanel, BorderLayout.NORTH);

        // Day headers
        JPanel headersPanel = new JPanel(new GridLayout(1, 7, 2, 2));
        headersPanel.setOpaque(false);
        headersPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(new Color(140, 140, 140));
            headersPanel.add(lbl);
        }
        content.add(headersPanel, BorderLayout.CENTER);

        // Days grid
        daysPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        daysPanel.setOpaque(false);
        daysPanel.setBorder(new EmptyBorder(4, 10, 10, 10));
        content.add(daysPanel, BorderLayout.SOUTH);

        updateCalendar();

        return window;
    }

    private void updateCalendar() {
        monthLabel.setText(new SimpleDateFormat("MMMM yyyy").format(calendar.getTime()));
        daysPanel.removeAll();

        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        boolean currentMonth = Calendar.getInstance().get(Calendar.MONTH) == calendar.get(Calendar.MONTH) 
            && Calendar.getInstance().get(Calendar.YEAR) == calendar.get(Calendar.YEAR);

        // Empty days before first
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            dayLabel.setForeground(new Color(200, 200, 200));

            if (currentMonth && day == today) {
                dayLabel.setOpaque(true);
                dayLabel.setBackground(new Color(0, 120, 212));
                dayLabel.setForeground(Color.WHITE);
            }

            dayLabel.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 0));
            daysPanel.add(dayLabel);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }
}

