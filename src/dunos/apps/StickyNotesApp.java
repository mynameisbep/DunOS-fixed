package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DunDunDunOS Sticky Notes - Desktop sticky notes application.
 */
public class StickyNotesApp {

    public Window createWindow() {
        Window window = new Window("Sticky Notes", "stickynotes", null);
        window.setSize(280, 300);
        window.setMaximizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(255, 255, 200));

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(new Color(255, 255, 200));
        textArea.setForeground(new Color(30, 30, 30));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText("Type your note here...");

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(255, 255, 200));
        content.add(scroll, BorderLayout.CENTER);

        // Color options
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        colorPanel.setOpaque(false);
        Color[] colors = {new Color(255, 255, 200), new Color(200, 255, 200), 
                         new Color(200, 200, 255), new Color(255, 200, 200), new Color(255, 200, 255)};
        for (Color c : colors) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(20, 20));
            btn.setBackground(c);
            btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
            btn.addActionListener(e -> {
                textArea.setBackground(c);
                scroll.getViewport().setBackground(c);
                content.setBackground(c);
            });
            colorPanel.add(btn);
        }
        content.add(colorPanel, BorderLayout.SOUTH);

        return window;
    }
}

