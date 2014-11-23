/*
 * ImportModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import java.nio.file.Path;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleExecutionException;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;

import org.apache.commons.io.FilenameUtils;

/**
 * Module to provide the correct importing method either for plain text files or for epub files.
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
    String extension = FilenameUtils.getExtension(filePath.getFileName().toString());
    extension = extension.toLowerCase();

    if(extension.equals(FILETYPE.EPUB.toString())) {
      return new EpubImportModule(filePath).execute(result, progressListener);
    } else if (extension.equals(FILETYPE.TXT.toString())) {
      return new TextImportModule(filePath).execute(result, progressListener);
    } else {
      throw new ModuleExecutionException("No supported filetype: " + extension);
    }
  }
}

enum FILETYPE{
  EPUB("epub"),
  TXT("txt");

  private final String text;

  /**
   * @param text
   */
  private FILETYPE(final String text) {
    this.text = text;
  }

  /* (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }
}
