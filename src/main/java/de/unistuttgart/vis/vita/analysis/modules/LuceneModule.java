package de.unistuttgart.vis.vita.analysis.modules;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.IndexSearcher;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

@AnalysisModule(dependencies = {ImportResult.class, Model.class})
public class LuceneModule implements Module<IndexSearcher> {

  private ImportResult importResult;
  private List<DocumentPart> documentParts = new ArrayList<DocumentPart>();
  private TextRepository textRepository;


  @Override
  public void observeProgress(double progress) {
    // TODO Auto-generated method stub

  }

  @Override
  public IndexSearcher execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {

    IndexSearcher indexSearcher = null;
    textRepository = result.getResultFor(Model.class).getTextRepository();
    importResult = result.getResultFor(ImportResult.class);
    documentParts = importResult.getParts();
    if (!documentParts.isEmpty()) {
      for (DocumentPart documentPart : documentParts) {
        textRepository.storeChaptersTexts(documentPart.getChapters());
      }
      Document document = documentParts.get(0).getChapters().get(0).getDocument();
      indexSearcher = textRepository.getIndexSearcherForDocument(document);
    }
    return indexSearcher;
  }

}
