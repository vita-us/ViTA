package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule(dependencies = {Integer.class})
public class MockModule extends DebugBaseModule<String> {
  public static final String RESULT = "Frodo";
  
  @Override
  public String realExecute(ModuleResultProvider result, ProgressListener progressListener)
      throws InterruptedException {
    return RESULT;
  }
}
