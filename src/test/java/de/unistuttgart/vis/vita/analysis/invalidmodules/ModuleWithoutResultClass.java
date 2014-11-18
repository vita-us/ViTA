package de.unistuttgart.vis.vita.analysis.invalidmodules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule
public class ModuleWithoutResultClass<V> extends Module<V> {

  @Override
  public V execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }

}
