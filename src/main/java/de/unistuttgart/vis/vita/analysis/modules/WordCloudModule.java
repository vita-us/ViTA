package de.unistuttgart.vis.vita.analysis.modules;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
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
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

@AnalysisModule(dependencies = { IndexSearcher.class })
public class WordCloudModule extends Module<WordCloud> {
  private static final CharArraySet STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

  @Override
  public WordCloud execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    IndexSearcher searcher = results.getResultFor(IndexSearcher.class);
    Terms terms = SlowCompositeReaderWrapper.wrap(searcher.getIndexReader())
          .terms(TextRepository.CHAPTER_TEXT_FIELD);
    TermsEnum enumerator = terms.iterator(null);
    BytesRef term = enumerator.next();
    Set<WordCloudItem> items = new HashSet<WordCloudItem>();
    while (term != null) {
      String termText = term.utf8ToString();

      if (!STOP_WORDS.contains(termText)) {
        long frequency = searcher.getIndexReader()
            .totalTermFreq(new Term(TextRepository.CHAPTER_TEXT_FIELD, term));
        items.add(new WordCloudItem(termText, (int)frequency));
      }
      term = enumerator.next();
    }
    return new WordCloud(items);
  }

}
