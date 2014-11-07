package de.unistuttgart.vis.vita.analysis.mockmodules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;

/**
 * A base class for modules that shows when they begin and finish
 */
public abstract class DebugBaseModule<T> extends Module<T> {
  private boolean hasBeenExecuted = false;
  private boolean hasBeenCalled = false;
  private boolean hasBeenInterrupted = false;
  private boolean shouldFail = false;
  private double currentProgress = 0;

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

  /**
   * Gets the progress of this module and its dependencies that has been reported by the latest call
   * to {@link #observeProgress(double)}.
   * 
   * @return the progress, or 0 if no progress has yet been reported
   */
  public double getCurrentProgress() {
    return currentProgress;
  }

  @Override
  public void observeProgress(double progress) {
    currentProgress = progress;
  }

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
