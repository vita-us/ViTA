package de.unistuttgart.vis.vita.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * The state maintained by the AnalyisController for the execution of a module
 */
public class ModuleExecutionState {
  private ModuleClass clazz;
  private Module<?> instance;
  private Set<ModuleClass> remainingDependencies;
  private ModuleResultProviderImpl resultProvider;
  private boolean isExecutable;
  private Thread thread;
  private Map<ModuleClass, Double> progressMap;
  private Set<ModuleClass> directAndIndirectDependencies;
  private double currentProgress;

  /**
   * Constructs a new execution state from a module class and its dependencies
   * 
   * @param clazz the module class
   * @param optionalInstance an instance of the module, or {@code null} to construct a new one
   * @param dependencies the direct dependencies
   * @param directAndIndirectDependencies the dependencies and their dependencies, recursively
   */
  public ModuleExecutionState(ModuleClass clazz, Module<?> optionalInstance,
      Set<ModuleClass> dependencies, Set<ModuleClass> directAndIndirectDependencies) {
    this.clazz = clazz;
    this.instance = optionalInstance;
    this.directAndIndirectDependencies = new HashSet<>(directAndIndirectDependencies);
    remainingDependencies = new HashSet<>(dependencies);
    resultProvider = new ModuleResultProviderImpl();
    isExecutable = remainingDependencies.isEmpty();
    progressMap = new HashMap<>();

    // Initialize progress
    for (ModuleClass dependency : directAndIndirectDependencies) {
      progressMap.put(dependency, 0.);
    }
    progressMap.put(clazz, 0.0);
    currentProgress = 0;
  }

  /**
   * Gets the dependencies of this module and its dependencies, recursively
   * 
   * @return the dependencies as unmodifiable set
   */
  public Set<ModuleClass> getDirectAndIndirectDependencies() {
    return Collections.unmodifiableSet(directAndIndirectDependencies);
  }

  /**
   * Indicates whether all dependencies have been finished and this module can execute
   * 
   * @return true, if this module can execute, false if it must wait for dependencies
   */
  public boolean isExecutable() {
    return isExecutable;
  }

  /**
   * Get the instance of this execution for the module
   * 
   * @return the instance
   */
  public Module<?> getInstance() {
    if (instance == null) {
      instance = clazz.newInstance();
    }
    return instance;
  }

  /**
   * Gets the ModuleClasses that are blocking this module from being executable
   * 
   * @return an unmodifiable view to the set.
   */
  public Set<ModuleClass> getRemainingDependencies() {
    return Collections.unmodifiableSet(remainingDependencies);
  }

  public ModuleClass getModuleClass() {
    return clazz;
  }

  /**
   * Should be called when the module itself, a direct or indirect dependency has successfully finished
   * 
   * @param module the module which has finished
   * @param result the result of the module
   */
  public void notifyModuleFinished(ModuleClass module, Object result) {
    if (!module.getResultClass().isAssignableFrom(result.getClass())) {
      throw new IllegalArgumentException(
          "The provided result is not assignable to the claimed module's result type");
    }
    
    putProgress(module, 1.0);

    synchronized (remainingDependencies) {
      if (!remainingDependencies.contains(module)) {
        return;
      }

      resultProvider.put(module.getResultClass(), result);
      remainingDependencies.remove(module);

      if (remainingDependencies.isEmpty()) {
        isExecutable = true;
      }
    }

    getInstance().dependencyFinished(module.getResultClass(), result);
  }

  /**
   * Should be called when a the module itself, a direct or indirect dependency reports its progress
   * 
   * @param module the module whose progress has changed
   * @param progress the new progress of that module
   */
  public void notifyModuleProgress(ModuleClass module, double progress) {
    putProgress(module, progress);
  }

  /**
   * Should be called when a dependency module has failed
   * 
   * @param module the module that has failed
   */
  public void notifyModuleFailed(ModuleClass module) {
    getInstance().dependencyFailed(module.getResultClass());
  }

  public void startExecution() {
    if (!isExecutable()) {
      throw new IllegalStateException("This module is not yet executable");
    }

  }

  /**
   * Gets a ModuleResultProvider that should be given to the instance
   * <p>
   * Contains all the results gathered by calls to notifyDependencyFinished
   * 
   * @return the ModelResultProvider
   * @throws IllegalStateException if this module is not yet executable (and thus has not yet all
   *         results gathered)
   */
  public ModuleResultProvider getResultProvider() {
    if (!isExecutable()) {
      throw new IllegalStateException("This module is not yet executable");
    }
    return resultProvider;
  }

  /**
   * Gets the thread that has been set using {@link #setThread(Thread)}
   *
   * @return the thread or null if {@link #setThread} has not been called yet
   */
  public Thread getThread() {
    return thread;
  }

  /**
   * Associates a thread that can later be retrieved by {@link #getThread()}. Ensures that
   * {@link #isExecutable()} is true. Does not to anything with the tread.
   *
   * @param thread the thread to set
   */
  public void setThread(Thread thread) {
    if (!isExecutable()) {
      throw new IllegalStateException("Can not assign threads to modules that are not executable");
    }
    this.thread = thread;
  }

  private void putProgress(ModuleClass module, double progress) {
    if (progress < 0 || progress > 1) {
      throw new IllegalArgumentException("progress must be between 0 and 1, inclusively");
    }

    synchronized (progressMap) {
      if (!progressMap.containsKey(module)) {
        return;
      }

      progressMap.put(module, progress);
      currentProgress = calculateProgress();
    }

    getInstance().observeProgress(currentProgress);
  }
  
  /**
   * Gets the progress of this module and its direct and indirect dependencies
   * 
   * @return a value between 0 and 1
   */
  public double getProgress() {
    return currentProgress;
  }
  
  private double calculateProgress() {
    double sum = 0;
    for (double value : progressMap.values()) {
      sum += value;
    }
    return sum / progressMap.size();
  }

  @Override
  public String toString() {
    return clazz + " @ " + getStatusString();
  }

  private String getStatusString() {
    if (thread != null) {
      if (!thread.isAlive()) {
        return "finished";
      }
      return "running";
    }
    if (isExecutable) {
      return "executable";
    }
    return "waiting for " + StringUtils.join(remainingDependencies, ", ");
  }

  private static class ModuleResultProviderImpl implements ModuleResultProvider {

    private Map<Class<?>, Object> results = new HashMap<>();

    public void put(Class<?> clazz, Object result) {
      if (!clazz.isInstance(result)) {
        throw new IllegalArgumentException("The result object (of type "
            + result.getClass().getName() + ") is not an instance of the result class ("
            + clazz.getName() + ")");
      }
      results.put(clazz, result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getResultFor(Class<T> resultClass) {
      if (!results.containsKey(resultClass)) {
        throw new IllegalArgumentException("Unable to provide result for " + resultClass.getName()
            + " because you did not specify that result class as dependency");
      }
      return (T) results.get(resultClass);
    }
  }
}
