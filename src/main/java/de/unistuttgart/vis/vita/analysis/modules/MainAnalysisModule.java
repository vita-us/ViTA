/*
 * MainAnalysisModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

@AnalysisModule()
public class MainAnalysisModule implements Module<Void> {
  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {

  }

  @Override
  public Void execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }
}
