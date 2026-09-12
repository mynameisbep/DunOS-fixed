package dunos.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * DunDunDunOS SplashScreen - Professional animated splash screen shown during boot.
 * Displays the DunDunDunOS logo, animated progress bar, status messages,
 * and provides smooth fade-in/fade-out transitions.
 */
public class SplashScreen extends JWindow {

    private final JLabel logoLabel;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;
    private Timer fadeTimer;
    private float opacity;
    private boolean fadingIn;
    private boolean fadingOut;
    private Runnable onFadeOutComplete;

    /**
     * Creates the splash screen with logo, title, and progress indicator.
     */
    public SplashScreen() {
        this.opacity = 0.0f;
        this.fadingIn = true;
        this.fadingOut = false;

        // Set up the window
        setSize(620, 420);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        // Create main panel with rounded corners
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Background with rounded corners
                g2d.setColor(new Color(28, 28, 28, 240));
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.fill(rect);

                // Subtle border
                g2d.setColor(new Color(60, 60, 60, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(rect);

                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 20, 5, 20);

        // Logo
        ImageIcon logoIcon = loadLogo();
        logoLabel = new JLabel();
        if (logoIcon != null) {
            Image img = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } else {
            // Fallback: draw text logo
            logoLabel.setText("🐉");
            logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(logoLabel, gbc);

        // Title
        titleLabel = new JLabel("DunDunDunOS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 120, 212));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        subtitleLabel = new JLabel("Starting Desktop...");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(subtitleLabel, gbc);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 6));
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(45, 45, 45));
        progressBar.setForeground(new Color(0, 120, 212));
        progressBar.setBorderPainted(false);
        progressBar.setValue(0);
        gbc.insets = new Insets(20, 20, 10, 20);
        mainPanel.add(progressBar, gbc);

        // Status label
        statusLabel = new JLabel("Initializing...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 20, 15, 20);
        mainPanel.add(statusLabel, gbc);

        add(mainPanel);

        // Fade timer (30 FPS)
        fadeTimer = new Timer(16, e -> {
            if (fadingIn) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    fadingIn = false;
                }
                setOpacity(Math.min(1.0f, opacity));
            }
            if (fadingOut) {
                opacity -= 0.05f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    fadingOut = false;
                    ((Timer) e.getSource()).stop();
                    setVisible(false);
                    dispose();
                    if (onFadeOutComplete != null) {
                        onFadeOutComplete.run();
                    }
                }
                setOpacity(Math.max(0.0f, opacity));
            }
        });
    }

    /**
     * Shows the splash screen with fade-in animation.
     */
    public void showSplash() {
        setOpacity(0.0f);
        setVisible(true);
        fadingIn = true;
        fadeTimer.start();
    }

    /**
     * Hides the splash screen with fade-out animation.
     */
    public void hideSplash(Runnable onComplete) {
        this.onFadeOutComplete = onComplete;
        fadingOut = true;
    }

    /**
     * Updates the progress bar value.
     */
    public void setProgress(int percent) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
    }

    /**
     * Updates the status message displayed.
     */
    public void setStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
        });
    }

    /**
     * Updates the subtitle text.
     */
    public void setSubtitle(String subtitle) {
        SwingUtilities.invokeLater(() -> subtitleLabel.setText(subtitle));
    }

    /**
     * Loads the logo image from the Images directory.
     */
    private ImageIcon loadLogo() {
        try {
            java.io.File logoFile = new java.io.File("Images/DunDun.jpg");
            if (logoFile.exists()) {
                return new ImageIcon(logoFile.getAbsolutePath());
            }
            // Try alternative extensions
            String[] extensions = {".png", ".jpeg", ".gif", ".bmp"};
            for (String ext : extensions) {
                logoFile = new java.io.File("Images/DunDun" + ext);
                if (logoFile.exists()) {
                    return new ImageIcon(logoFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("[Splash] Could not load logo: " + e.getMessage());
        }
        return null;
    }
}

