package de.unistuttgart.vis.vita.analysis;

import java.lang.reflect.InvocationTargetException;

/**
 * Works through the list with modules.
 *
 */
public class ModuleExecutionThread extends Thread {

  private final AnalysisController analysisController;
  private final Class<? extends Module> executableModule;

  public ModuleExecutionThread(AnalysisController analysisController,
                               Class<? extends Module> executableModule) {

    this.analysisController = analysisController;
    this.executableModule = executableModule;
  }

  @Override
  public void run() {
    try {
      Module theModule = executableModule.getConstructor().newInstance();
      // TODO execution of the module.
      Object result = theModule.execute(analysisController.getResultProvider(), new MockProgressListener());
      analysisController.getResultProvider().registerResult(executableModule.getClass(), result);
      analysisController.continueAnalysis(executableModule.getClass());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }
}
