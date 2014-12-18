package de.unistuttgart.vis.vita.analysis.modules;

import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.TextMetrics;
import de.unistuttgart.vis.vita.model.TextRepository;

/**
 * Currently, only counts the words in the document
 */
@AnalysisModule(dependencies = IndexSearcher.class)
public class MetricsModule extends Module<TextMetrics> {

  @Override
  public TextMetrics execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    IndexSearcher searcher = results.getResultFor(IndexSearcher.class);
    Terms terms = SlowCompositeReaderWrapper.wrap(searcher.getIndexReader())
        .terms(TextRepository.CHAPTER_TEXT_FIELD);
    final int count = (int)(terms.getSumTotalTermFreq());


    return new TextMetrics() {
      @Override
      public int getWordCount() {
        return count;
      }
    };
  }

}