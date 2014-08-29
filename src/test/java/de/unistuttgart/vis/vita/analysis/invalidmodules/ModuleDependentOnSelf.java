package de.unistuttgart.vis.vita.analysis.invalidmodules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule(dependencies = String.class)
public class ModuleDependentOnSelf implements Module<String> {

  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {

  }

  @Override
  public String execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }

}
