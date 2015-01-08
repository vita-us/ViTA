package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.model.document.Document;

@AnalysisModule(weight = 0.1)
public class DocumentPersistenceContextModule extends Module<DocumentPersistenceContext> {

  private String id;
  private Document document;

  public DocumentPersistenceContextModule(Document document) {
    this.document = document;
    this.id = document.getId();
  }

  @Override
  public DocumentPersistenceContext execute(ModuleResultProvider result,
                                            ProgressListener progressListener) throws Exception {
    return new DocumentPersistenceContext() {
      @Override
      public Document getDocument() {
        return document;
      }

      @Override
      public String getDocumentId() {
        return id;
      }

      @Override
      public String getFileName() {
        return document.getFileName();
      }
    };
  }
}
