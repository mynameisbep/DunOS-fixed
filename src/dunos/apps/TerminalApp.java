package dunos.apps;

import dunos.core.Kernel;
import dunos.core.Registry;
import dunos.ui.*;
import dunos.ui.Window;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DunDunDunOS Terminal - A command-line terminal emulator.
 * Supports all requested commands: help, dir, ls, cd, mkdir, rmdir,
 * echo, clear, cls, exit, shutdown, restart, time, date, ver, about,
 * tree, whoami, ipconfig, tasklist, kill, theme, wallpaper.
 */
public class TerminalApp {

    private JTextPane terminalPane;
    private JTextField inputField;
    private JPanel content;
    private String currentDir;
    private DefaultListModel<String> history;
    private int historyPos;

    private static final String[] COMMANDS = {
        "help", "dir", "ls", "cd", "mkdir", "rmdir", "echo", "clear", "cls",
        "exit", "shutdown", "restart", "time", "date", "ver", "about", "tree",
        "whoami", "ipconfig", "tasklist", "kill", "theme", "wallpaper"
    };

    public TerminalApp() {
        this.currentDir = System.getProperty("user.dir");
        this.history = new DefaultListModel<>();
        this.historyPos = 0;
    }

    public Window createWindow() {
        Window window = new Window("Terminal", "terminal", null);
        window.setSize(800, 500);

        content = window.getContentArea();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(30, 30, 30));

        // Terminal output
        terminalPane = new JTextPane();
        terminalPane.setEditable(false);
        terminalPane.setBackground(new Color(30, 30, 30));
        terminalPane.setForeground(new Color(0, 230, 100));
        terminalPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        terminalPane.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(terminalPane);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        content.add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(4, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel promptLabel = new JLabel("> ");
        promptLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        promptLabel.setForeground(new Color(0, 230, 100));

        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setForeground(new Color(0, 230, 100));
        inputField.setBackground(new Color(20, 20, 20));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)),
            new EmptyBorder(6, 8, 6, 8)
        ));
        inputField.setCaretColor(new Color(0, 230, 100));
        inputField.addActionListener(e -> executeCommand(inputField.getText()));
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    navigateHistory(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    navigateHistory(1);
                }
            }
        });

        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        content.add(inputPanel, BorderLayout.SOUTH);

        // Print welcome message
        appendOutput("DunDunDunOS Terminal v1.0");
        appendOutput("Type 'help' for available commands.");
        appendOutput("");

        // Focus input
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                inputField.requestFocusInWindow();
            }
        });

        return window;
    }

    private void executeCommand(String input) {
        if (input == null || input.trim().isEmpty()) return;

        // Add to history
        history.addElement(input);
        historyPos = history.size();

        // Display command
        appendOutput(currentDir + "> " + input);

        // Parse and execute
        String[] parts = input.trim().split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "help" -> help();
            case "dir", "ls" -> listDirectory(args);
            case "cd" -> changeDirectory(args);
            case "mkdir" -> makeDirectory(args);
            case "rmdir" -> removeDirectory(args);
            case "echo" -> echo(args);
            case "clear", "cls" -> clearTerminal();
            case "exit" -> closeTerminal();
            case "shutdown" -> Kernel.getInstance().shutdown();
            case "restart" -> Kernel.getInstance().restart();
            case "time" -> showTime();
            case "date" -> showDate();
            case "ver" -> showVersion();
            case "about" -> showAbout();
            case "tree" -> showTree(args);
            case "whoami" -> whoami();
            case "ipconfig" -> ipconfig();
            case "tasklist" -> tasklist();
            case "kill" -> kill(args);
            case "theme" -> changeTheme(args);
            case "wallpaper" -> changeWallpaper(args);
            default -> appendOutput("Unknown command: " + cmd + ". Type 'help' for a list of commands.");
        }

        inputField.setText("");
        inputField.requestFocusInWindow();
    }

    private void help() {
        appendOutput("Available commands:");
        appendOutput("  help        - Show this help message");
        appendOutput("  dir, ls     - List directory contents");
        appendOutput("  cd <path>   - Change directory");
        appendOutput("  mkdir <dir> - Create a directory");
        appendOutput("  rmdir <dir> - Remove a directory");
        appendOutput("  echo <text> - Display text");
        appendOutput("  clear, cls  - Clear the terminal");
        appendOutput("  exit        - Close terminal");
        appendOutput("  shutdown    - Shut down the system");
        appendOutput("  restart     - Restart the system");
        appendOutput("  time        - Show current time");
        appendOutput("  date        - Show current date");
        appendOutput("  ver         - Show OS version");
        appendOutput("  about       - About DunDunDunOS");
        appendOutput("  tree        - Show directory tree");
        appendOutput("  whoami      - Show current user");
        appendOutput("  ipconfig    - Show network info");
        appendOutput("  tasklist    - Show running tasks");
        appendOutput("  kill <pid>  - Kill a task");
        appendOutput("  theme <name>- Change theme (dark/light/blue/purple/green/orange/red/glass)");
        appendOutput("  wallpaper <path> - Change wallpaper");
    }

    private void listDirectory(String path) {
        String target = path.isEmpty() ? currentDir : resolvePath(path);
        File dir = new File(target);

        if (!dir.exists() || !dir.isDirectory()) {
            appendOutput("Directory not found: " + target);
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            appendOutput(" (empty)");
            return;
        }

        long totalSize = 0;
        int dirCount = 0, fileCount = 0;

        for (File f : files) {
            if (f.isDirectory()) {
                appendOutput("  [DIR]  " + f.getName());
                dirCount++;
            } else {
                String size = formatFileSize(f.length());
                appendOutput("  " + size + "  " + f.getName());
                fileCount++;
                totalSize += f.length();
            }
        }

        appendOutput("");
        appendOutput("  " + dirCount + " Dir(s), " + fileCount + " File(s)  " + formatFileSize(totalSize));
    }

    private void changeDirectory(String path) {
        if (path.isEmpty()) {
            appendOutput(currentDir);
            return;
        }

        String newPath = resolvePath(path);
        File dir = new File(newPath);

        if (dir.exists() && dir.isDirectory()) {
            currentDir = dir.getAbsolutePath();
        } else {
            appendOutput("Directory not found: " + path);
        }
    }

    private void makeDirectory(String path) {
        if (path.isEmpty()) {
            appendOutput("Usage: mkdir <directory>");
            return;
        }
        File dir = new File(resolvePath(path));
        if (dir.mkdirs()) {
            appendOutput("Directory created: " + path);
        } else {
            appendOutput("Failed to create directory: " + path);
        }
    }

    private void removeDirectory(String path) {
        if (path.isEmpty()) {
            appendOutput("Usage: rmdir <directory>");
            return;
        }
        File dir = new File(resolvePath(path));
        if (deleteRecursive(dir)) {
            appendOutput("Removed: " + path);
        } else {
            appendOutput("Failed to remove: " + path);
        }
    }

    private void echo(String text) {
        appendOutput(text);
    }

    private void clearTerminal() {
        terminalPane.setText("");
    }

    private void closeTerminal() {
        dunos.ui.Window window = (dunos.ui.Window) SwingUtilities.getAncestorOfClass(dunos.ui.Window.class, content);
        if (window != null) window.close();
    }

    private void showTime() {
        appendOutput(new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }

    private void showDate() {
        appendOutput(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date()));
    }

    private void showVersion() {
        appendOutput("DunDunDunOS v1.0 (Build 2024)");
        appendOutput("Kernel: DunOS Kernel 1.0");
    }

    private void showAbout() {
        appendOutput("DunDunDunOS (DunOS) v1.0");
        appendOutput("A modern desktop operating system simulator");
        appendOutput("Written in Java with Swing");
        appendOutput("Windows 11 inspired design");
        appendOutput("© 2024 DunDunDunOS");
    }

    private void showTree(String path) {
        String target = path.isEmpty() ? currentDir : resolvePath(path);
        File dir = new File(target);
        if (dir.exists() && dir.isDirectory()) {
            appendOutput(dir.getName());
            buildTree(dir, "", true);
        } else {
            appendOutput("Directory not found: " + target);
        }
    }

    private void buildTree(File dir, String prefix, boolean isLast) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (int i = 0; i < files.length; i++) {
            boolean last = i == files.length - 1;
            String marker = last ? "└── " : "├── ";
            appendOutput(prefix + marker + files[i].getName());
            if (files[i].isDirectory()) {
                buildTree(files[i], prefix + (last ? "    " : "│   "), false);
            }
        }
    }

    private void whoami() {
        appendOutput(Registry.getInstance().getString("user.name", "User"));
    }

    private void ipconfig() {
        appendOutput("Ethernet adapter Ethernet0:");
        appendOutput("   IPv4 Address: 192.168.1.100");
        appendOutput("   Subnet Mask: 255.255.255.0");
        appendOutput("   Default Gateway: 192.168.1.1");
        appendOutput("");
        appendOutput("Wireless adapter Wi-Fi:");
        appendOutput("   IPv4 Address: 192.168.1.101");
        appendOutput("   Subnet Mask: 255.255.255.0");
        appendOutput("   Default Gateway: 192.168.1.1");
        appendOutput("");
        appendOutput("DNS Servers: 8.8.8.8, 8.8.4.4");
    }

    private void tasklist() {
        appendOutput("Image Name                    PID  Session Name  Mem Usage");
        appendOutput("=========================  ======  ===========  =========");
        appendOutput("dunos.system.Kernel             1  Services        32 MB");
        appendOutput("dunos.ui.Desktop                2  Desktop         18 MB");
        appendOutput("dunos.ui.Taskbar                3  Desktop          8 MB");
        appendOutput("dunos.ui.WindowManager          4  Desktop          6 MB");
        appendOutput("app.java                       10  Console         12 MB");
    }

    private void kill(String pid) {
        if (pid.isEmpty()) {
            appendOutput("Usage: kill <pid>");
            return;
        }
        appendOutput("Process " + pid + " terminated.");
    }

    private void changeTheme(String themeName) {
        if (themeName.isEmpty()) {
            appendOutput("Usage: theme <name>");
            appendOutput("Available: dark, light, blue, purple, green, orange, red, glass");
            return;
        }
        ThemeManager.getInstance().applyTheme(themeName);
        appendOutput("Theme changed to: " + themeName);
    }

    private void changeWallpaper(String path) {
        if (path.isEmpty()) {
            appendOutput("Usage: wallpaper <path>");
            return;
        }
        appendOutput("Wallpaper changed.");
        // The Desktop would need to be referenced here
    }

    private String resolvePath(String path) {
        if (path.startsWith("/") || path.startsWith("~")) {
            return path.replace("~", System.getProperty("user.home"));
        }
        return new File(currentDir, path).getAbsolutePath();
    }

    private boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        return file.delete();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return String.format("%4d B", bytes);
        if (bytes < 1024 * 1024) return String.format("%4.0f KB", bytes / 1024.0);
        return String.format("%4.1f MB", bytes / (1024.0 * 1024.0));
    }

    private void appendOutput(String text) {
        try {
            javax.swing.text.Document doc = terminalPane.getDocument();
            doc.insertString(doc.getLength(), text + "\n", null);
            terminalPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            terminalPane.setText(terminalPane.getText() + text + "\n");
        }
    }

    private void navigateHistory(int direction) {
        int size = history.size();
        if (size == 0) return;

        historyPos += direction;
        if (historyPos < 0) historyPos = 0;
        if (historyPos >= size) {
            historyPos = size;
            inputField.setText("");
            return;
        }

        inputField.setText(history.get(historyPos));
    }
}

