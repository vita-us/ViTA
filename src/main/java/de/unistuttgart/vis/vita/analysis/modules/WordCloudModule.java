package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

@AnalysisModule(dependencies = {IndexSearcher.class})
public class WordCloudModule extends Module<GlobalWordCloudResult> {
  private static final int MAX_COUNT = 100;

  @Override
  public GlobalWordCloudResult execute(ModuleResultProvider results, ProgressListener progressListener)
      throws IOException {
    IndexSearcher searcher = results.getResultFor(IndexSearcher.class);

    final WordCloud globalWordCloud = getGlobalWordCloud(searcher);

    return new GlobalWordCloudResult() {
      @Override
      public WordCloud getGlobalWordCloud() {
        return globalWordCloud;
      }
    };
  }

  private WordCloud getGlobalWordCloud(IndexSearcher searcher) throws IOException {
    Terms terms = SlowCompositeReaderWrapper.wrap(searcher.getIndexReader())
        .terms(TextRepository.CHAPTER_TEXT_FIELD);
    TermsEnum enumerator = terms.iterator(null);
    BytesRef term = enumerator.next();
    List<WordCloudItem> items = new ArrayList<WordCloudItem>();
    while (term != null) {
      String termText = term.utf8ToString();

      if (!StopWordList.getStopWords().contains(termText)) {
        long frequency = searcher.getIndexReader()
            .totalTermFreq(new Term(TextRepository.CHAPTER_TEXT_FIELD, term));
        items.add(new WordCloudItem(termText, (int) frequency));
      }
      term = enumerator.next();
    }
    Collections.sort(items, Collections.reverseOrder());
    if (items.size() > MAX_COUNT)
      items = items.subList(0, MAX_COUNT);
    return new WordCloud(items);
  }
}
