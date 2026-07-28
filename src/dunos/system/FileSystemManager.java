package dunos.system;

import java.io.*;
import java.util.*;

/**
 * DunDunDunOS FileSystemManager - Manages the virtual file system structure.
 * Creates the initial folder hierarchy and provides file operations.
 * The file system mirrors the project structure on disk.
 */
public class FileSystemManager {

    private static volatile FileSystemManager instance;
    private final File rootDir;

    private static final String[] REQUIRED_DIRECTORIES = {
        "Desktop", "Documents", "Downloads", "Music", "Pictures",
        "Videos", "RecycleBin", "Fonts", "Config", "Temp", "Logs",
        "Users", "Users/Default", "Images/Wallpapers", "Images/Icons"
    };

    private FileSystemManager() {
        this.rootDir = new File(System.getProperty("user.dir"));
    }

    /**
     * Gets the singleton FileSystemManager instance.
     */
    public static FileSystemManager getInstance() {
        if (instance == null) {
            synchronized (FileSystemManager.class) {
                if (instance == null) {
                    instance = new FileSystemManager();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes the file system by creating required directories.
     */
    public void init() {
        createRequiredDirectories();
        createSampleFiles();
    }

    /**
     * Creates all required directories if they don't exist.
     */
    private void createRequiredDirectories() {
        for (String dir : REQUIRED_DIRECTORIES) {
            File directory = new File(rootDir, dir);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("[FS] Created directory: " + dir);
                } else {
                    System.err.println("[FS] Failed to create directory: " + dir);
                }
            }
        }
    }

    /**
     * Creates sample files for demonstration.
     */
    private void createSampleFiles() {
        createSampleFile("Desktop/readme.txt", "Welcome to DunDunDunOS!\nDouble-click icons to launch applications.");
        createSampleFile("Documents/sample.txt", "This is a sample text file.");
        createSampleFile("Documents/notes.txt", "DunOS Notes:\n- Modern desktop OS simulator\n- Java Swing based\n- Windows 11 inspired");
        createSampleFile("Pictures/wallpaper.txt", "Place wallpapers (.jpg, .png) in Images/Wallpapers/");
        createSampleFile("Users/Default/profile.txt", "User: Default\nTheme: Dark\nLast Login: " + new Date());
    }

    private void createSampleFile(String path, String content) {
        File file = new File(rootDir, path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(content);
                }
            } catch (IOException e) {
                System.err.println("[FS] Failed to create sample file: " + path);
            }
        }
    }

    /**
     * Lists contents of a directory.
     */
    public File[] listDirectory(String path) {
        File dir = new File(rootDir, path);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                Arrays.sort(files, (a, b) -> {
                    if (a.isDirectory() && !b.isDirectory()) return -1;
                    if (!a.isDirectory() && b.isDirectory()) return 1;
                    return a.getName().compareToIgnoreCase(b.getName());
                });
            }
            return files;
        }
        return new File[0];
    }

    /**
     * Gets the full path for a relative path.
     */
    public File getFile(String relativePath) {
        return new File(rootDir, relativePath);
    }

    /**
     * Gets the root directory.
     */
    public File getRootDir() {
        return rootDir;
    }

    /**
     * Creates a new directory.
     */
    public boolean createDirectory(String path) {
        File dir = new File(rootDir, path);
        return dir.mkdirs();
    }

    /**
     * Creates a new file.
     */
    public boolean createFile(String path) throws IOException {
        File file = new File(rootDir, path);
        return file.createNewFile();
    }

    /**
     * Deletes a file or directory.
     */
    public boolean delete(String path) {
        File file = new File(rootDir, path);
        return deleteRecursive(file);
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

    /**
     * Copies a file or directory.
     */
    public boolean copy(String sourcePath, String destPath) {
        File source = new File(rootDir, sourcePath);
        File dest = new File(rootDir, destPath);
        return copyRecursive(source, dest);
    }

    private boolean copyRecursive(File source, File dest) {
        if (source.isDirectory()) {
            if (!dest.mkdirs()) return false;
            File[] children = source.listFiles();
            if (children != null) {
                for (File child : children) {
                    copyRecursive(child, new File(dest, child.getName()));
                }
            }
            return true;
        } else {
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(dest)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    /**
     * Moves a file or directory.
     */
    public boolean move(String sourcePath, String destPath) {
        File source = new File(rootDir, sourcePath);
        File dest = new File(rootDir, destPath);
        return source.renameTo(dest);
    }

    /**
     * Renames a file or directory.
     */
    public boolean rename(String oldPath, String newName) {
        File file = new File(rootDir, oldPath);
        File renamed = new File(file.getParent(), newName);
        return file.renameTo(renamed);
    }

    /**
     * Gets the size of a file or directory.
     */
    public long getSize(String path) {
        File file = new File(rootDir, path);
        return getSizeRecursive(file);
    }

    private long getSizeRecursive(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    size += getSizeRecursive(child);
                }
            }
        } else {
            size = file.length();
        }
        return size;
    }

    /**
     * Searches for files matching a query.
     */
    public List<File> search(String query, String path) {
        List<File> results = new ArrayList<>();
        File dir = new File(rootDir, path);
        if (dir.exists() && dir.isDirectory()) {
            searchRecursive(dir, query.toLowerCase(), results);
        }
        return results;
    }

    private void searchRecursive(File dir, String query, List<File> results) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().contains(query)) {
                    results.add(file);
                }
                if (file.isDirectory()) {
                    searchRecursive(file, query, results);
                }
            }
        }
    }

    /**
     * Gets human-readable file size.
     */
    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.1f %s", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Gets the file extension.
     */
    public static String getExtension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1).toLowerCase() : "";
    }

    /**
     * Gets an icon emoji based on file type.
     */
    public static String getFileIcon(File file) {
        if (file.isDirectory()) return "📁";
        String ext = getExtension(file);
        return switch (ext) {
            case "txt", "md" -> "📄";
            case "java", "py", "js", "html", "css", "json", "xml" -> "📜";
            case "jpg", "jpeg", "png", "gif", "bmp" -> "🖼";
            case "mp3", "wav", "flac" -> "🎵";
            case "mp4", "avi", "mkv" -> "🎬";
            case "zip", "rar", "tar", "gz" -> "📦";
            case "pdf" -> "📕";
            case "exe", "jar" -> "⚙";
            default -> "📄";
        };
    }
}

