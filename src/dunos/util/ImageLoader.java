package dunos.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * DunDunDunOS ImageLoader - Utility for loading, caching, and scaling images.
 * Supports lazy loading and background loading for performance.
 */
public class ImageLoader {

    private static volatile ImageLoader instance;
    private final Map<String, ImageIcon> cache;
    private final ExecutorService loadingExecutor;
    private boolean cacheEnabled;

    private ImageLoader() {
        this.cache = new ConcurrentHashMap<>();
        this.loadingExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "ImageLoader-Thread");
            t.setDaemon(true);
            return t;
        });
        this.cacheEnabled = true;
    }

    /**
     * Gets the singleton ImageLoader instance.
     */
    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    /**
     * Loads an image from a file path.
     */
    public ImageIcon loadImage(String path) {
        return loadImage(path, -1, -1);
    }

    /**
     * Loads and scales an image to the specified dimensions.
     */
    public ImageIcon loadImage(String path, int width, int height) {
        String cacheKey = path + "_" + width + "x" + height;

        if (cacheEnabled && cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        try {
            File file = new File(path);
            if (!file.exists()) return null;

            BufferedImage img = ImageIO.read(file);
            if (img == null) return null;

            ImageIcon icon;
            if (width > 0 && height > 0) {
                Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaled);
            } else {
                icon = new ImageIcon(img);
            }

            if (cacheEnabled) {
                cache.put(cacheKey, icon);
                // Limit cache size to avoid memory issues
                if (cache.size() > 100) {
                    cache.clear();
                }
            }

            return icon;
        } catch (IOException e) {
            System.err.println("[ImageLoader] Failed to load: " + path);
            return null;
        }
    }

    /**
     * Loads an image asynchronously and calls back when done.
     */
    public void loadImageAsync(String path, int width, int height, 
                                Consumer<ImageIcon> callback) {
        loadingExecutor.submit(() -> {
            ImageIcon icon = loadImage(path, width, height);
            if (callback != null && icon != null) {
                SwingUtilities.invokeLater(() -> callback.accept(icon));
            }
        });
    }

    /**
     * Creates a solid color icon.
     */
    public static ImageIcon createColorIcon(Color color, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * Creates a rounded rectangle icon with text.
     */
    public static ImageIcon createTextIcon(String text, Color bg, Color fg, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(bg);
        g2d.fillRoundRect(0, 0, size, size, size / 4, size / 4);
        g2d.setColor(fg);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * Enables or disables caching.
     */
    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
        if (!enabled) {
            cache.clear();
        }
    }

    /**
     * Clears the image cache.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Preloads all images from a directory.
     */
    public void preloadDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".png") || 
                       lower.endsWith(".gif") || lower.endsWith(".bmp");
            });
            if (files != null) {
                for (File file : files) {
                    loadImageAsync(file.getAbsolutePath(), 64, 64, null);
                }
            }
        }
    }

    /**
     * Consumer interface for async callbacks.
     */
    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T value);
    }
}

