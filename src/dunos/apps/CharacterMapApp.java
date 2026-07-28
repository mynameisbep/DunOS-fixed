package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * DunDunDunOS Character Map - Browse and copy Unicode characters.
 */
public class CharacterMapApp {

    public Window createWindow() {
        Window window = new Window("Character Map", "charactermap", null);
        window.setSize(550, 400);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        JLabel header = new JLabel("Select a character to copy:", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        header.setForeground(new Color(200, 200, 200));
        header.setBorder(new EmptyBorder(10, 10, 5, 10));
        content.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(8, 16, 4, 4));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(5, 10, 10, 10));

        String[][] categories = {
            {"😀", "😂", "🤣", "😍", "🤔", "😎", "🙌", "💪", "🔥", "⭐", "❤", "💡", "🎉", "✅", "❌", "⚠"},
            {"☀", "🌙", "⭐", "🌈", "☁", "⛅", "❄", "🔥", "💧", "🌊", "🌍", "🌺", "🌻", "🌲", "🍎", "🍕"},
            {"⚽", "🏀", "🎾", "🎱", "🏈", "⚾", "🎯", "🎮", "🎲", "🎰", "🏆", "🥇", "🎪", "🎨", "🎭", "🎤"},
            {"🚗", "🚕", "🚌", "🚓", "🚑", "🚒", "✈", "🚀", "🚁", "🚂", "🚲", "🛴", "⛵", "🚢", "🛸", "🚇"},
            {"♠", "♥", "♦", "♣", "♛", "♚", "♝", "♞", "♟", "♔", "♕", "♖", "♗", "♘", "♙", "♤"},
            {"α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο", "π"},
            {"∞", "≈", "≠", "≤", "≥", "±", "×", "÷", "√", "∑", "∏", "∫", "∂", "∆", "∅", "∈"},
            {"←", "↑", "→", "↓", "↔", "↕", "↖", "↗", "↘", "↙", "↩", "↪", "⌚", "⌛", "⏰", "⏳"}
        };

        for (String[] row : categories) {
            for (String ch : row) {
                JButton btn = new JButton(ch);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                btn.setForeground(new Color(220, 220, 220));
                btn.setBackground(new Color(40, 40, 40));
                btn.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55)));
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                btn.addActionListener(e -> {
                    StringSelection sel = new StringSelection(btn.getText());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                    header.setText("Copied '" + btn.getText() + "' to clipboard!");
                });

                grid.add(btn);
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(28, 28, 28));
        content.add(scroll, BorderLayout.CENTER);

        return window;
    }
}

