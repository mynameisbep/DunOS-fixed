package dunos.apps;

import dunos.ui.*;
import dunos.ui.Window;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * DunDunDunOS Paint - A simple drawing application with brushes, colors,
 * shapes, and canvas support. Basic but functional paint program.
 */
public class PaintApp {

    private BufferedImage canvas;
    private Graphics2D graphics;
    private Color currentColor;
    private int brushSize;
    private int lastX, lastY;
    private boolean drawing;
    private JPanel canvasPanel;
    private JLabel statusBar;
    private String currentTool;

    public PaintApp() {
        this.canvas = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
        this.graphics = canvas.createGraphics();
        this.currentColor = Color.WHITE;
        this.brushSize = 4;
        this.drawing = false;
        this.currentTool = "pencil";

        // Initialize canvas
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(30, 30, 30));
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.setColor(currentColor);
        graphics.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    public Window createWindow() {
        Window window = new Window("Paint", "paint", null);
        window.setSize(900, 600);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setOpaque(false);

        String[] tools = {"✏ Pencil", "🖌 Brush", "📏 Line", "⬜ Rectangle", "⭕ Oval", "🪣 Fill"};
        for (String tool : tools) {
            JButton btn = new JButton(tool);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setForeground(new Color(200, 200, 200));
            btn.setBackground(new Color(45, 45, 45));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                new EmptyBorder(4, 8, 4, 8)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            String t = tool;
            btn.addActionListener(e -> currentTool = t.split(" ")[1].toLowerCase());
            toolbar.add(btn);
        }

        // Color buttons
        Color[] colors = {Color.WHITE, Color.RED, Color.ORANGE, Color.YELLOW, 
                          Color.GREEN, Color.BLUE, Color.MAGENTA, Color.BLACK};
        toolbar.add(new JLabel("  "));
        for (Color c : colors) {
            JButton colorBtn = new JButton();
            colorBtn.setPreferredSize(new Dimension(24, 24));
            colorBtn.setBackground(c);
            colorBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
            colorBtn.setFocusPainted(false);
            colorBtn.addActionListener(e -> {
                currentColor = c;
                graphics.setColor(c);
            });
            toolbar.add(colorBtn);
        }

        // Brush size slider
        toolbar.add(new JLabel("  Size:"));
        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, brushSize);
        sizeSlider.setPreferredSize(new Dimension(100, 20));
        sizeSlider.setOpaque(false);
        sizeSlider.addChangeListener(e -> {
            brushSize = sizeSlider.getValue();
            graphics.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        });
        toolbar.add(sizeSlider);

        // Clear button
        JButton clearBtn = new JButton("🗑 Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearBtn.setForeground(new Color(200, 200, 200));
        clearBtn.setBackground(new Color(60, 45, 45));
        clearBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 60, 60)),
            new EmptyBorder(4, 8, 4, 8)
        ));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearCanvas());
        toolbar.add(clearBtn);

        content.add(toolbar, BorderLayout.NORTH);

        // Canvas
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
            }
        };
        canvasPanel.setPreferredSize(new Dimension(800, 500));
        canvasPanel.setBackground(new Color(40, 40, 40));
        canvasPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drawing = true;
                lastX = e.getX();
                lastY = e.getY();
                if (currentTool.equals("fill")) {
                    floodFill(e.getX(), e.getY(), currentColor);
                    canvasPanel.repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = false;
                if (currentTool.equals("line") || currentTool.equals("rectangle") || 
                    currentTool.equals("oval")) {
                    drawShape(e.getX(), e.getY());
                }
                statusBar.setText("Ready");
            }
        });

        canvasPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawing && (currentTool.equals("pencil") || currentTool.equals("brush"))) {
                    graphics.drawLine(lastX, lastY, e.getX(), e.getY());
                    lastX = e.getX();
                    lastY = e.getY();
                    canvasPanel.repaint();
                    statusBar.setText("Drawing at " + e.getX() + ", " + e.getY());
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                statusBar.setText("X: " + e.getX() + "  Y: " + e.getY());
            }
        });

        JScrollPane scrollPane = new JScrollPane(canvasPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        content.add(scrollPane, BorderLayout.CENTER);

        // Status bar
        statusBar = new JLabel("Ready");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.setForeground(new Color(150, 150, 150));
        statusBar.setBorder(new EmptyBorder(2, 8, 2, 8));
        content.add(statusBar, BorderLayout.SOUTH);

        return window;
    }

    private void drawShape(int x, int y) {
        int minX = Math.min(lastX, x);
        int minY = Math.min(lastY, y);
        int w = Math.abs(x - lastX);
        int h = Math.abs(y - lastY);

        switch (currentTool) {
            case "line" -> {
                graphics.drawLine(lastX, lastY, x, y);
            }
            case "rectangle" -> {
                graphics.drawRect(minX, minY, w, h);
            }
            case "oval" -> {
                graphics.drawOval(minX, minY, w, h);
            }
        }
        canvasPanel.repaint();
    }

    private void floodFill(int x, int y, Color fillColor) {
        if (x < 0 || x >= canvas.getWidth() || y < 0 || y >= canvas.getHeight()) return;

        Color targetColor = new Color(canvas.getRGB(x, y), true);
        if (targetColor.equals(fillColor)) return;

        // Simple BFS flood fill
        java.util.Queue<Point> queue = new java.util.LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (p.x < 0 || p.x >= canvas.getWidth() || p.y < 0 || p.y >= canvas.getHeight()) continue;
            Color c = new Color(canvas.getRGB(p.x, p.y), true);
            if (!c.equals(targetColor)) continue;

            canvas.setRGB(p.x, p.y, fillColor.getRGB());
            queue.add(new Point(p.x + 1, p.y));
            queue.add(new Point(p.x - 1, p.y));
            queue.add(new Point(p.x, p.y + 1));
            queue.add(new Point(p.x, p.y - 1));
        }
    }

    private void clearCanvas() {
        graphics.setColor(new Color(30, 30, 30));
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.setColor(currentColor);
        canvasPanel.repaint();
    }
}

