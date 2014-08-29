package de.unistuttgart.vis.vita.analysis;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * The state maintained by the AnalyisController for the execution of a module
 */
public class ModuleExecutionState {
  private ModuleClass clazz;
  private Module<?> instance;
  private Set<ModuleClass> remainingDependencies;
  private ModuleResultProviderImpl resultProvider;
  private boolean isExecutable;
  
  public ModuleExecutionState(ModuleClass clazz, Module<?> optionalInstance,
      Set<ModuleClass> dependencies) {
    this.clazz = clazz;
    this.instance = optionalInstance;
    remainingDependencies = dependencies;
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
    private Map<Class<?>, Object> results;

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
