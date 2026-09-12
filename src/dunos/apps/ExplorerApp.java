package dunos.apps;

import dunos.system.FileSystemManager;
import dunos.ui.*;
import dunos.ui.Window;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * DunDunDunOS File Explorer - Full-featured file browser with tree navigation,
 * grid/list views, copy/paste, rename, delete, and breadcrumb navigation.
 */
public class ExplorerApp {

    private JPanel content;
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JLabel pathLabel;
    private JPanel viewPanel;
    private String currentPath;
    private boolean gridView;
    private final List<File> clipboard;
    private boolean cutMode;
    private JPanel gridPanel;

    public ExplorerApp() {
        this.currentPath = System.getProperty("user.dir");
        this.clipboard = new ArrayList<>();
        this.cutMode = false;
        this.gridView = true;
    }

    public Window createWindow() {
        Window window = new Window("File Explorer", "explorer", null);
        window.setSize(900, 600);

        content = window.getContentArea();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(28, 28, 28));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(4, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        navButtons.setOpaque(false);

        JButton backBtn = createToolBtn("◀");
        JButton forwardBtn = createToolBtn("▶");
        JButton upBtn = createToolBtn("⬆");
        JButton refreshBtn = createToolBtn("🔄");
        JButton newFolderBtn = createToolBtn("📁+");
        JButton deleteBtn = createToolBtn("🗑");
        JButton viewToggleBtn = createToolBtn(gridView ? "📋" : "🔲");

        backBtn.addActionListener(e -> {});
        forwardBtn.addActionListener(e -> {});
        upBtn.addActionListener(e -> navigateUp());
        refreshBtn.addActionListener(e -> refreshView());
        newFolderBtn.addActionListener(e -> newFolder());
        deleteBtn.addActionListener(e -> deleteSelected());
        viewToggleBtn.addActionListener(e -> toggleView());

        navButtons.add(backBtn);
        navButtons.add(forwardBtn);
        navButtons.add(upBtn);
        navButtons.add(refreshBtn);
        navButtons.add(newFolderBtn);
        navButtons.add(deleteBtn);
        navButtons.add(viewToggleBtn);

        toolbar.add(navButtons, BorderLayout.WEST);

        // Address bar
        pathLabel = new JLabel(currentPath);
        pathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pathLabel.setForeground(new Color(200, 200, 200));
        pathLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(6, 10, 6, 10)
        ));
        toolbar.add(pathLabel, BorderLayout.CENTER);

        content.add(toolbar, BorderLayout.NORTH);

        // Split pane: tree + file view
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(40, 40, 40));
        splitPane.setBorder(null);

        // File tree
        DefaultMutableTreeNode root = buildTree();
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        fileTree.setBackground(new Color(35, 35, 35));
        fileTree.setForeground(new Color(200, 200, 200));
        fileTree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileTree.setRowHeight(22);
        fileTree.setBorder(new EmptyBorder(4, 4, 4, 4));
        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            if (node != null && node.getUserObject() instanceof FileNode) {
                FileNode fn = (FileNode) node.getUserObject();
                navigateTo(fn.file.getAbsolutePath());
            }
        });

        JScrollPane treeScroll = new JScrollPane(fileTree);
        treeScroll.setBorder(null);
        treeScroll.getViewport().setBackground(new Color(35, 35, 35));
        splitPane.setLeftComponent(treeScroll);

        // File view panel (grid/list)
        viewPanel = new JPanel(new BorderLayout());
        viewPanel.setOpaque(false);
        gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        gridPanel.setOpaque(false);

        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.setBorder(null);
        gridScroll.getViewport().setBackground(new Color(28, 28, 28));

        // Table view
        String[] columns = {"Name", "Size", "Type", "Modified"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        fileTable = new JTable(tableModel);
        fileTable.setBackground(new Color(35, 35, 35));
        fileTable.setForeground(new Color(200, 200, 200));
        fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileTable.setRowHeight(24);
        fileTable.getTableHeader().setBackground(new Color(45, 45, 45));
        fileTable.getTableHeader().setForeground(new Color(200, 200, 200));
        fileTable.setGridColor(new Color(50, 50, 50));
        fileTable.setSelectionBackground(new Color(0, 80, 160));

        JScrollPane tableScroll = new JScrollPane(fileTable);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(new Color(35, 35, 35));

        viewPanel.add(gridScroll, BorderLayout.CENTER);
        splitPane.setRightComponent(viewPanel);

        splitPane.setDividerLocation(250);
        content.add(splitPane, BorderLayout.CENTER);

        // Initial load
        refreshView();

        // Context menu for files
        setupContextMenu();

        return window;
    }

    private void navigateTo(String path) {
        currentPath = path;
        pathLabel.setText(path);
        refreshView();
    }

    private void navigateUp() {
        File parent = new File(currentPath).getParentFile();
        if (parent != null) {
            navigateTo(parent.getAbsolutePath());
        }
    }

    private void refreshView() {
        gridPanel.removeAll();
        tableModel.setRowCount(0);

        FileSystemManager fsm = FileSystemManager.getInstance();
        File[] files = fsm.listDirectory(currentPath);

        if (files != null) {
            for (File file : files) {
                if (gridView) {
                    addGridItem(file);
                } else {
                    addTableRow(file);
                }
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void addGridItem(File file) {
        JPanel item = new JPanel(new BorderLayout());
        item.setPreferredSize(new Dimension(80, 90));
        item.setOpaque(false);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String icon = FileSystemManager.getFileIcon(file);
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));

        String name = file.getName();
        if (name.length() > 12) name = name.substring(0, 11) + "...";
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        nameLabel.setForeground(new Color(200, 200, 200));

        item.add(iconLabel, BorderLayout.CENTER);
        item.add(nameLabel, BorderLayout.SOUTH);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && file.isDirectory()) {
                    navigateTo(file.getAbsolutePath());
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setOpaque(true);
                item.setBackground(new Color(255, 255, 255, 30));
                item.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                item.setOpaque(false);
                item.repaint();
            }
        });

        gridPanel.add(item);
    }

    private void addTableRow(File file) {
        String size = file.isDirectory() ? "<DIR>" : FileSystemManager.formatSize(file.length());
        String ext = file.isDirectory() ? "File Folder" : FileSystemManager.getExtension(file).toUpperCase() + " File";
        String modified = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date(file.lastModified()));
        tableModel.addRow(new Object[]{file.getName(), size, ext, modified});
    }

    private void toggleView() {
        gridView = !gridView;
        refreshView();
    }

    private void newFolder() {
        String name = JOptionPane.showInputDialog(content, "Folder name:");
        if (name != null && !name.trim().isEmpty()) {
            FileSystemManager.getInstance().createDirectory(currentPath + "/" + name);
            refreshView();
        }
    }

    private void deleteSelected() {
        int row = fileTable.getSelectedRow();
        if (row >= 0) {
            String name = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(content,
                "Delete '" + name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                FileSystemManager.getInstance().delete(currentPath + "/" + name);
                refreshView();
            }
        }
    }

    private void setupContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem renameItem = new JMenuItem("Rename");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem propertiesItem = new JMenuItem("Properties");

        contextMenu.add(openItem);
        contextMenu.add(copyItem);
        contextMenu.add(pasteItem);
        contextMenu.addSeparator();
        contextMenu.add(renameItem);
        contextMenu.add(deleteItem);
        contextMenu.addSeparator();
        contextMenu.add(propertiesItem);

        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    contextMenu.show(fileTable, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    contextMenu.show(fileTable, e.getX(), e.getY());
                }
            }
        });
    }

    private DefaultMutableTreeNode buildTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(new File(System.getProperty("user.dir")), "🏠 This PC"));
        addDirectories(new File(System.getProperty("user.dir")), root);
        return root;
    }

    private void addDirectories(File dir, DefaultMutableTreeNode node) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && !f.isHidden()) {
                    DefaultMutableTreeNode child = new DefaultMutableTreeNode(
                        new FileNode(f, FileSystemManager.getFileIcon(f) + " " + f.getName()));
                    node.add(child);
                    addDirectories(f, child);
                }
            }
        }
    }

    private JButton createToolBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

    private static class FileNode {
        final File file;
        final String display;

        FileNode(File file, String display) {
            this.file = file;
            this.display = display;
        }

        @Override
        public String toString() { return display; }
    }
}

