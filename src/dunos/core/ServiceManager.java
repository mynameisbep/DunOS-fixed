package dunos.core;

import dunos.core.Kernel.Service;
import java.util.*;
import java.util.concurrent.*;

/**
 * DunDunDunOS ServiceManager - Manages the lifecycle of all system services.
 * Handles service registration, dependency resolution, ordered startup/shutdown,
 * and health monitoring.
 */
public class ServiceManager implements Service {

    private static volatile ServiceManager instance;
    private final Map<String, ManagedService> services;
    private volatile boolean running;
    private final ScheduledExecutorService healthChecker;

    /**
     * Internal wrapper for managed services with metadata.
     */
    private static class ManagedService {
        final Service service;
        final List<String> dependencies;
        volatile boolean initialized;
        volatile boolean started;
        ServiceState state;

        ManagedService(Service service, List<String> dependencies) {
            this.service = service;
            this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
            this.state = ServiceState.STOPPED;
        }
    }

    /**
     * Enum representing the state of a managed service.
     */
    public enum ServiceState {
        STOPPED,
        INITIALIZING,
        INITIALIZED,
        STARTING,
        RUNNING,
        STOPPING,
        ERROR
    }

    public ServiceManager() {
        this.services = new LinkedHashMap<>();
        this.healthChecker = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ServiceHealthChecker");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Gets the singleton ServiceManager instance.
     */
    public static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                if (instance == null) {
                    instance = new ServiceManager();
                }
            }
        }
        return instance;
    }

    @Override
    public String getName() {
        return "ServiceManager";
    }

    @Override
    public void init() {
        // ServiceManager initializes itself
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        healthChecker.shutdown();
        // Stop all managed services
        List<ManagedService> reversed = new ArrayList<>(services.values());
        Collections.reverse(reversed);
        for (ManagedService ms : reversed) {
            if (ms.started) {
                try {
                    ms.service.stop();
                } catch (Exception e) {
                    System.err.println("[ServiceManager] Error stopping " + ms.service.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Registers a service with optional dependencies.
     */
    public void register(Service service, String... dependencies) {
        services.put(service.getName().toLowerCase(), 
            new ManagedService(service, Arrays.asList(dependencies)));
    }

    /**
     * Unregisters a service.
     */
    public void unregister(String name) {
        services.remove(name.toLowerCase());
    }

    /**
     * Gets a registered service by name.
     */
    public Service getService(String name) {
        ManagedService ms = services.get(name.toLowerCase());
        return ms != null ? ms.service : null;
    }

    /**
     * Gets the state of a service.
     */
    public ServiceState getServiceState(String name) {
        ManagedService ms = services.get(name.toLowerCase());
        return ms != null ? ms.state : ServiceState.STOPPED;
    }

    /**
     * Initializes all registered services in dependency order.
     */
    public void initializeAll() throws Exception {
        List<ManagedService> ordered = resolveDependencyOrder();
        for (ManagedService ms : ordered) {
            if (!ms.initialized) {
                try {
                    ms.state = ServiceState.INITIALIZING;
                    ms.service.init();
                    ms.initialized = true;
                    ms.state = ServiceState.INITIALIZED;
                } catch (Exception e) {
                    ms.state = ServiceState.ERROR;
                    throw new Exception("Failed to initialize " + ms.service.getName() + ": " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Starts all initialized services in dependency order.
     */
    public void startAll() throws Exception {
        List<ManagedService> ordered = resolveDependencyOrder();
        for (ManagedService ms : ordered) {
            if (ms.initialized && !ms.started) {
                try {
                    ms.state = ServiceState.STARTING;
                    ms.service.start();
                    ms.started = true;
                    ms.state = ServiceState.RUNNING;
                } catch (Exception e) {
                    ms.state = ServiceState.ERROR;
                    throw new Exception("Failed to start " + ms.service.getName() + ": " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Stops all running services in reverse dependency order.
     */
    public void stopAll() {
        List<ManagedService> ordered = resolveDependencyOrder();
        Collections.reverse(ordered);
        for (ManagedService ms : ordered) {
            if (ms.started) {
                try {
                    ms.state = ServiceState.STOPPING;
                    ms.service.stop();
                    ms.started = false;
                    ms.state = ServiceState.STOPPED;
                } catch (Exception e) {
                    ms.state = ServiceState.ERROR;
                    System.err.println("[ServiceManager] Error stopping " + ms.service.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Returns a list of all service names.
     */
    public List<String> getServiceNames() {
        return new ArrayList<>(services.keySet());
    }

    /**
     * Returns summary information about all services.
     */
    public Map<String, ServiceState> getServiceSummary() {
        Map<String, ServiceState> summary = new LinkedHashMap<>();
        for (Map.Entry<String, ManagedService> entry : services.entrySet()) {
            summary.put(entry.getKey(), entry.getValue().state);
        }
        return summary;
    }

    /**
     * Resolves the dependency order using topological sort.
     */
    private List<ManagedService> resolveDependencyOrder() {
        List<ManagedService> result = new ArrayList<>();
        Set<String> resolved = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String name : services.keySet()) {
            try {
                resolveDependencies(name, resolved, visiting, result);
            } catch (Exception e) {
                System.err.println("[ServiceManager] Dependency resolution error: " + e.getMessage());
            }
        }

        return result;
    }

    private void resolveDependencies(String name, Set<String> resolved, 
                                      Set<String> visiting, List<ManagedService> result) {
        if (resolved.contains(name)) return;
        if (visiting.contains(name)) {
            throw new RuntimeException("Circular dependency detected: " + name);
        }

        visiting.add(name);
        ManagedService ms = services.get(name);
        if (ms != null) {
            for (String dep : ms.dependencies) {
                resolveDependencies(dep, resolved, visiting, result);
            }
            result.add(ms);
        }
        visiting.remove(name);
        resolved.add(name);
    }
}

