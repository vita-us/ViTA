package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit test on LuceneModuleTest
 * 
 *
 */
public class LuceneModuleTest {

  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";
  private static final String INDEX_PATH = "~/.vita/lucene/";

  private LuceneModule luceneModule;
  private ModuleResultProvider moduleResultProvider;
  private ProgressListener progressListener;
  private List<DocumentPart> documentParts = new ArrayList<DocumentPart>();
  private List<Chapter> chapters = new ArrayList<Chapter>();
  private TextRepository textRepository = new TextRepository();
  private IndexSearcher indexSearcher;
  private final static String[] CHAPTERS_TEXTS = {"This is the text of chapter one.",
      "This is the text of chapter two.", "This is the text of chapter three.",
      "This is the text of chapter four.",};

  @Before
  public void setUp() {
    luceneModule = new LuceneModule();
    moduleResultProvider = mock(ModuleResultProvider.class);
    ImportResult importResult = mock(ImportResult.class);
    Model model = mock(Model.class);
    when(importResult.getParts()).thenReturn(documentParts);
    when(model.getTextRepository()).thenReturn(textRepository);
    when(moduleResultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    when(moduleResultProvider.getResultFor(Model.class)).thenReturn(model);
    progressListener = mock(ProgressListener.class, withSettings().verboseLogging());
    fillText();
  }
  
  /**
   * Fills the chapter texts with CHAPTERS_TEXTS
   * 
   * @throws IOException
   */
  private void fillText() {
    de.unistuttgart.vis.vita.model.document.Document document = new de.unistuttgart.vis.vita.model.document.Document();
    DocumentPart documentPart = new DocumentPart();
    documentParts.add(documentPart);

    for (int i = 0; i < 4; i++) {
      Chapter chapter = new Chapter(document);
      chapter.setText(CHAPTERS_TEXTS[i]);
      documentPart.getChapters().add(chapter);
      chapters.add(chapter);
    }
  }
  
  /**
   * Returns the appropriate lucene document to this chapter
   * 
   * @param chapter
   * @throws IOException
   * @throws ParseException
   */
  private Document getStoredDocument(Chapter chapter) throws IOException, ParseException {
    QueryParser queryParser = new QueryParser(CHAPTER_ID, new StandardAnalyzer());
    Query query = queryParser.parse(chapter.getId());
    ScoreDoc[] hits = indexSearcher.search(query, 1).scoreDocs;
    Document hitDoc = indexSearcher.doc(hits[0].doc);
    return hitDoc;
    
  }
  
  /**
   * Tests the equality of the commited chapter ids and texts with the stored ones
   * 
   * @throws ParseException
   * @throws IOException
   */
  @Test
  public void testStoredChapter() throws Exception{
    indexSearcher = luceneModule.execute(moduleResultProvider, progressListener);
    
    for(int i = 0; i<4; i++){
    assertEquals(chapters.get(i).getId(), getStoredDocument(chapters.get(i)).getField(CHAPTER_ID).stringValue());
    assertEquals(chapters.get(i).getText(), getStoredDocument(chapters.get(i)).getField(CHAPTER_TEXT).stringValue());
    }  
  }
}
