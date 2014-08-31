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
  private boolean hasBeenInterrupted = false;
  private boolean shouldFail = false;

  /**
   * The exception that is thrown when {@link #makeFail()} is set to true
   */
  public static final Class<? extends Exception> FAIL_EXCEPTION =
      UnsupportedOperationException.class;

  /**
   * Instruct the module to fail with {@link #FAIL_EXCEPTION}
   */
  public void makeFail() {
    shouldFail = true;
  }

  public boolean hasBeenExecuted() {
    return hasBeenExecuted;
  }

  public boolean hasBeenCalled() {
    return hasBeenCalled;
  }

  public boolean hasBeenInterrupted() {
    return hasBeenInterrupted;
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
    T moduleResult;
    try {
      Thread.sleep(DEFAULT_SLEEP_MS);

      if (shouldFail) {
        throw new UnsupportedOperationException("Succeeding is disabled");
      }

      moduleResult = realExecute(result, progressListener);
    } catch (InterruptedException e) {
      hasBeenInterrupted = true;
      moduleResult = interruptReceived(e);
    }
    hasBeenExecuted = true;
    return moduleResult;
  }

  protected abstract T realExecute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception;

  protected T interruptReceived(InterruptedException e) throws Exception {
    throw e;
  }
}
