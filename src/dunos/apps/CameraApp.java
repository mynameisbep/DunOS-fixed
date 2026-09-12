package dunos.apps;

import dunos.ui.*;
import dunos.ui.Window;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * DunDunDunOS Camera - Mock camera application.
 */
public class CameraApp {

    public Window createWindow() {
        Window window = new Window("Camera", "camera", null);
        window.setSize(500, 400);
        window.setResizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Viewfinder
        JPanel viewfinder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(20, 20, 25));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Grid lines
                g2d.setColor(new Color(60, 60, 70, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(getWidth() / 3, 0, getWidth() / 3, getHeight());
                g2d.drawLine(getWidth() * 2 / 3, 0, getWidth() * 2 / 3, getHeight());
                g2d.drawLine(0, getHeight() / 3, getWidth(), getHeight() / 3);
                g2d.drawLine(0, getHeight() * 2 / 3, getWidth(), getHeight() * 2 / 3);

                // Center circle
                g2d.setColor(new Color(100, 200, 100, 50));
                g2d.drawOval(getWidth() / 2 - 30, getHeight() / 2 - 30, 60, 60);

                // "No camera" message
                g2d.setColor(new Color(150, 150, 150));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                String msg = "📷 Camera unavailable (simulated)";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() - 20);

                g2d.dispose();
            }
        };
        viewfinder.setPreferredSize(new Dimension(0, 300));
        content.add(viewfinder, BorderLayout.CENTER);

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        controls.setOpaque(false);

        JButton captureBtn = createBtn("📸 Capture", e -> JOptionPane.showMessageDialog(window, "Photo captured! (simulated)"));
        JButton switchBtn = createBtn("🔄 Switch", e -> {});
        JButton settingsBtn = createBtn("⚙", e -> {});

        controls.add(captureBtn);
        controls.add(switchBtn);
        controls.add(settingsBtn);
        content.add(controls, BorderLayout.SOUTH);

        return window;
    }

    private JButton createBtn(String text, ActionListener action) {
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
}

