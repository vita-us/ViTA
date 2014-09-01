package de.unistuttgart.vis.vita.analysis.modules;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;

public class TextImportModule implements Module<ImportResult> {
  private Path filePath;

  public TextImportModule(Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public <T> Collection<Class<T>> getDependencies() {
    return Arrays.asList(); // no dependencies
  }

  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {
    // do nothing
  }

  @Override
  public ImportResult execute(ModuleResultProvider result, ProgressListener progressListener) {
    TextImportModule importer = new TextImportModule(filePath);
    // TODO implement
    return null;
  }

}
