package de.unistuttgart.vis.vita.analysis.invalidmodules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule(dependencies = String.class)
public class ModuleDependentOnSelf implements Module<String> {

  @Override
  public void observeProgress(double progress) {

  }

  @Override
  public String execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }

}
