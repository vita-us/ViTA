package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
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
import de.unistuttgart.vis.vita.analysis.results.WordCloudResult;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

@AnalysisModule(dependencies = {IndexSearcher.class})
public class WordCloudModule extends Module<WordCloudResult> {
  private static final int MAX_COUNT = 100;
  private static Set<String> stopWords;

  @Override
  public WordCloudResult execute(ModuleResultProvider results, ProgressListener progressListener)
      throws IOException {
    IndexSearcher searcher = results.getResultFor(IndexSearcher.class);

    final WordCloud globalWordCloud = getGlobalWordCloud(searcher);

    return new WordCloudResult() {
      @Override
      public WordCloud getWordCloudForEntity(BasicEntity entity) {
        // TODO Auto-generated method stub
        return null;
      }

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

      if (!getStopWords().contains(termText)) {
        long frequency = searcher.getIndexReader()
            .totalTermFreq(new Term(TextRepository.CHAPTER_TEXT_FIELD, term));
        items.add(new WordCloudItem(termText, (int) frequency));
        System.out.println(termText);
      }
      term = enumerator.next();
    }
    Collections.sort(items, Collections.reverseOrder());
    if (items.size() > MAX_COUNT)
      items = items.subList(0, MAX_COUNT);
    return new WordCloud(items);
  }

  private Set<String> getStopWords() throws IOException {
    if (stopWords != null)
      return stopWords;

    stopWords = new HashSet<>(
        IOUtils.readLines(WordCloudModule.class.getResourceAsStream("stopwords.txt")));

    return stopWords;
  }
}
