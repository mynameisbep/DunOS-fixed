package dunos.core;

import dunos.ui.*;
import dunos.system.*;
import dunos.util.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * DunDunDunOS Kernel - The central core of the operating system.
 * Manages boot sequence, shutdown, service lifecycle, and system state.
 * Implements the Singleton pattern to ensure a single kernel instance.
 */
public class Kernel {

    private static volatile Kernel instance;
    private final Map<String, Service> services;
    private final List<BootListener> bootListeners;
    private volatile KernelState state;
    private final ExecutorService threadPool;
    private final Timer timer;

    /**
     * Represents the lifecycle states of the kernel.
     */
    public enum KernelState {
        POWERED_OFF,
        BOOTING,
        RUNNING,
        SHUTTING_DOWN,
        SLEEPING,
        RESTARTING
    }

    /**
     * Interface for services that can be registered with the kernel.
     */
    public interface Service {
        String getName();
        void init() throws Exception;
        void start() throws Exception;
        void stop() throws Exception;
        boolean isRunning();
    }

    /**
     * Listener interface for boot sequence events.
     */
    public interface BootListener {
        void onBootProgress(String message, int percent);
        void onBootComplete();
        void onBootFailed(String error);
    }

    private Kernel() {
        this.services = new ConcurrentHashMap<>();
        this.bootListeners = new ArrayList<>();
        this.state = KernelState.POWERED_OFF;
        this.threadPool = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "Kernel-Thread");
            t.setDaemon(true);
            return t;
        });
        this.timer = new Timer("Kernel-Timer", true);
    }

    /**
     * Gets the singleton kernel instance.
     */
    public static Kernel getInstance() {
        if (instance == null) {
            synchronized (Kernel.class) {
                if (instance == null) {
                    instance = new Kernel();
                }
            }
        }
        return instance;
    }

    /**
     * Returns the kernel's thread pool for async operations.
     */
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * Returns the kernel's timer for scheduled tasks.
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Registers a boot listener to receive boot progress updates.
     */
    public void addBootListener(BootListener listener) {
        synchronized (bootListeners) {
            bootListeners.add(listener);
        }
    }

    /**
     * Removes a boot listener.
     */
    public void removeBootListener(BootListener listener) {
        synchronized (bootListeners) {
            bootListeners.remove(listener);
        }
    }

    /**
     * Registers a service with the kernel.
     */
    public void registerService(Service service) {
        services.put(service.getName().toLowerCase(), service);
    }

    /**
     * Gets a registered service by name.
     */
    public Service getService(String name) {
        return services.get(name.toLowerCase());
    }

    /**
     * Returns all registered services.
     */
    public Collection<Service> getAllServices() {
        return services.values();
    }

    /**
     * Returns the current kernel state.
     */
    public KernelState getState() {
        return state;
    }

    /**
     * Boots the system: initializes kernel, loads services, and transitions to RUNNING.
     */
    public void boot() throws Exception {
        state = KernelState.BOOTING;
        fireBootProgress("Initializing Kernel...", 0);

        // Initialize core services in order
        String[] bootSequence = {
            "Initializing Kernel...",
            "Loading Drivers...",
            "Loading Registry...",
            "Loading Services...",
            "Loading Desktop...",
            "Launching Explorer...",
            "Done."
        };

        for (int i = 0; i < bootSequence.length; i++) {
            Thread.sleep(300); // Simulate boot time
            fireBootProgress(bootSequence[i], (i * 100) / (bootSequence.length - 1));
        }

        // Start all registered services
        int idx = 0;
        for (Service service : services.values()) {
            try {
                service.init();
                service.start();
                fireBootProgress("Starting " + service.getName() + "...", 
                    (idx * 100) / services.size());
                idx++;
            } catch (Exception e) {
                System.err.println("[Kernel] Failed to start service: " + service.getName() + " - " + e.getMessage());
            }
        }

        state = KernelState.RUNNING;
        fireBootProgress("Done!", 100);
        fireBootComplete();
    }

    /**
     * Initiates a graceful shutdown of the system.
     */
    public void shutdown() {
        if (state == KernelState.SHUTTING_DOWN || state == KernelState.POWERED_OFF) {
            return;
        }
        state = KernelState.SHUTTING_DOWN;

        threadPool.submit(() -> {
            // Stop services in reverse order
            List<Service> serviceList = new ArrayList<>(services.values());
            Collections.reverse(serviceList);
            for (Service service : serviceList) {
                try {
                    if (service.isRunning()) {
                        service.stop();
                    }
                } catch (Exception e) {
                    System.err.println("[Kernel] Error stopping service: " + service.getName());
                }
            }

            timer.cancel();
            threadPool.shutdown();
            state = KernelState.POWERED_OFF;
            System.exit(0);
        });
    }

    /**
     * Restarts the system.
     */
    public void restart() {
        state = KernelState.RESTARTING;
        threadPool.submit(() -> {
            shutdown();
            // In a real scenario we'd restart, but for the simulator we'll let shutdown handle it
        });
    }

    /**
     * Puts the system to sleep (suspends UI but keeps services running).
     */
    public void sleep() {
        state = KernelState.SLEEPING;
        fireBootProgress("System sleeping...", -1);
    }

    /**
     * Wakes the system from sleep.
     */
    public void wake() {
        if (state == KernelState.SLEEPING) {
            state = KernelState.RUNNING;
        }
    }

    private void fireBootProgress(String message, int percent) {
        synchronized (bootListeners) {
            for (BootListener listener : bootListeners) {
                try {
                    listener.onBootProgress(message, percent);
                } catch (Exception ignored) {}
            }
        }
    }

    private void fireBootComplete() {
        synchronized (bootListeners) {
            for (BootListener listener : bootListeners) {
                try {
                    listener.onBootComplete();
                } catch (Exception ignored) {}
            }
        }
    }

    private void fireBootFailed(String error) {
        synchronized (bootListeners) {
            for (BootListener listener : bootListeners) {
                try {
                    listener.onBootFailed(error);
                } catch (Exception ignored) {}
            }
        }
    }
}

