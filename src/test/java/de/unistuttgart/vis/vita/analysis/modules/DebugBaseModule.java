package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;

/**
 * A base class for modules that shows when they begin and finish
 */
public abstract class DebugBaseModule<T> implements Module<T> {
  private boolean hasBeenExecuted = false;
  private boolean hasBeenCalled = false;

  public boolean hasBeenExecuted() {
    return hasBeenExecuted;
  }

  public boolean hasBeenCalled() {
    return hasBeenCalled;
  }

  @Override
  public <U> void observeProgress(java.lang.Class<U> resultClass, double progress, boolean isReady) {

  };

  /**
   * The time in milliseconds the mock modules should sleep to make execution deterministic Greater
   * values slow the tests down, slower values risk failures
   */
  public static final int DEFAULT_SLEEP_MS = 300;

  @Override
  public final T execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    hasBeenCalled = true;
    Thread.sleep(DEFAULT_SLEEP_MS);
    T moduleResult = realExecute(result, progressListener);
    hasBeenExecuted = true;
    return moduleResult;
  }

  protected abstract T realExecute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception;
}
