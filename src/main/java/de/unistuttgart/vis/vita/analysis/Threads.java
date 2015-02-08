package de.unistuttgart.vis.vita.analysis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides an application-wide thread pool
 */
public class Threads {
  private static final ExecutorService GLOBAL_EXECUTOR = Executors.newCachedThreadPool();

  /**
   * Gets an application-wide thread pool
   * @return
   */
  public static ExecutorService getGlobalExecutorService() {
    return GLOBAL_EXECUTOR;
  }
}
