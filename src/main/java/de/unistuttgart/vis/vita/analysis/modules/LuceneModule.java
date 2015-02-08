package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.LuceneResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * LuceneModule class with its result IndexSearcher
 */
@AnalysisModule(dependencies = {ImportResult.class, Model.class, DocumentPersistenceContext.class},
    weight = 5)
public class LuceneModule extends Module<LuceneResult> {

  private ImportResult importResult;
  private List<DocumentPart> documentParts = new ArrayList<DocumentPart>();
  private TextRepository textRepository;
  private DocumentPersistenceContext documentPersistenceContext;
  private String documentId;

  /**
   * Stores the chapters of DocumentParts from ImportResult in the Lucene textRepository of model
   */
  @Override
  public LuceneResult execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {

    textRepository = result.getResultFor(Model.class).getTextRepository();
    importResult = result.getResultFor(ImportResult.class);
    documentPersistenceContext = result.getResultFor(DocumentPersistenceContext.class);
    documentParts = importResult.getParts();
    documentId = documentPersistenceContext.getDocumentId();
    if (!documentParts.isEmpty()) {
      for (DocumentPart documentPart : documentParts) {
        textRepository.storeChaptersTexts(documentPart.getChapters(), documentId);
      }
    }
    return new LuceneResult() {

      @Override
      public IndexReader getIndexReader() throws IOException {
         return textRepository.getIndexSearcherForDocument(documentId).getIndexReader();
      }
    };
  }
}
