package dunos.apps;

import dunos.ui.*;
import dunos.ui.Window;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * DunDunDunOS Notes - Simple text notes application with save/load.
 */
public class NotesApp {

    private JTextArea textArea;
    private File currentFile;
    private boolean modified;
    private JLabel statusLabel;

    public Window createWindow() {
        Window window = new Window("Notes", "notes", null);
        window.setSize(600, 400);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setOpaque(false);

        JButton newBtn = createToolBtn("📄 New");
        JButton saveBtn = createToolBtn("💾 Save");
        JButton openBtn = createToolBtn("📂 Open");
        JButton clearBtn = createToolBtn("🗑 Clear");

        newBtn.addActionListener(e -> newFile());
        saveBtn.addActionListener(e -> saveFile());
        openBtn.addActionListener(e -> openFile());
        clearBtn.addActionListener(e -> textArea.setText(""));

        toolbar.add(newBtn);
        toolbar.add(saveBtn);
        toolbar.add(openBtn);
        toolbar.add(clearBtn);

        content.add(toolbar, BorderLayout.NORTH);

        // Text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(new Color(35, 35, 35));
        textArea.setForeground(new Color(220, 220, 220));
        textArea.setCaretColor(Color.WHITE);
        textArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        textArea.setTabSize(4);
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(35, 35, 35));
        content.add(scrollPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("New note");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(140, 140, 140));
        statusLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
        content.add(statusLabel, BorderLayout.SOUTH);

        return window;
    }

    private void newFile() {
        if (modified) {
            int r = JOptionPane.showConfirmDialog(null, "Save changes?", "Notes", 
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (r == JOptionPane.YES_OPTION) saveFile();
            else if (r == JOptionPane.CANCEL_OPTION) return;
        }
        textArea.setText("");
        currentFile = null;
        modified = false;
        statusLabel.setText("New note");
    }

    private void saveFile() {
        if (currentFile == null) {
            JFileChooser chooser = new JFileChooser("Documents");
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
            } else return;
        }
        try (FileWriter fw = new FileWriter(currentFile)) {
            fw.write(textArea.getText());
            modified = false;
            statusLabel.setText("Saved: " + currentFile.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save: " + e.getMessage());
        }
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser("Documents");
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(currentFile))) {
                textArea.read(br, null);
                modified = false;
                statusLabel.setText("Opened: " + currentFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to open: " + e.getMessage());
            }
        }
    }

    private void setModified(boolean m) {
        modified = m;
        statusLabel.setText((m ? "* " : "") + (currentFile != null ? currentFile.getName() : "New note"));
    }

    private JButton createToolBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(new Color(45, 45, 45));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(4, 10, 4, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

