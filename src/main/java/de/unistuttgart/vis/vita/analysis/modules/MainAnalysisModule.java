package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * The target module that depends on all enabled feature modules so that they will be executed in
 * the analysis.
 */
@AnalysisModule(dependencies = {TextFeatureModule.class, EntityFeatureModule.class}, weight = 0.1)
public class MainAnalysisModule extends Module<MainAnalysisModule> {
  @Override
  public MainAnalysisModule execute(ModuleResultProvider result, ProgressListener progressListener) {
    // do nothing
    return this;
  }
}
