package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * DunDunDunOS Browser - A simple tabbed web browser with history, bookmarks,
 * and basic HTML rendering. Uses JEditorPane for basic HTML display.
 */
public class BrowserApp {

    private final java.util.List<String> history;
    private int historyIndex;
    private final java.util.List<JPanel> tabs;
    private final java.util.Map<String, String> bookmarks;
    private JTabbedPane tabbedPane;
    private JTextField addressBar;

    public BrowserApp() {
        this.history = new ArrayList<>();
        this.historyIndex = -1;
        this.tabs = new ArrayList<>();
        this.bookmarks = new HashMap<>();
    }

    public Window createWindow() {
        Window window = new Window("Browser", "browser", null);
        window.setSize(900, 650);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(4, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        navButtons.setOpaque(false);

        JButton backBtn = createToolButton("◀");
        JButton forwardBtn = createToolButton("▶");
        JButton refreshBtn = createToolButton("🔄");
        JButton homeBtn = createToolButton("🏠");
        JButton bookmarkBtn = createToolButton("⭐");

        backBtn.addActionListener(e -> goBack());
        forwardBtn.addActionListener(e -> goForward());
        refreshBtn.addActionListener(e -> refreshPage());
        homeBtn.addActionListener(e -> goHome());
        bookmarkBtn.addActionListener(e -> toggleBookmark());

        navButtons.add(backBtn);
        navButtons.add(forwardBtn);
        navButtons.add(refreshBtn);
        navButtons.add(homeBtn);
        navButtons.add(bookmarkBtn);

        toolbar.add(navButtons, BorderLayout.WEST);

        // Address bar
        addressBar = new JTextField("https://duckduckgo.com");
        addressBar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressBar.setForeground(new Color(200, 200, 200));
        addressBar.setBackground(new Color(40, 40, 40));
        addressBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(6, 10, 6, 10)
        ));
        addressBar.setCaretColor(Color.WHITE);
        addressBar.addActionListener(e -> navigateTo(addressBar.getText()));
        toolbar.add(addressBar, BorderLayout.CENTER);

        content.add(toolbar, BorderLayout.NORTH);

        // Tabbed pane for pages
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabbedPane.setForeground(new Color(200, 200, 200));
        tabbedPane.setBackground(new Color(30, 30, 30));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // New tab button
        JPanel tabButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabButtonPanel.setOpaque(false);
        JButton newTabBtn = createToolButton("+");
        newTabBtn.addActionListener(e -> addNewTab("about:blank"));
        tabButtonPanel.add(newTabBtn);
        content.add(tabButtonPanel, BorderLayout.SOUTH);

        content.add(tabbedPane, BorderLayout.CENTER);

        // Add initial tab
        addNewTab("https://duckduckgo.com");

        return window;
    }

    private JButton createToolButton(String text) {
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

    private void addNewTab(String url) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setBackground(new Color(28, 28, 28));
        editorPane.setForeground(new Color(200, 200, 200));
        editorPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                navigateTo(e.getURL().toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(28, 28, 28));

        int tabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab("New Tab", scrollPane);
        tabbedPane.setSelectedIndex(tabIndex);

        if (url != null && !url.equals("about:blank")) {
            navigateTo(url);
        } else {
            setPageContent(editorPane, buildHomePage());
        }
    }

    private void navigateTo(String url) {
        if (url == null || url.isEmpty()) return;

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        addressBar.setText(url);

        // Add to history
        if (historyIndex >= 0 && historyIndex < history.size() - 1) {
            history.subList(historyIndex + 1, history.size()).clear();
        }
        history.add(url);
        historyIndex = history.size() - 1;

        // Load page
        loadPage(url);
    }

    private void loadPage(String url) {
        JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        if (scrollPane == null) return;

        JEditorPane editorPane = (JEditorPane) scrollPane.getViewport().getView();
        if (editorPane == null) return;

        try {
            editorPane.setPage(url);
            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), getPageTitle(url));
        } catch (Exception e) {
            // Fallback for pages that can't be loaded
            setPageContent(editorPane, buildErrorPage(url, e.getMessage()));
            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), "Error");
        }
    }

    private void setPageContent(JEditorPane editorPane, String html) {
        editorPane.setContentType("text/html");
        editorPane.setText(html);
        editorPane.setCaretPosition(0);
    }

    private String buildHomePage() {
        return """
            <html>
            <head><style>
                body { background: #1c1c1c; color: #e0e0e0; font-family: Segoe UI, sans-serif; padding: 40px; text-align: center; }
                h1 { color: #0078D4; font-size: 48px; margin-bottom: 10px; }
                .search { width: 500px; padding: 12px; border-radius: 24px; border: 1px solid #444; background: #333; color: #fff; font-size: 16px; margin: 20px auto; }
                .nav { display: flex; justify-content: center; gap: 20px; margin: 30px 0; }
                a { color: #0078D4; text-decoration: none; margin: 0 10px; }
                a:hover { text-decoration: underline; }
            </style></head>
            <body>
                <h1>🌐 DunOS Browser</h1>
                <p style="color: #888;">Enter a URL above to browse the web</p>
                <div class="nav">
                    <a href='https://google.com'>Google</a>
                    <a href='https://duckduckgo.com'>DuckDuckGo</a>
                    <a href='https://wikipedia.org'>Wikipedia</a>
                    <a href='https://github.com'>GitHub</a>
                </div>
                <p style="color: #666; margin-top: 50px; font-size: 12px;">
                    DunDunDunOS Browser v1.0<br>
                    Simple HTML rendering via Swing
                </p>
            </body></html>
            """;
    }

    private String buildErrorPage(String url, String error) {
        return """
            <html><body style='background:#1c1c1c; color:#e0e0e0; font-family:sans-serif; padding:40px;'>
            <h2 style='color:#ff5555;'>⚠ Page could not be loaded</h2>
            <p style='color:#888;'>URL: """ + url + """</p>
            <p style='color:#666;'>Error: """ + error + """</p>
            <p><a href='#' style='color:#0078D4;'>Try again</a></p>
            </body></html>
            """;
    }

    private String getPageTitle(String url) {
        try {
            String domain = new java.net.URL(url).getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            return url;
        }
    }

    private void goBack() {
        if (historyIndex > 0) {
            historyIndex--;
            loadPage(history.get(historyIndex));
            addressBar.setText(history.get(historyIndex));
        }
    }

    private void goForward() {
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            loadPage(history.get(historyIndex));
            addressBar.setText(history.get(historyIndex));
        }
    }

    private void refreshPage() {
        if (historyIndex >= 0 && historyIndex < history.size()) {
            loadPage(history.get(historyIndex));
        }
    }

    private void goHome() {
        addressBar.setText("https://duckduckgo.com");
        navigateTo("https://duckduckgo.com");
    }

    private void toggleBookmark() {
        String url = addressBar.getText();
        if (bookmarks.containsKey(url)) {
            bookmarks.remove(url);
            JOptionPane.showMessageDialog(null, "Bookmark removed: " + url);
        } else {
            bookmarks.put(url, getPageTitle(url));
            JOptionPane.showMessageDialog(null, "Bookmark added: " + getPageTitle(url));
        }
    }
}

