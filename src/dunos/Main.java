package dunos;

import dunos.core.*;
import dunos.ui.*;
import dunos.system.*;
import dunos.util.*;

import javax.swing.*;
import java.awt.*;

/**
 * DunDunDunOS (DunOS) - Main Entry Point
 * 
 * This is the starting point of the entire operating system simulator.
 * It is responsible for:
 * - Initializing the kernel and core system services
 * - Loading fonts, themes, and configuration
 * - Initializing the file system
 * - Displaying the boot sequence splash screen
 * - Loading and registering all applications
 * - Launching the desktop environment
 * 
 * Usage:
 *   javac -d out src/dunos/Main.java src/dunos/core/*.java src/dunos/ui/*.java src/dunos/system/*.java src/dunos/util/*.java src/dunos/apps/*.java
 *   java -cp out dunos.Main
 */
public class Main {

    private static SplashScreen splash;
    private static BootSequence bootSeq;
    private static Desktop desktop;
    private static LoginScreen loginScreen;

    /**
     * Application entry point.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set system properties for better look and feel
        configureSystemProperties();

        // Initialize everything
        try {
            // Phase 1: Core system initialization
            initializeCore();

            // Phase 2: Display boot sequence
            showBootSequence();

            // Phase 3: Load and initialize all services
            loadServices();

            // Phase 4: Show splash screen with detailed progress
            showSplashScreen();

            // Phase 5: Initialize UI components
            initializeUI();

            // Phase 6: Initialize file system
            initializeFileSystem();

            // Phase 7: Register and initialize applications
            initializeApplications();

            // Phase 8: Complete initialization and transition to desktop
            completeBoot();

        } catch (Exception e) {
            handleFatalError(e);
        }
    }

    /**
     * Configures system properties for optimal appearance.
     */
    private static void configureSystemProperties() {
        try {
            // Set System Look and Feel
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // System properties
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            System.setProperty("sun.java2d.opengl", "true");
            System.setProperty("sun.java2d.d3d", "false");

            // Disable direct3D for better compatibility
            System.setProperty("sun.java2d.noddraw", "true");

        } catch (Exception e) {
            System.err.println("[Main] Could not configure look and feel: " + e.getMessage());
        }
    }

    /**
     * Phase 1: Initializes the core system components.
     */
    private static void initializeCore() {
        System.out.println("[Main] Initializing DunDunDunOS core...");

        // Initialize Kernel (singleton)
        Kernel kernel = Kernel.getInstance();

        // Initialize Registry (singleton) with default settings
        Registry registry = Registry.getInstance();
        registry.set("system.bootCount", registry.getInt("system.bootCount", 0) + 1);

        // Initialize EventBus (singleton)
        EventBus eventBus = EventBus.getInstance();

        // Initialize ServiceManager
        ServiceManager serviceManager = ServiceManager.getInstance();

        // Register core services
        Kernel.getInstance().registerService(serviceManager);

        System.out.println("[Main] Core initialized successfully.");
    }

    /**
     * Phase 2: Displays the text boot sequence.
     */
    private static void showBootSequence() {
        bootSeq = new BootSequence();

        // Update boot count
        Registry registry = Registry.getInstance();
        int bootCount = registry.getInt("system.bootCount", 0);

        // Custom boot messages
        String[] bootMessages = {
            "DunDunDunOS v1.0 - Boot #" + bootCount,
            "",
            "[KERNEL] Initializing kernel... OK",
            "[KERNEL] Loading system drivers... OK",
            "[KERNEL] Initializing registry... OK",
            "[KERNEL] Mounting file systems... OK",
            "[KERNEL] Starting service manager... OK",
            "[SERVICES] Loading theme service... OK",
            "[SERVICES] Loading audio service... OK",
            "[SERVICES] Loading notification service... OK",
            "[SERVICES] Loading update service... OK",
            "[DESKTOP] Initializing display... OK",
            "[DESKTOP] Loading desktop environment... OK",
            "[DESKTOP] Starting window manager... OK",
            "[APPS] Loading applications... OK",
            "[SHELL] Launching explorer... OK",
            "",
            "DunDunDunOS is ready.",
            "Starting desktop environment..."
        };

        // Override default boot messages if needed
        bootSeq = new BootSequence(bootMessages);

        // Show boot sequence, then continue
        bootSeq.start(() -> {
            bootSeq.dismiss();
            // Continue to splash
        });
    }

    /**
     * Phase 3: Loads and initializes system services.
     */
    private static void loadServices() {
        // Register services with the kernel
        Kernel kernel = Kernel.getInstance();

        // Theme manager service
        ThemeManager themeManager = ThemeManager.getInstance();

        // Image loader
        ImageLoader imageLoader = ImageLoader.getInstance();

        // File system manager
        FileSystemManager fsManager = FileSystemManager.getInstance();

        // Register services for lifecycle management
        kernel.registerService(new Kernel.Service() {
            public String getName() { return "ThemeManager"; }
            public void init() { themeManager.init(); }
            public void start() {}
            public void stop() {}
            public boolean isRunning() { return true; }
        });

        kernel.registerService(new Kernel.Service() {
            public String getName() { return "ImageLoader"; }
            public void init() {}
            public void start() {}
            public void stop() {}
            public boolean isRunning() { return true; }
        });

        kernel.registerService(new Kernel.Service() {
            public String getName() { return "FileSystem"; }
            public void init() { 
                fsManager.init(); 
                System.out.println("[Main] File system initialized.");
            }
            public void start() {}
            public void stop() {}
            public boolean isRunning() { return true; }
        });
    }

    /**
     * Phase 4: Shows the splash screen with animated progress.
     */
    private static void showSplashScreen() throws Exception {
        // Display splash screen
        splash = new SplashScreen();

        // Listen for kernel boot progress
        Kernel.getInstance().addBootListener(new Kernel.BootListener() {
            @Override
            public void onBootProgress(String message, int percent) {
                splash.setStatus(message);
                if (percent >= 0) {
                    splash.setProgress(percent);
                }
            }

            @Override
            public void onBootComplete() {
                // Transition to desktop
                splash.setStatus("Done!");
                splash.setSubtitle("Welcome to DunDunDunOS");
            }

            @Override
            public void onBootFailed(String error) {
                splash.setStatus("Error: " + error);
            }
        });

        // Show splash with fade-in
        splash.showSplash();

        // Simulate loading sequence with progress updates
        String[] loadingSteps = {
            "Loading Fonts...",
            "Loading Images...",
            "Loading Applications...",
            "Loading Kernel...",
            "Loading Explorer...",
            "Loading Desktop...",
            "Loading Taskbar...",
            "Loading Widgets...",
            "Loading File System...",
            "Loading Themes...",
            "Loading Settings...",
            "Loading Registry...",
            "Done!"
        };

        for (int i = 0; i < loadingSteps.length; i++) {
            Thread.sleep(150); // Simulate loading time
            splash.setStatus(loadingSteps[i]);
            splash.setProgress((i * 100) / (loadingSteps.length - 1));
        }

        Thread.sleep(300); // Brief pause at 100%
    }

    /**
     * Phase 5: Initializes the UI components.
     */
    private static void initializeUI() {
        // Initialize theme manager (loads saved theme)
        ThemeManager.getInstance().init();

        // Start animation engine
        AnimationEngine.getInstance().start();

        // Initialize Window Manager
        WindowManager.getInstance();

        System.out.println("[Main] UI components initialized.");
    }

    /**
     * Phase 6: Initializes the file system structure.
     */
    private static void initializeFileSystem() {
        FileSystemManager fs = FileSystemManager.getInstance();
        fs.init();
        System.out.println("[Main] File system ready.");
    }

    /**
     * Phase 7: Registers and initializes all applications.
     */
    private static void initializeApplications() {
        System.out.println("[Main] Registering applications...");

        // Application registration is handled through the AppLauncher
        // All application classes are available for launching

        System.out.println("[Main] " + getAppCount() + " applications registered.");
    }

    /**
     * Phase 8: Completes boot and transitions to the desktop.
     */
    private static void completeBoot() {
        // Hide splash and show desktop
        splash.hideSplash(() -> {
            // Create and show login screen
            loginScreen = new LoginScreen();
            loginScreen.showLogin(() -> {
                // On successful login, show desktop
                showDesktop();
            });

            // If no password is set, auto-login
            String password = Registry.getInstance().getString("user.password", "");
            if (password.isEmpty()) {
                // Auto-login after a brief delay
                Timer timer = new Timer(500, e -> {
                    if (loginScreen.isVisible()) {
                        loginScreen.setVisible(false);
                        loginScreen.dispose();
                        showDesktop();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    /**
     * Shows the main desktop environment.
     */
    private static void showDesktop() {
        // Create the main frame
        JFrame frame = new JFrame("DunDunDunOS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);

        // Create desktop
        desktop = new Desktop();
        frame.setContentPane(desktop);

        // Set application icon
        try {
            ImageIcon icon = new ImageIcon("Images/DunDun.jpg");
            if (icon.getImage() != null) {
                frame.setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            // No icon available, use default
        }

        // Show the frame
        frame.setVisible(true);

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Kernel.getInstance().shutdown();
        }));

        System.out.println("[Main] DunDunDunOS desktop ready.");
    }

    /**
     * Returns the count of registered applications.
     */
    private static int getAppCount() {
        return 20; // Number of application classes
    }

    /**
     * Handles fatal errors during boot.
     */
    private static void handleFatalError(Exception e) {
        System.err.println("[Main] FATAL ERROR: " + e.getMessage());
        e.printStackTrace();

        JOptionPane.showMessageDialog(null,
            "DunDunDunOS encountered a fatal error during boot:\n\n" +
            e.getMessage() + "\n\nPlease check the logs for details.",
            "DunDunDunOS - Fatal Error",
            JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }
}
           
