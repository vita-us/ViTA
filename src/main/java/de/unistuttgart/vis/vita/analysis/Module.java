package de.unistuttgart.vis.vita.analysis;


/**
 * This interface is the architecture for the modules used in the analyze.
 *
 * @param <TResult> The type of the module.
 */
public interface Module<TResult> {
  /**
   * Observes the progress of the module. TODO not sure what this method is used for.
   * 
   * @param resultClass The result class.
   * @param progress The progress.
   * @param isReady If the module is ready.
   */
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady);

  /**
   * Starts the execution of the module. A progress listener will be registered to check whether the
   * module is finished or not.
   * 
   * @param result The provider of the result.
   * @param progressListener A listener to observe the execution.
   * @return The result of the module.
   */
  public TResult execute(ModuleResultProvider result, ProgressListener progressListener);
}
