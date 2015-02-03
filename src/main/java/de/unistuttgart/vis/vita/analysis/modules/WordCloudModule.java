package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.GlobalWordCloudResult;
import de.unistuttgart.vis.vita.analysis.results.LuceneResult;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

/**
 * Calculates the document-wide word cloud using lucene. Analysis Parameters can determine to which
 * value the number of different words in the word cloud is limited and if the stop word list should
 * be used.
 */
@AnalysisModule(dependencies = {LuceneResult.class, AnalysisParameters.class})
public class WordCloudModule extends Module<GlobalWordCloudResult> {
  private int maxWordCloudItemsCount;

  @Override
  public GlobalWordCloudResult execute(ModuleResultProvider results,
      ProgressListener progressListener) throws IOException {

    // get parameters
    LuceneResult luceneResult = results.getResultFor(LuceneResult.class);
    boolean stopWordListEnabled =
        results.getResultFor(AnalysisParameters.class).getStopWordListEnabled();
    maxWordCloudItemsCount =
        results.getResultFor(AnalysisParameters.class).getWordCloudItemsCount();

    // calculate word cloud
    final WordCloud globalWordCloud;
    IndexReader reader = luceneResult.getIndexReader();
    try {
      globalWordCloud = getGlobalWordCloud(reader, stopWordListEnabled);
    } finally {
      reader.close();
    }

    // return word cloud
    return new GlobalWordCloudResult() {
      @Override
      public WordCloud getGlobalWordCloud() {
        return globalWordCloud;
      }
    };
  }

  /**
   * 
   * @param reader - The Lucene Index Reader of the current document.
   * @param stopWordListEnabled - true: stop words should be used to filter the Word Cloud. false:
   *        no filtering.
   * @return The Global Word Cloud for the document.
   * @throws IOException - Can be thrown by reader or terms iterator. Can also be thrown if this
   *         method was unable to get the Stop Word list.
   */
  private WordCloud getGlobalWordCloud(IndexReader reader, boolean stopWordListEnabled)
      throws IOException {
    Terms terms = SlowCompositeReaderWrapper.wrap(reader).terms(TextRepository.CHAPTER_TEXT_FIELD);

    if (terms == null) {
      // This means that there are no chapters
      return new WordCloud();
    }
    
    Set<String> stopWordList = prepareStopWordsSet(stopWordListEnabled);
    List<WordCloudItem> items = createWordCloudItemsFromTerms(reader, stopWordList, terms);
    items = sortAndFilter(items);
    
    return new WordCloud(items);
  }

  /**
   * Uses the Lucene IndexReader and Terms to get Word Counts and create WordCloudItems. Ignores the
   * stop words.
   * 
   * @param reader - The IndexReader.
   * @param stopWordList - Words on this list will be ignored.
   * @param terms - The Terms (Words) found by Lucene.
   * @return The WordCloutItems to create a Word Cloud.
   * @throws IOException - Thrown by reader or terms iterator.
   */
  private List<WordCloudItem> createWordCloudItemsFromTerms(IndexReader reader,
      Set<String> stopWordList, Terms terms) throws IOException {
    List<WordCloudItem> items = new ArrayList<WordCloudItem>();

    TermsEnum enumerator = terms.iterator(null);
    BytesRef term = enumerator.next();

    while (term != null) {
      String termText = term.utf8ToString();
      if (!stopWordList.contains(termText)) {
        long frequency = reader.totalTermFreq(new Term(TextRepository.CHAPTER_TEXT_FIELD, term));
        items.add(new WordCloudItem(termText, (int) frequency));
      }
      term = enumerator.next();
    }

    return items;
  }

  /**
   * Get the stop words if enabled, otherwise an empty set.
   * 
   * @param stopWordListEnabled - true: defined stop words should be used to filter the Word Cloud.
   *        false: only basic filtering, no stop words.
   * @return all stop words
   * @throws IOException - Thrown if this methods was unable to get the stop words.
   */
  private Set<String> prepareStopWordsSet(boolean stopWordListEnabled) throws IOException {
    Set<String> stopWordList;
    if (stopWordListEnabled) {
      stopWordList = StopWordList.getStopWords();
    } else {
      stopWordList = new HashSet<String>();
    }
    return stopWordList;
  }
  
  /**
   * Sorts the WordCloudItems in reversed order (highest first). The most rare items may be removed,
   * if there are more items than allowed.
   * 
   * @param items - The WordCloudItems, will be sorted.
   * @return The sorted and filtered items.
   */
  private List<WordCloudItem> sortAndFilter(List<WordCloudItem> items) {
    List<WordCloudItem> filteredItems = items;
    Collections.sort(filteredItems, Collections.reverseOrder());
    if (filteredItems.size() > maxWordCloudItemsCount) {
      filteredItems = filteredItems.subList(0, maxWordCloudItemsCount);
    }
    return filteredItems;
  }

}
