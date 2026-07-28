package dunos.core;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * DunDunDunOS Registry - A hierarchical key-value store for system configuration.
 * Manages settings, preferences, and system-wide configuration data.
 * Thread-safe and supports serialization to disk.
 */
public class Registry {

    private static volatile Registry instance;
    private final ConcurrentHashMap<String, Object> store;
    private final ConcurrentHashMap<String, Long> modifiedTimestamps;
    private final File registryFile;
    private boolean autoSave;

    private Registry() {
        this.store = new ConcurrentHashMap<>();
        this.modifiedTimestamps = new ConcurrentHashMap<>();
        this.registryFile = new File("Config/registry.dat");
        this.autoSave = true;
        loadDefaults();
        loadFromDisk();
    }

    /**
     * Gets the singleton Registry instance.
     */
    public static Registry getInstance() {
        if (instance == null) {
            synchronized (Registry.class) {
                if (instance == null) {
                    instance = new Registry();
                }
            }
        }
        return instance;
    }

    /**
     * Sets a registry value.
     */
    public void set(String key, Object value) {
        store.put(key, value);
        modifiedTimestamps.put(key, System.currentTimeMillis());
        if (autoSave) {
            saveToDisk();
        }
    }

    /**
     * Gets a registry value by key, returning null if not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) store.get(key);
    }

    /**
     * Gets a registry value with a default fallback.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) store.getOrDefault(key, defaultValue);
    }

    /**
     * Gets a string value from the registry.
     */
    public String getString(String key, String defaultValue) {
        Object value = store.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Gets an integer value from the registry.
     */
    public int getInt(String key, int defaultValue) {
        Object value = store.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return value != null ? Integer.parseInt(value.toString()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets a boolean value from the registry.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = store.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }

    /**
     * Gets a double value from the registry.
     */
    public double getDouble(String key, double defaultValue) {
        Object value = store.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return value != null ? Double.parseDouble(value.toString()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Checks if a key exists in the registry.
     */
    public boolean containsKey(String key) {
        return store.containsKey(key);
    }

    /**
     * Removes a key from the registry.
     */
    public void remove(String key) {
        store.remove(key);
        modifiedTimestamps.remove(key);
        if (autoSave) {
            saveToDisk();
        }
    }

    /**
     * Gets all keys that start with the given prefix.
     */
    public Set<String> getKeysWithPrefix(String prefix) {
        Set<String> result = new HashSet<>();
        for (String key : store.keySet()) {
            if (key.startsWith(prefix)) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * Gets the timestamp when a key was last modified.
     */
    public long getLastModified(String key) {
        return modifiedTimestamps.getOrDefault(key, 0L);
    }

    /**
     * Clears all registry entries.
     */
    public void clear() {
        store.clear();
        modifiedTimestamps.clear();
        if (autoSave) {
            saveToDisk();
        }
    }

    /**
     * Enables or disables automatic saving to disk.
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * Saves the registry to disk.
     */
    public synchronized void saveToDisk() {
        try {
            registryFile.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(registryFile))) {
                oos.writeObject(new HashMap<>(store));
                oos.writeObject(new HashMap<>(modifiedTimestamps));
            }
        } catch (IOException e) {
            System.err.println("[Registry] Failed to save: " + e.getMessage());
        }
    }

    /**
     * Loads the registry from disk.
     */
    @SuppressWarnings("unchecked")
    public synchronized void loadFromDisk() {
        if (!registryFile.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(registryFile))) {
            Map<String, Object> saved = (Map<String, Object>) ois.readObject();
            store.putAll(saved);
            try {
                Map<String, Long> timestamps = (Map<String, Long>) ois.readObject();
                modifiedTimestamps.putAll(timestamps);
            } catch (Exception ignored) {}
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Registry] Failed to load: " + e.getMessage());
        }
    }

    /**
     * Loads default registry values.
     */
    private void loadDefaults() {
        // Desktop settings
        set("desktop.wallpaper", "default");
        set("desktop.theme", "dark");
        set("desktop.accentColor", "#0078D4");
        set("desktop.iconSize", 48);
        set("desktop.showIcons", true);
        set("desktop.virtualDesktops", 2);

        // Taskbar settings
        set("taskbar.alignment", "center");
        set("taskbar.autoHide", false);
        set("taskbar.showClock", true);
        set("taskbar.showBattery", true);
        set("taskbar.showNetwork", true);
        set("taskbar.showVolume", true);

        // Theme settings
        set("theme.mode", "dark");
        set("theme.glassEffect", true);
        set("theme.blurIntensity", 15);
        set("theme.roundedCorners", true);
        set("theme.cornerRadius", 8);
        set("theme.shadowEnabled", true);
        set("theme.transparency", 0.85);

        // Animation settings
        set("animations.enabled", true);
        set("animations.speed", 1.0);
        set("animations.fadeIn", true);
        set("animations.fadeOut", true);

        // Performance settings
        set("performance.threadPoolSize", 4);
        set("performance.cacheEnabled", true);
        set("performance.lazyLoading", true);

        // Sound settings
        set("sound.enabled", true);
        set("sound.volume", 0.7);
        set("sound.startup", true);
        set("sound.shutdown", true);

        // System settings
        set("system.language", "en");
        set("system.autoUpdate", false);
        set("system.lastShutdown", "clean");
        set("system.bootCount", 0);

        // User settings
        set("user.name", "User");
        set("user.avatar", null);
        set("user.password", "");
        set("user.autoLogin", false);

        // Application defaults
        set("apps.calculator.mode", "standard");
        set("apps.browser.homepage", "https://duckduckgo.com");
        set("apps.explorer.defaultView", "grid");
        set("apps.terminal.backgroundColor", "#1E1E1E");
        set("apps.terminal.foregroundColor", "#FFFFFF");
        set("apps.terminal.fontSize", 14);
    }
}

