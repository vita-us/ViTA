package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;

import java.nio.file.Path;

@AnalysisModule(weight = 0.1)
public class DocumentPersistenceContextModule extends Module<DocumentPersistenceContext> {

  private String id;
  private Path documentPath;

  public DocumentPersistenceContextModule(String id, Path documentPath) {
    this.id = id;
    this.documentPath = documentPath;
  }

  @Override
  public DocumentPersistenceContext execute(ModuleResultProvider result,
                                            ProgressListener progressListener) throws Exception {
    return new DocumentPersistenceContext() {
      @Override
      public String getDocumentId() {
        return id;
      }

      @Override
      public String getFileName() {
        return documentPath.getFileName().toString();
      }
    };
  }
}
