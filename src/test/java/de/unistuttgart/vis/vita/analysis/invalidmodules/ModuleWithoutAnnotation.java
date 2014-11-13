package de.unistuttgart.vis.vita.analysis.invalidmodules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;

public class ModuleWithoutAnnotation extends Module<String> {
  @Override
  public String execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }
}
