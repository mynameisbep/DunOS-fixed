package dunos.ui;

import dunos.core.Registry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DunDunDunOS LoginScreen - User login with avatar, password, time/date,
 * background image, and power/accessibility buttons.
 */
public class LoginScreen extends JWindow {

    private final JPanel mainPanel;
    private final JLabel timeLabel;
    private final JLabel dateLabel;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;
    private Runnable onLogin;

    public LoginScreen() {
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocation(0, 0);

        mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(18, 18, 28));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Subtle gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 40, 60, 50), 
                    getWidth(), getHeight(), new Color(60, 30, 60, 50));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Time
        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Segoe UI", Font.THIN, 72));
        timeLabel.setForeground(new Color(235, 235, 235));
        mainPanel.add(timeLabel, gbc);

        // Date
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateLabel.setForeground(new Color(180, 180, 180));
        gbc.insets = new Insets(0, 5, 30, 5);
        mainPanel.add(dateLabel, gbc);

        // Avatar
        JLabel avatarLabel = new JLabel("👤", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(avatarLabel, gbc);

        // Username
        String username = Registry.getInstance().getString("user.name", "User");
        JLabel userLabel = new JLabel(username, SwingConstants.CENTER);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        userLabel.setForeground(new Color(220, 220, 220));
        mainPanel.add(userLabel, gbc);

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setEchoChar('●');
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setBackground(new Color(40, 40, 50));
        passwordField.setForeground(new Color(220, 220, 220));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        passwordField.addActionListener(e -> attemptLogin());

        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(passwordField, gbc);

        // Error label
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(255, 80, 80));
        mainPanel.add(errorLabel, gbc);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);

        JButton powerBtn = new JButton("⏻");
        JButton accessibilityBtn = new JButton("♿");
        JButton shutdownBtn = new JButton("⏻ Shut Down");

        for (JButton btn : new JButton[]{powerBtn, accessibilityBtn, shutdownBtn}) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setForeground(new Color(200, 200, 200));
            btn.setBackground(new Color(40, 40, 50, 150));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                new EmptyBorder(8, 16, 8, 16)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        shutdownBtn.addActionListener(e -> System.exit(0));
        powerBtn.addActionListener(e -> System.exit(0));
        accessibilityBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Accessibility options:\n• Magnifier\n• Narrator\n• High Contrast\n• Sticky Keys"));

        bottomPanel.add(accessibilityBtn);
        bottomPanel.add(shutdownBtn);

        gbc.insets = new Insets(30, 5, 10, 5);
        mainPanel.add(bottomPanel, gbc);

        add(mainPanel);

        // Clock update timer
        Timer clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    /**
     * Shows the login screen and calls onLogin when authenticated.
     */
    public void showLogin(Runnable onLogin) {
        this.onLogin = onLogin;
        setVisible(true);
        passwordField.requestFocus();
    }

    private void attemptLogin() {
        String password = new String(passwordField.getPassword());
        String storedPassword = Registry.getInstance().getString("user.password", "");

        if (storedPassword.isEmpty() || password.equals(storedPassword)) {
            // Successful login
            errorLabel.setText("");
            setVisible(false);
            dispose();
            if (onLogin != null) onLogin.run();
        } else {
            errorLabel.setText("Invalid password. Try again.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private void updateClock() {
        timeLabel.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        dateLabel.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date()));
    }
}

