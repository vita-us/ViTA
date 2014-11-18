/*
 * ImportModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import java.nio.file.Path;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;

/**
 * @author Vincent Link, Eduard Marbach
 */
@AnalysisModule(weight = 0.1)
public class ImportModule extends Module<ImportResult> {

  private Path filePath;

  public ImportModule(Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public ImportResult execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    return new TextImportModule(filePath).execute(result, progressListener);
  }
}
