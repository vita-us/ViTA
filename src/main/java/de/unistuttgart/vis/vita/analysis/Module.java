package de.unistuttgart.vis.vita.analysis;


/**
 * This interface is the architecture for the modules used in the analyze.
 *
 * @param <TResult> The type of the module.
 */
public interface Module<TResult> {
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
   * @param result The provider of the result.
   * @param progressListener A listener to observe the execution.
   * @return The result of the module.
   */
  public TResult execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception;
}
