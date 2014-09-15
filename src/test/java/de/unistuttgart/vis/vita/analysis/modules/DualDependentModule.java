package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule(dependencies = {Boolean.class, String.class})
public class DualDependentModule extends DebugBaseModule<Float> {
  @Override
  public Float realExecute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }
}
