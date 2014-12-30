package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.GlobalWordCloudResult;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

/**
 * Calculates the document-wide word cloud using lucene
 */
@AnalysisModule(dependencies = {IndexSearcher.class, AnalysisParameters.class})
public class WordCloudModule extends Module<GlobalWordCloudResult> {
  private int maxCount;

  @Override
  public GlobalWordCloudResult execute(ModuleResultProvider results,
      ProgressListener progressListener) throws IOException {
    IndexSearcher searcher = results.getResultFor(IndexSearcher.class);
    boolean stopWordListEnabled =
        results.getResultFor(AnalysisParameters.class).isStopWordListEnabled();
    maxCount = results.getResultFor(AnalysisParameters.class).getWordCloudItemsCount();
    final WordCloud globalWordCloud = getGlobalWordCloud(searcher, stopWordListEnabled);

    return new GlobalWordCloudResult() {
      @Override
      public WordCloud getGlobalWordCloud() {
        return globalWordCloud;
      }
    };
  }

  private WordCloud getGlobalWordCloud(IndexSearcher searcher, boolean stopWordListEnabled)
      throws IOException {
    Terms terms =
        SlowCompositeReaderWrapper.wrap(searcher.getIndexReader()).terms(
            TextRepository.CHAPTER_TEXT_FIELD);
    if (terms == null) {
      // This means that there are no chapters
      return new WordCloud();
    }

    Set<String> stopWordList;
    if (stopWordListEnabled) {
      stopWordList = StopWordList.getStopWords();
    } else {
      stopWordList = new HashSet<String>();
    }

    TermsEnum enumerator = terms.iterator(null);
    BytesRef term = enumerator.next();
    List<WordCloudItem> items = new ArrayList<WordCloudItem>();
    while (term != null) {
      String termText = term.utf8ToString();

      if (!stopWordList.contains(termText)) {
        long frequency =
            searcher.getIndexReader().totalTermFreq(
                new Term(TextRepository.CHAPTER_TEXT_FIELD, term));
        items.add(new WordCloudItem(termText, (int) frequency));
      }
      term = enumerator.next();
    }

    // WordCloudItems sort normally, but we want the higher items at the start, thus reversed
    Collections.sort(items, Collections.reverseOrder());
    if (items.size() > maxCount)
      items = items.subList(0, maxCount);

    searcher.getIndexReader().close();
    return new WordCloud(items);
  }
}
