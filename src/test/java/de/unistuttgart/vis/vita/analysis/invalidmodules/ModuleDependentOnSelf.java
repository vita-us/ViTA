package de.unistuttgart.vis.vita.analysis.invalidmodules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule(dependencies = String.class)
public class ModuleDependentOnSelf extends Module<String> {

  @Override
  public String execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }

}
