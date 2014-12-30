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
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;


public class WordCloudModuleTest {
  private WordCloudModule module;
  private Model model;
  private TextRepository textRepository;
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private AnalysisParameters parameters;

  private static final String DOCUMENT_ID = "thedocumentid";
  // "such" and "a" are stop words
  private static final String CHAPTER_1_TEXT = "Frodo Bilbo Bilbo Gandalf Gandalf such a";
  private static final String CHAPTER_2_TEXT = "Gandalf Mordor";
  private static boolean setUpIsDone = false;
  
  @Before
  public void setUp() throws IOException {
    model = new UnitTestModel();
    textRepository = model.getTextRepository();
    
    if(!setUpIsDone){
      UnitTestModel.startNewSession();
      textRepository.storeChaptersTexts(getChapters(), DOCUMENT_ID);
    }
    setUpIsDone = true;
  }

  private void createWordCloudModule(boolean stopWordListEnabled) throws IOException {
   
    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(Model.class)).thenReturn(model);
    IndexSearcher searcher = textRepository.getIndexSearcherForDocument(DOCUMENT_ID);
    when(resultProvider.getResultFor(IndexSearcher.class)).thenReturn(searcher);
    parameters = new AnalysisParameters();
    parameters.setStopWordListEnabled(stopWordListEnabled);
    when(resultProvider.getResultFor(AnalysisParameters.class)).thenReturn(parameters);
    progressListener = mock(ProgressListener.class);
    
    module = new WordCloudModule();

  }

  @Test
  public void testWordCloud() throws Exception {
    createWordCloudModule(true);
    Set<WordCloudItem> wordCloud = module.execute(resultProvider, progressListener)
          .getGlobalWordCloud().getItems();
    // items with same frequency are sorted alphabetically, but backwards
    assertThat(wordCloud, contains(
        new WordCloudItem("gandalf", 3),
        new WordCloudItem("bilbo", 2),
        new WordCloudItem("mordor", 1),
        new WordCloudItem("frodo", 1)));

  }

  @Test
  public void testWordCloudWithStopWords() throws IOException{
    createWordCloudModule(false);
    Set<WordCloudItem> wordCloud = module.execute(resultProvider, progressListener)
        .getGlobalWordCloud().getItems();
   // items with same frequency are sorted alphabetically, but backwards
   assertThat(wordCloud, contains(
       new WordCloudItem("gandalf", 3),
       new WordCloudItem("bilbo", 2),
       new WordCloudItem("such", 1),
       new WordCloudItem("mordor", 1),
       new WordCloudItem("frodo", 1),
       new WordCloudItem("a", 1)));
    
  }
  
  private List<Chapter> getChapters() {
    Chapter chapter1 = new Chapter();
    chapter1.setText(CHAPTER_1_TEXT);
    Chapter chapter2 = new Chapter();
    chapter2.setText(CHAPTER_2_TEXT);
    return Arrays.asList(chapter1, chapter2);
  }
}
