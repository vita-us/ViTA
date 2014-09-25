package de.unistuttgart.vis.vita.analysis.mockmodules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * A module that cannot be automatically instanced
 */
@AnalysisModule(dependencies={Integer.class})
public class ManualModule extends DebugBaseModule<Boolean> {
  public ManualModule(int theValue) {
    
  }
  
  @Override
  public Boolean realExecute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }
}
