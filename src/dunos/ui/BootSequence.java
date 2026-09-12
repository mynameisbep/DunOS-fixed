package dunos.ui;

import javax.swing.*;
import java.awt.*;

/**
 * DunDunDunOS BootSequence - Displays a text-based boot sequence screen
 * similar to operating system startup logs. Shows initialization messages
 * in a terminal-like fashion before transitioning to the splash/desktop.
 */
public class BootSequence extends JWindow {

    private final JTextPane bootText;
    private javax.swing.Timer typeTimer;
    private final String[] bootMessages;
    private int currentMessage;
    private int currentChar;
    private StringBuilder currentLine;
    private Runnable onComplete;
    private boolean skipping;

    private static final String[] DEFAULT_BOOT_MESSAGES = {
        "DunDunDunOS v1.0 - Booting...",
        "",
        "[KERNEL] Initializing kernel... OK",
        "[KERNEL] Loading system drivers... OK",
        "[KERNEL] Initializing registry... OK",
        "[KERNEL] Mounting file systems... OK",
        "[KERNEL] Starting service manager... OK",
        "[SERVICES] Loading notification service... OK",
        "[SERVICES] Loading theme service... OK",
        "[SERVICES] Loading audio service... OK",
        "[SERVICES] Loading update service... OK",
        "[DESKTOP] Initializing display... OK",
        "[DESKTOP] Loading desktop environment... OK",
        "[DESKTOP] Starting window manager... OK",
        "[SHELL] Launching explorer... OK",
        "",
        "DunDunDunOS is ready.",
        "Starting desktop environment..."
    };

    /**
     * Creates the boot sequence display.
     */
    public BootSequence() {
        this(DEFAULT_BOOT_MESSAGES);
    }

    /**
     * Creates the boot sequence with custom messages.
     */
    public BootSequence(String[] messages) {
        this.bootMessages = messages != null ? messages : DEFAULT_BOOT_MESSAGES;
        this.currentMessage = 0;
        this.currentChar = 0;
        this.currentLine = new StringBuilder();
        this.skipping = false;

        setSize(800, 600);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);

        bootText = new JTextPane();
        bootText.setBackground(Color.BLACK);
        bootText.setForeground(new Color(0, 200, 80));
        bootText.setFont(new Font("Consolas", Font.PLAIN, 14));
        bootText.setEditable(false);
        bootText.setMargin(new Insets(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(bootText);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().setBackground(Color.BLACK);
        add(scrollPane);

        // Typewriter timer
        typeTimer = new javax.swing.Timer(25, e -> {
            if (currentMessage < bootMessages.length) {
                String msg = bootMessages[currentMessage];

                if (currentChar < msg.length()) {
                    currentLine.append(msg.charAt(currentChar));
                    currentChar++;
                    updateText();
                } else {
                    currentLine.append("\n");
                    currentMessage++;
                    currentChar = 0;
                    if (currentMessage < bootMessages.length) {
                        // Don't add extra newline for empty messages
                    }
                    updateText();
                }
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                if (onComplete != null) {
                    javax.swing.Timer delay = new javax.swing.Timer(500, ev -> {
                        onComplete.run();
                    });
                    delay.setRepeats(false);
                    delay.start();
                }
            }
        });
    }

    /**
     * Starts the boot sequence animation.
     */
    public void start(Runnable onComplete) {
        this.onComplete = onComplete;
        setVisible(true);
        typeTimer.start();

        // Allow skipping by clicking
        bootText.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                skipToEnd();
            }
        });

        // Allow skipping by pressing any key
        bootText.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                skipToEnd();
            }
        });
        bootText.setFocusable(true);
        bootText.requestFocus();
    }

    /**
     * Skips the boot animation and jumps to the end.
     */
    public void skipToEnd() {
        if (skipping) return;
        skipping = true;
        typeTimer.stop();

        StringBuilder full = new StringBuilder();
        for (String msg : bootMessages) {
            full.append(msg).append("\n");
        }
        bootText.setText(full.toString());
        bootText.setCaretPosition(bootText.getDocument().getLength());

        javax.swing.Timer skipTimer = new javax.swing.Timer(300, e -> {
            if (onComplete != null) onComplete.run();
        });
        skipTimer.setRepeats(false);
        skipTimer.start();
    }

    private void updateText() {
        // Build full text from start to current position
        StringBuilder full = new StringBuilder();
        for (int i = 0; i < currentMessage; i++) {
            full.append(bootMessages[i]).append("\n");
        }
        full.append(currentLine);

        // Add blinking cursor
        full.append("_");

        bootText.setText(full.toString());
        bootText.setCaretPosition(bootText.getDocument().getLength());
    }

    /**
     * Hides and disposes the boot sequence window.
     */
    public void dismiss() {
        typeTimer.stop();
        setVisible(false);
        dispose();
    }
}

