package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule()
public class IntProvidingModule extends DebugBaseModule<Integer> {
  public static final int RESULT = 42;

  @Override
  public Integer realExecute(ModuleResultProvider result, ProgressListener progressListener)
      throws InterruptedException {
    progressListener.observeProgress(0.5);
    Thread.sleep(DebugBaseModule.DEFAULT_SLEEP_MS);
    return RESULT;
  }
}
