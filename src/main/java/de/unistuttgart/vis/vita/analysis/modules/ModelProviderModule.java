package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.model.Model;

/**
 * A module that provides access to the data base via the {@link Model} class. Only feature modules
 * should depend on this module to store persist the final results and to store the feature progress.
 */
@AnalysisModule
public class ModelProviderModule extends Module<Model> {
  private Model model;

  public ModelProviderModule(Model model) {
    this.model = model;
  }

  @Override
  public Model execute(ModuleResultProvider result, ProgressListener progressListener) {
    return model;
  }
}
