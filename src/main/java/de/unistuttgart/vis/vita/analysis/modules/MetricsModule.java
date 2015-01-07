package de.unistuttgart.vis.vita.analysis.modules;

import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Terms;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.LuceneResult;
import de.unistuttgart.vis.vita.analysis.results.TextMetrics;
import de.unistuttgart.vis.vita.model.TextRepository;

/**
 * Currently, only counts the words in the document
 */
@AnalysisModule(dependencies = LuceneResult.class)
public class MetricsModule extends Module<TextMetrics> {

  @Override
  public TextMetrics execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    
    LuceneResult luceneResult = results.getResultFor(LuceneResult.class);
    Terms terms = SlowCompositeReaderWrapper.wrap(luceneResult.getIndexReader())
        .terms(TextRepository.CHAPTER_TEXT_FIELD);
    final int count = (int)(terms.getSumTotalTermFreq());

    luceneResult.getIndexReader().close();
    return new TextMetrics() {
      @Override
      public int getWordCount() {
        return count;
      }
    };
  }

}
