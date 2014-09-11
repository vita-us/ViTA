package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
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
import de.unistuttgart.vis.vita.model.document.Document;
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
  private List<String> storedChaptersIds = new ArrayList<String>();
  private List<String> storedChapterTexts = new ArrayList<String>();
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

  private void fillText() {
    Document document = new Document();
    DocumentPart documentPart = new DocumentPart();
    documentParts.add(documentPart);

    for (int i = 0; i < 4; i++) {
      Chapter chapter = new Chapter(document);
      chapter.setText(CHAPTERS_TEXTS[i]);
      documentPart.getChapters().add(chapter);
      chapters.add(chapter);
    }
  }
  
  
  private void getStoredChaptersIdsAndTexts(Chapter chapter) throws IOException, ParseException {
    QueryParser queryParser = new QueryParser(CHAPTER_ID, new StandardAnalyzer());
    Query query = queryParser.parse(chapter.getId());
    ScoreDoc[] hits = indexSearcher.search(query, 1).scoreDocs;
    org.apache.lucene.document.Document hitDoc = indexSearcher.doc(hits[0].doc);
    storedChaptersIds.add(hitDoc.getField(CHAPTER_ID).stringValue());
    storedChapterTexts.add(hitDoc.getField(CHAPTER_TEXT).stringValue());
  }
  
  @Test
  public void testStoredChapter() throws Exception{
    indexSearcher = luceneModule.execute(moduleResultProvider, progressListener);
    getStoredChaptersIdsAndTexts(chapters.get(0));
    getStoredChaptersIdsAndTexts(chapters.get(1));
    getStoredChaptersIdsAndTexts(chapters.get(2));
    getStoredChaptersIdsAndTexts(chapters.get(3));
    
    for (int i = 0; i < 4; i++) {
      assertEquals(chapters.get(i).getId(), storedChaptersIds.get(i));
      assertEquals(chapters.get(i).getText(), storedChapterTexts.get(i));
    }
  }
}
