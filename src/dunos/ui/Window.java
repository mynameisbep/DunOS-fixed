package dunos.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * DunDunDunOS Window - Base class for all application windows.
 * Provides rounded corners, shadows, resize, move, minimize, maximize, close,
 * and theme-aware styling. Mimics Windows 11 window behavior.
 */
public class Window extends JPanel {

    protected final String title;
    protected final String appId;
    protected final ImageIcon icon;
    protected JPanel titleBar;
    protected JPanel contentArea;
    protected JLabel titleLabel;
    protected JButton closeBtn, maximizeBtn, minimizeBtn;
    protected boolean maximized;
    protected boolean minimized;
    protected Rectangle normalBounds;
    protected Point dragOffset;
    protected boolean dragging;
    protected boolean resizing;
    protected int resizeEdge;
    protected static final int RESIZE_MARGIN = 6;
    protected static final int TITLE_BAR_HEIGHT = 32;
    protected static final int CORNER_RADIUS = 8;
    protected static final int SHADOW_SIZE = 10;

    // Snap layout constants
    public enum SnapPosition {
        NONE, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, FULLSCREEN
    }

    protected SnapPosition snapPosition = SnapPosition.NONE;
    protected Consumer<Window> onClose;
    protected Consumer<Window> onMinimize;
    protected Consumer<Window> onMaximize;
    protected boolean closable = true;
    protected boolean resizable = true;
    protected boolean minimizable = true;
    protected boolean maximizable = true;

    /**
     * Creates a new window with the specified title and app ID.
     */
    public Window(String title, String appId, ImageIcon icon) {
        this.title = title;
        this.appId = appId;
        this.icon = icon;
        this.maximized = false;
        this.minimized = false;
        this.normalBounds = null;
        this.dragging = false;
        this.resizing = false;

        setLayout(new BorderLayout());
        setOpaque(false);

        Theme theme = ThemeManager.getInstance().getCurrentTheme();
        setupTitleBar(theme);
        setupContentArea(theme);
        setupListeners();

        // Default size
        setSize(800, 600);
    }

    /**
     * Creates a window with title only.
     */
    public Window(String title, String appId) {
        this(title, appId, null);
    }

    private void setupTitleBar(Theme theme) {
        titleBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Title bar background
                g2d.setColor(theme.getSurface());
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Bottom border
                g2d.setColor(theme.getBorder());
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

                g2d.dispose();
            }
        };
        titleBar.setLayout(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(0, TITLE_BAR_HEIGHT));
        titleBar.setMinimumSize(new Dimension(0, TITLE_BAR_HEIGHT));

        // Title label
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setPreferredSize(new Dimension(18, 18));
            titlePanel.add(iconLabel);
        }

        titleLabel = new JLabel(title);
        titleLabel.setFont(theme.getSmallFont().deriveFont(Font.PLAIN, 12));
        titleLabel.setForeground(theme.getText());
        titlePanel.add(titleLabel);

        // Window control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        buttonPanel.setOpaque(false);

        minimizeBtn = createControlButton("─", theme, e -> minimize());
        maximizeBtn = createControlButton("□", theme, e -> toggleMaximize());
        closeBtn = createControlButton("✕", theme, e -> close());

        // Style close button differently
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setBackground(new Color(232, 17, 35));
                closeBtn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setBackground(new Color(60, 60, 60));
                closeBtn.setForeground(theme.getText());
            }
        });

        buttonPanel.add(minimizeBtn);
        buttonPanel.add(maximizeBtn);
        buttonPanel.add(closeBtn);

        titleBar.add(titlePanel, BorderLayout.WEST);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        // Make title bar draggable
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && !maximized) {
                    dragging = true;
                    dragOffset = SwingUtilities.convertPoint(titleBar, e.getPoint(), 
                        (Container) Window.this);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging && !maximized) {
                    Point p = SwingUtilities.convertPoint(titleBar, e.getPoint(), 
                        (Container) getParent());
                    if (getParent() != null) {
                        setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
                    }
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleMaximize();
                }
            }
        };

        titleBar.addMouseListener(dragAdapter);
        titleBar.addMouseMotionListener(dragAdapter);

        add(titleBar, BorderLayout.NORTH);
    }

    private void setupContentArea(Theme theme) {
        contentArea = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(theme.getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.dispose();
            }
        };
        contentArea.setOpaque(false);
        add(contentArea, BorderLayout.CENTER);
    }

    private JButton createControlButton(String text, Theme theme, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(theme.getText());
        btn.setBackground(new Color(60, 60, 60));
        btn.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(36, TITLE_BAR_HEIGHT - 4));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(80, 80, 80));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60));
            }
        });

        return btn;
    }

    private void setupListeners() {
        // Resize listener
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!resizable || maximized) return;
                setResizeCursor(e.getPoint());
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (resizing && resizable && !maximized) {
                    handleResize(e);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!resizable || maximized) return;
                resizeEdge = getResizeEdge(e.getPoint());
                if (resizeEdge != 0) {
                    resizing = true;
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                resizing = false;
            }
        });
    }

    private void setResizeCursor(Point p) {
        int edge = getResizeEdge(p);
        switch (edge) {
            case 1: case 3: setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)); break;
            case 2: case 6: setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)); break;
            case 4: case 8: setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)); break;
            case 5: case 7: setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)); break;
            default: setCursor(Cursor.getDefaultCursor()); break;
        }
    }

    private int getResizeEdge(Point p) {
        int w = getWidth(), h = getHeight();
        boolean left = p.x < RESIZE_MARGIN;
        boolean right = p.x > w - RESIZE_MARGIN;
        boolean top = p.y < RESIZE_MARGIN;
        boolean bottom = p.y > h - RESIZE_MARGIN;

        if (top && left) return 1;
        if (top && right) return 5;
        if (bottom && left) return 3;
        if (bottom && right) return 7;
        if (top) return 2;
        if (bottom) return 6;
        if (left) return 4;
        if (right) return 8;
        return 0;
    }

    private void handleResize(MouseEvent e) {
        // Basic resize handling - WindowManager should handle the actual geometry
        WindowManager.getInstance().notifyResize(this, e);
    }

    /**
     * Gets the content area for adding application components.
     */
    public JPanel getContentArea() {
        return contentArea;
    }

    /**
     * Gets the title bar panel.
     */
    public JPanel getTitleBar() {
        return titleBar;
    }

    /**
     * Gets the app ID for this window.
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Gets the window title.
     */
    public String getWindowTitle() {
        return title;
    }

    // Window actions

    public void minimize() {
        minimized = true;
        setVisible(false);
        if (onMinimize != null) onMinimize.accept(this);
        WindowManager.getInstance().notifyMinimize(this);
    }

    public void toggleMaximize() {
        if (maximized) {
            restore();
        } else {
            maximize();
        }
    }

    public void maximize() {
        if (!maximizable) return;
        maximized = true;
        normalBounds = getBounds();
        WindowManager.getInstance().notifyMaximize(this);
        if (onMaximize != null) onMaximize.accept(this);
    }

    public void restore() {
        if (maximized && normalBounds != null) {
            maximized = false;
            setBounds(normalBounds);
            normalBounds = null;
            WindowManager.getInstance().notifyRestore(this);
        }
    }

    public void close() {
        if (!closable) return;
        WindowManager.getInstance().notifyClose(this);
        if (onClose != null) onClose.accept(this);
    }

    /**
     * Shows this window with a fade animation.
     */
    public void showWithAnimation() {
        AnimationEngine ae = AnimationEngine.getInstance();
        setVisible(true);
        ae.fadeIn(this, 200);
    }

    /**
     * Hides this window with a fade animation.
     */
    public void hideWithAnimation() {
        AnimationEngine ae = AnimationEngine.getInstance();
        ae.fadeOut(this, 150);
    }

    /**
     * Brings this window to the front.
     */
    public void bringToFront() {
        WindowManager.getInstance().bringToFront(this);
    }

    /**
     * Updates the theme on this window.
     */
    public void updateTheme(Theme theme) {
        titleLabel.setForeground(theme.getText());
        minimizeBtn.setForeground(theme.getText());
        maximizeBtn.setForeground(theme.getText());
        closeBtn.setForeground(theme.getText());
        minimizeBtn.setBackground(new Color(60, 60, 60));
        maximizeBtn.setBackground(new Color(60, 60, 60));
        closeBtn.setBackground(new Color(60, 60, 60));
        titleBar.repaint();
        contentArea.repaint();
    }

    // Callback setters
    public void setOnClose(Consumer<Window> onClose) { this.onClose = onClose; }
    public void setOnMinimize(Consumer<Window> onMinimize) { this.onMinimize = onMinimize; }
    public void setOnMaximize(Consumer<Window> onMaximize) { this.onMaximize = onMaximize; }
    public void setClosable(boolean closable) { this.closable = closable; }
    public void setResizable(boolean resizable) { this.resizable = resizable; }
    public void setMinimizable(boolean minimizable) { this.minimizable = minimizable; }
    public void setMaximizable(boolean maximizable) { this.maximizable = maximizable; }

    public boolean isMaximized() { return maximized; }
    public boolean isMinimized() { return minimized; }
    public void setMinimized(boolean m) { minimized = m; }
}

