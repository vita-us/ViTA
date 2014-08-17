package de.unistuttgart.vis.vita.analysis.invalidmodules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;

public class ModuleWithoutAnnotation implements Module<String> {
  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String execute(ModuleResultProvider result, ProgressListener progressListener) {
    // TODO Auto-generated method stub
    return null;
  }
}
