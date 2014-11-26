package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.IndexSearcher;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;


public class WordCloudModuleTest {
  private WordCloudModule module;
  private Model model;
  private TextRepository textRepository;
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;

  private static final String DOCUMENT_ID = "thedocumentid";
  // "such" and "a" are stop words
  private static final String CHAPTER_1_TEXT = "once twice twice three three such a";
  private static final String CHAPTER_2_TEXT = "three other words";

  @Before
  public void setUp() throws IOException {
    model = new UnitTestModel();
    UnitTestModel.startNewSession();
    textRepository = model.getTextRepository();
    textRepository.storeChaptersTexts(getChapters(), DOCUMENT_ID);
    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(Model.class)).thenReturn(model);
    IndexSearcher searcher = textRepository.getIndexSearcherForDocument(DOCUMENT_ID);
    when(resultProvider.getResultFor(IndexSearcher.class)).thenReturn(searcher);
    progressListener = mock(ProgressListener.class);

    module = new WordCloudModule();
  }

  @Test
  public void testWordCloud() throws Exception {
    Set<WordCloudItem> wordCloud = module.execute(resultProvider, progressListener).getItems();
    assertThat(wordCloud, containsInAnyOrder(
        new WordCloudItem("once", 1),
        new WordCloudItem("twice", 2),
        new WordCloudItem("three", 3),
        new WordCloudItem("other", 1),
        new WordCloudItem("words", 1)));

  }

  private List<Chapter> getChapters() {
    Chapter chapter1 = new Chapter();
    chapter1.setText(CHAPTER_1_TEXT);
    Chapter chapter2 = new Chapter();
    chapter2.setText(CHAPTER_2_TEXT);
    return Arrays.asList(chapter1, chapter2);
  }
}
