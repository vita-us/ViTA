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

  public ModuleExecutionState(ModuleClass clazz, Module<?> optionalInstance,
      Set<ModuleClass> dependencies) {
    this.clazz = clazz;
    this.instance = optionalInstance;
    remainingDependencies = new HashSet<ModuleClass>(dependencies);
    resultProvider = new ModuleResultProviderImpl();
    isExecutable = remainingDependencies.isEmpty();
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
  
  public void notifyDependencyFinished(ModuleClass dependency, Object result) {
    if (!dependency.getResultClass().isAssignableFrom(result.getClass())) {
      throw new IllegalArgumentException(
          "The provided result is not assignable to the claimed module's result type");
    }

    synchronized (remainingDependencies) {
      if (!remainingDependencies.contains(dependency)) {
        return;
      }

      resultProvider.put(dependency.getResultClass(), result);
      remainingDependencies.remove(dependency);
      
      if (remainingDependencies.isEmpty()) {
        isExecutable = true;
      }
    }
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
   * @return the ModelResultProvider
   * @throws IllegalStateException if this module is not yet executable (and thus has not
   * yet all results gathered)
   */
  public ModuleResultProvider getResultProvider() {
    if (!isExecutable()) {
      throw new IllegalStateException("This module is not yet executable");
    }
    return resultProvider;
  }

  private static class ModuleResultProviderImpl implements ModuleResultProvider {
    private Map<Class<?>, Object> results = new HashMap<>();

    public void put(Class<?> clazz, Object result) {
      if (!clazz.isInstance(result)) {
        throw new IllegalArgumentException("The result object (of type " + result.getClass().getName()
            + ") is not an instance of the result class (" + clazz.getName() + ")");
      }
      results.put(clazz, result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getResultFor(Class<T> resultClass) {
      if (!results.containsKey(resultClass))
        throw new IllegalArgumentException("Unable to provide result for " + resultClass.getName()
            + " because you did not specify that result class as dependency");
      return (T) results.get(resultClass);
    }
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
    if (!isExecutable())
      throw new IllegalStateException("Can not assign threads to modules that are not executable");
    this.thread = thread;
  }

  @Override
  public String toString() {
    return clazz + " @ " + getStatusString();
  }

  private String getStatusString() {
    if (thread != null) {
      if (!thread.isAlive())
        return "finished";
      return "running";
    }
    if (isExecutable)
      return "executable";
    return "waiting for " + StringUtils.join(remainingDependencies, ", ");
  }
}
