package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;

/**
 * DunDunDunOS Task Manager - Monitor processes, CPU, RAM, and kill tasks.
 */
public class TaskManagerApp {

    private JTable processTable;
    private DefaultTableModel tableModel;
    private JProgressBar cpuBar;
    private JProgressBar ramBar;
    private JLabel cpuLabel;
    private JLabel ramLabel;
    private Timer refreshTimer;

    public Window createWindow() {
        Window window = new Window("Task Manager", "taskmanager", null);
        window.setSize(650, 480);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Performance tab
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabs.setForeground(new Color(200, 200, 200));
        tabs.setBackground(new Color(30, 30, 30));

        // Processes tab
        JPanel processPanel = new JPanel(new BorderLayout());
        processPanel.setOpaque(false);

        String[] columns = {"Process Name", "PID", "CPU %", "Memory", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        processTable = new JTable(tableModel);
        processTable.setBackground(new Color(35, 35, 35));
        processTable.setForeground(new Color(200, 200, 200));
        processTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        processTable.setRowHeight(22);
        processTable.setGridColor(new Color(50, 50, 50));
        processTable.getTableHeader().setBackground(new Color(45, 45, 45));
        processTable.getTableHeader().setForeground(new Color(200, 200, 200));
        processTable.setSelectionBackground(new Color(0, 80, 160));

        JScrollPane scrollPane = new JScrollPane(processTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(35, 35, 35));
        processPanel.add(scrollPane, BorderLayout.CENTER);

        // Kill button
        JButton killBtn = new JButton("✕ End Task");
        killBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        killBtn.setForeground(new Color(200, 200, 200));
        killBtn.setBackground(new Color(60, 40, 40));
        killBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 60, 60)),
            new EmptyBorder(6, 14, 6, 14)
        ));
        killBtn.setFocusPainted(false);
        killBtn.addActionListener(e -> killProcess());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        btnPanel.add(killBtn);
        processPanel.add(btnPanel, BorderLayout.SOUTH);

        tabs.addTab("Processes", processPanel);

        // Performance tab
        JPanel perfPanel = new JPanel(new GridBagLayout());
        perfPanel.setOpaque(false);
        perfPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // CPU
        JPanel cpuPanel = new JPanel(new BorderLayout(8, 0));
        cpuPanel.setOpaque(false);
        cpuLabel = new JLabel("CPU: 0%");
        cpuLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cpuLabel.setForeground(new Color(220, 220, 220));
        cpuBar = new JProgressBar(0, 100);
        cpuBar.setPreferredSize(new Dimension(0, 20));
        cpuBar.setBackground(new Color(50, 50, 50));
        cpuBar.setForeground(new Color(0, 120, 212));
        cpuPanel.add(cpuLabel, BorderLayout.NORTH);
        cpuPanel.add(cpuBar, BorderLayout.CENTER);
        perfPanel.add(cpuPanel, gbc);

        // RAM
        JPanel ramPanel = new JPanel(new BorderLayout(8, 0));
        ramPanel.setOpaque(false);
        ramLabel = new JLabel("Memory: 0 MB / 0 MB");
        ramLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ramLabel.setForeground(new Color(220, 220, 220));
        ramBar = new JProgressBar(0, 100);
        ramBar.setPreferredSize(new Dimension(0, 20));
        ramBar.setBackground(new Color(50, 50, 50));
        ramBar.setForeground(new Color(80, 180, 80));
        ramPanel.add(ramLabel, BorderLayout.NORTH);
        ramPanel.add(ramBar, BorderLayout.CENTER);
        perfPanel.add(ramPanel, gbc);

        tabs.addTab("Performance", perfPanel);

        content.add(tabs, BorderLayout.CENTER);

        // Populate and refresh
        populateProcesses();
        refreshTimer = new Timer(2000, e -> refreshData());
        refreshTimer.start();

        return window;
    }

    private void populateProcesses() {
        String[][] processes = {
            {"Kernel (System)", "1", "2", "32 MB", "Running"},
            {"Desktop Environment", "2", "5", "18 MB", "Running"},
            {"Taskbar", "3", "1", "8 MB", "Running"},
            {"Window Manager", "4", "1", "6 MB", "Running"},
            {"Event Bus", "5", "0", "4 MB", "Running"},
            {"File System", "6", "1", "5 MB", "Running"},
            {"Theme Manager", "7", "0", "2 MB", "Running"},
            {"Audio Service", "8", "0", "3 MB", "Running"},
            {"Notification Service", "9", "0", "2 MB", "Running"},
            {"Update Service", "10", "0", "1 MB", "Running"}
        };

        for (String[] proc : processes) {
            tableModel.addRow(proc);
        }
    }

    private void refreshData() {
        int cpu = (int) (Math.random() * 30 + 5);
        cpuBar.setValue(cpu);
        cpuLabel.setText("CPU: " + cpu + "%");

        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        int memPercent = (int) (usedMem * 100 / Math.max(totalMem, 1));
        ramBar.setValue(memPercent);
        ramLabel.setText(String.format("Memory: %d MB / %d MB", 
            usedMem / (1024 * 1024), totalMem / (1024 * 1024)));
    }

    private void killProcess() {
        int row = processTable.getSelectedRow();
        if (row >= 0) {
            String name = (String) tableModel.getValueAt(row, 0);
            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(null, "Process terminated: " + name);
        }
    }
}

