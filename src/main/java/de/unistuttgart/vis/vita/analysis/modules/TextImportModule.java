package de.unistuttgart.vis.vita.analysis.modules;

import java.nio.file.Path;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;

@AnalysisModule
public class TextImportModule implements Module<ImportResult> {
  private Path filePath;

  public TextImportModule(Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public void observeProgress(double progress) {
    // do nothing
  }

  @Override
  public ImportResult execute(ModuleResultProvider result, ProgressListener progressListener) {
    TextImportModule importer = new TextImportModule(filePath);
    // TODO implement
    return null;
  }
}
