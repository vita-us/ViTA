package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * This interface is the architecture for the modules used in the analyze.
 * 
 * Apart from implementing this interface, modules must have the @{@link AnalysisModule} annotation.
 *
 * @param <T> The class for the result returned by {@link #execute(ModuleResultProvider, ProgressListener)}
 */
public interface Module<T> {
  /**
   * Is called when the progress of this module or its dependencies changes.
   *
   * @param progress The progress of the dependencies and this module combined as a value between 0
   *        and 1
   */
  public void observeProgress(double progress);

  /**
   * Starts the execution of the module. A progress listener will be registered to check whether the
   * module is finished or not.
   * 
   * @param result provides the results of the specified dependency modules
   * @param progressListener An object that should be informed about the progress of this module
   * @return The result of the module.
   */
  public T execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception;
}
