package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;

@AnalysisModule
public class DocumentPersistenceContextModule implements Module<DocumentPersistenceContext> {
  private String id;

  public DocumentPersistenceContextModule(String id) {
    this.id = id;
  }

  @Override
  public void observeProgress(double progress) {}

  @Override
  public DocumentPersistenceContext execute(ModuleResultProvider result,
      ProgressListener progressListener) throws Exception {
    return new DocumentPersistenceContext() {
      @Override
      public String getDocumentId() {
        return id;
      }
    };
  }
}
