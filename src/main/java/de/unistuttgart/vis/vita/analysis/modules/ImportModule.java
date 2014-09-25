/*
 * ImportModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;

import java.nio.file.Path;

/**
 * @author Vincent Link, Eduard Marbach
 */
@AnalysisModule()
public class ImportModule implements Module<ImportResult> {

  private Path filePath;

  public ImportModule(Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public void observeProgress(double progress) {
    // Ignore progress reports
  }

  @Override
  public ImportResult execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }
}
