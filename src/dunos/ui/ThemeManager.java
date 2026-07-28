package dunos.ui;

import dunos.core.Registry;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * DunDunDunOS ThemeManager - Manages the current system theme and provides
 * listeners for theme change events. Supports theme switching, custom themes,
 * and persistent theme selection via the Registry.
 */
public class ThemeManager {

    private static volatile ThemeManager instance;
    private Theme currentTheme;
    private final Map<String, Theme> customThemes;
    private final List<ThemeChangeListener> listeners;
    private boolean initialized;

    /**
     * Interface for receiving theme change notifications.
     */
    public interface ThemeChangeListener {
        void onThemeChanged(Theme oldTheme, Theme newTheme);
    }

    private ThemeManager() {
        this.customThemes = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.currentTheme = Theme.dark();
    }

    /**
     * Gets the singleton ThemeManager instance.
     */
    public static ThemeManager getInstance() {
        if (instance == null) {
            synchronized (ThemeManager.class) {
                if (instance == null) {
                    instance = new ThemeManager();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes the theme manager by loading the saved theme.
     */
    public void init() {
        if (initialized) return;
        Registry registry = Registry.getInstance();
        String themeName = registry.getString("desktop.theme", "dark");
        applyTheme(themeName);
        initialized = true;
    }

    /**
     * Gets the current active theme.
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Gets the current accent color from the theme.
     */
    public Color getAccentColor() {
        return currentTheme.getAccent();
    }

    /**
     * Applies a theme by name.
     */
    public void applyTheme(String name) {
        Theme oldTheme = currentTheme;
        Theme newTheme;

        if (customThemes.containsKey(name.toLowerCase())) {
            newTheme = customThemes.get(name.toLowerCase());
        } else {
            newTheme = Theme.fromName(name);
        }

        if (newTheme != null) {
            currentTheme = newTheme;
            Registry.getInstance().set("desktop.theme", name);
            notifyListeners(oldTheme, newTheme);
        }
    }

    /**
     * Applies a custom Theme object directly.
     */
    public void applyTheme(Theme theme) {
        Theme oldTheme = currentTheme;
        currentTheme = theme;
        Registry.getInstance().set("desktop.theme", theme.getName().toLowerCase());
        customThemes.put(theme.getName().toLowerCase(), theme);
        notifyListeners(oldTheme, theme);
    }

    /**
     * Registers a custom theme by name.
     */
    public void registerCustomTheme(String name, Theme theme) {
        customThemes.put(name.toLowerCase(), theme);
    }

    /**
     * Gets a list of all available theme names.
     */
    public List<String> getAvailableThemes() {
        List<String> themes = new ArrayList<>(Arrays.asList(Theme.getThemeNames()));
        themes.addAll(customThemes.keySet());
        return themes;
    }

    /**
     * Adds a theme change listener.
     */
    public void addListener(ThemeChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a theme change listener.
     */
    public void removeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Theme oldTheme, Theme newTheme) {
        for (ThemeChangeListener listener : listeners) {
            try {
                listener.onThemeChanged(oldTheme, newTheme);
            } catch (Exception e) {
                System.err.println("[ThemeManager] Listener error: " + e.getMessage());
            }
        }
    }
}

