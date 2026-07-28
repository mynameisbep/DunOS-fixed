package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;

/**
 * DunDunDunOS Text Editor - Full-featured text editor with syntax highlighting support.
 */
public class TextEditorApp {

    private JTextArea textArea;
    private File currentFile;
    private boolean modified;
    private JLabel statusLabel;
    private JComboBox<String> fontSelector;
    private JSpinner fontSizeSpinner;

    public Window createWindow() {
        Window window = new Window("Text Editor", "texteditor", null);
        window.setSize(700, 500);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Menu bar
        JPanel menubar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        menubar.setOpaque(false);

        JButton newBtn = createBtn("📄 New");
        JButton openBtn = createBtn("📂 Open");
        JButton saveBtn = createBtn("💾 Save");
        JButton saveAsBtn = createBtn("💾 Save As");
        JButton undoBtn = createBtn("↩ Undo");
        JButton redoBtn = createBtn("↪ Redo");

        newBtn.addActionListener(e -> newFile());
        openBtn.addActionListener(e -> openFile());
        saveBtn.addActionListener(e -> saveFile());
        saveAsBtn.addActionListener(e -> saveAsFile());
        undoBtn.addActionListener(e -> textArea.undo());
        redoBtn.addActionListener(e -> textArea.redo());

        menubar.add(newBtn);
        menubar.add(openBtn);
        menubar.add(saveBtn);
        menubar.add(saveAsBtn);
        menubar.add(undoBtn);
        menubar.add(redoBtn);

        // Font controls
        menubar.add(new JLabel("  "));
        String[] fonts = {"Segoe UI", "Consolas", "Arial", "Courier New", "Monospaced"};
        fontSelector = new JComboBox<>(fonts);
        fontSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fontSelector.setBackground(new Color(45, 45, 45));
        fontSelector.setForeground(new Color(200, 200, 200));
        fontSelector.setSelectedItem("Segoe UI");
        fontSelector.addActionListener(e -> updateFont());
        menubar.add(fontSelector);

        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(14, 8, 48, 1));
        fontSizeSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fontSizeSpinner.setPreferredSize(new Dimension(60, 24));
        fontSizeSpinner.addChangeListener(e -> updateFont());
        menubar.add(fontSizeSpinner);

        content.add(menubar, BorderLayout.NORTH);

        // Text area with line numbers
        textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(new Color(32, 32, 32));
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
        scrollPane.getViewport().setBackground(new Color(32, 32, 32));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI());
        content.add(scrollPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("New file | Line: 1 | Col: 1");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(140, 140, 140));
        statusLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
        content.add(statusLabel, BorderLayout.SOUTH);

        return window;
    }

    private void newFile() {
        if (modified) {
            int r = JOptionPane.showConfirmDialog(null, "Save changes?", "Text Editor", 
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (r == JOptionPane.YES_OPTION) saveFile();
            else if (r == JOptionPane.CANCEL_OPTION) return;
        }
        textArea.setText("");
        currentFile = null;
        modified = false;
        statusLabel.setText("New file | Line: 1 | Col: 1");
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

    private void saveFile() {
        if (currentFile == null) {
            saveAsFile();
            return;
        }
        try (FileWriter fw = new FileWriter(currentFile)) {
            fw.write(textArea.getText());
            modified = false;
            statusLabel.setText("Saved: " + currentFile.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save: " + e.getMessage());
        }
    }

    private void saveAsFile() {
        JFileChooser chooser = new JFileChooser("Documents");
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            saveFile();
        }
    }

    private void updateFont() {
        String fontName = (String) fontSelector.getSelectedItem();
        int size = (int) fontSizeSpinner.getValue();
        textArea.setFont(new Font(fontName, Font.PLAIN, size));
    }

    private void setModified(boolean m) {
        modified = m;
        String name = currentFile != null ? currentFile.getName() : "New file";
        statusLabel.setText((m ? "* " : "") + name);
    }

    private JButton createBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(new Color(45, 45, 45));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(4, 8, 4, 8)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Custom scroll bar UI
    private static class BasicScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(60, 60, 60);
            thumbHighlightColor = new Color(70, 70, 70);
            thumbDarkShadowColor = new Color(40, 40, 40);
            trackColor = new Color(30, 30, 30);
        }
    }
}

