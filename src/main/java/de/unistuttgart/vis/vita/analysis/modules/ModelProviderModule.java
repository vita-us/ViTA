package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.model.Model;

@AnalysisModule
public class ModelProviderModule implements Module<Model> {
  private Model model;
   
  public ModelProviderModule(Model model) {
    this.model = model;
  }

  @Override
  public void observeProgress(double progress) {
    // Ignore progress reports
  }

  @Override
  public Model execute(ModuleResultProvider result, ProgressListener progressListener) {
    return model;
  }
}
