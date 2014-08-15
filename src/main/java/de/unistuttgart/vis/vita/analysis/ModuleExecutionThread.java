package de.unistuttgart.vis.vita.analysis;

import java.lang.reflect.InvocationTargetException;

/**
 * Works through the list with modules.
 *
 */
public class ModuleExecutionThread extends Thread {

  private final AnalysisController analysisController;
  private final Class<? extends Module> executeModule;

  public ModuleExecutionThread(AnalysisController analysisController,
                               Class<? extends Module> executeModule) {

    this.analysisController = analysisController;
    this.executeModule = executeModule;
  }

  @Override
  public void run() {
    try {
      Module test = executeModule.getConstructor().newInstance();
      // TODO execution of the module.
      Object result = test.execute(analysisController.getResultProvider(), new MockProgressListener());
      analysisController.getResultProvider().registerResult(executeModule.getClass(), result);
      analysisController.continueAnalysis(executeModule.getClass());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }
}
