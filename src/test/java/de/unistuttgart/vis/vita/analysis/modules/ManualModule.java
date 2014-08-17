package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * A module that cannot be automatically instanced
 */
@AnalysisModule(dependencies={Integer.class})
public class ManualModule implements Module<String> {
  public ManualModule(int theValue) {
    
  }
  
  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {
    
  }

  @Override
  public String execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }
}
