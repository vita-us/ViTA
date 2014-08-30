package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule(dependencies = {Integer.class})
public class MockModule extends DebugBaseModule<String> {
  public static final String RESULT = "Smeagol";

  /**
   * The result that is returned when this module is interrupted
   */
  public static final String INTERRUPT_RESULT = "Gollum";
  
  /**
   * The exception that is thrown when {@see #shouldFail} is set to true
   */
  public static final Class<? extends Exception> FAIL_EXCEPTION =
      UnsupportedOperationException.class;

  /**
   * Set to true if the module should throw an exception
   */
  public boolean shouldFail;

  @Override
  public String realExecute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    if (shouldFail) {
      throw new UnsupportedOperationException("Succeeding is disabled");
    }

    return RESULT;
  }

  /**
   * This module is nasty and completes even when interrupted
   */
  @Override
  protected String interruptReceived(InterruptedException e) throws InterruptedException {
    Thread.sleep(DebugBaseModule.DEFAULT_SLEEP_MS);
    return INTERRUPT_RESULT;
  }
}
