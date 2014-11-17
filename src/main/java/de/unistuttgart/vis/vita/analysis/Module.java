package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * Base class for modules that do analysis of documents. They can be dependent on other modules, get
 * their results and derive a new result from them.
 * 
 * Apart from implementing this interface, modules must have the @{@link AnalysisModule} annotation.
 *
 * @param <T> The class for the result returned by
 *        {@link #execute(ModuleResultProvider, ProgressListener)}
 */
public abstract class Module<T> {
  /**
   * Is called when the progress of this module or its dependencies changes.
   *
   * @param progress The progress of the dependencies and this module combined as a value between 0
   *        and 1
   */
  public void observeProgress(double progress) {

  }
  
  /**
   * Is called when module that should provide a required result has failed
   * @param resultClass the result class of the failed module
   */
  public void dependencyFailed(Class<?> resultClass) {
    
  }
  
  /**
   * Is called when a result this module depends on has been calculated
   * @param resultClass the result class of the failed module
   * @param result the actual result
   */
  public void dependencyFinished(Class<?> resultClass, Object result) {
    
  }

  /**
   * Starts the execution of the module. A progress listener will be registered to check whether the
   * module is finished or not.
   * 
   * @param result provides the results of the specified dependency modules
   * @param progressListener An object that should be informed about the progress of this module
   * @return The result of the module.
   */
  public abstract T execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception;
}
