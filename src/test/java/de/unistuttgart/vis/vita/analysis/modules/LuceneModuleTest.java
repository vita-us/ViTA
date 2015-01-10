package de.unistuttgart.vis.vita.analysis.modules;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.IndexSearcher;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.LuceneResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * JUnit test on LuceneModuleTest
 */
public class LuceneModuleTest {

  private final static String[] CHAPTERS_TEXTS = {"This is the text of chapter one.",
                                                  "This is the text of chapter two.",
                                                  "This is the text of chapter three.",
                                                  "This is the text of chapter four.",};
  private LuceneModule luceneModule;
  private ModuleResultProvider moduleResultProvider;
  private ProgressListener progressListener;
  private List<DocumentPart> documentParts = new ArrayList<DocumentPart>();
  private String documentId = "document3";
  private List<Chapter> chapters = new ArrayList<Chapter>();
  private TextRepository providedTextRepository = mock(TextRepository.class);
  private IndexSearcher providedIndexSearcher = mock(IndexSearcher.class);

  @Before
  public void setUp() throws IOException {
    luceneModule = new LuceneModule();
    moduleResultProvider = mock(ModuleResultProvider.class);
    ImportResult importResult = mock(ImportResult.class);
    Model model = mock(Model.class);
    DocumentPersistenceContext documentPersistenceContext = mock(DocumentPersistenceContext.class);
    when(importResult.getParts()).thenReturn(documentParts);
    when(model.getTextRepository()).thenReturn(providedTextRepository);
    when(providedTextRepository.getIndexSearcherForDocument(documentId)).thenReturn(providedIndexSearcher);
    when(documentPersistenceContext.getDocumentId()).thenReturn(documentId);
    when(moduleResultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    when(moduleResultProvider.getResultFor(Model.class)).thenReturn(model);
    when(moduleResultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(
        documentPersistenceContext);
    progressListener = mock(ProgressListener.class, withSettings().verboseLogging());
    fillText();
  }

  /**
   * Fills the chapter texts with CHAPTERS_TEXTS
   */
  private void fillText() {

    DocumentPart documentPart = new DocumentPart();
    documentParts.add(documentPart);

    for (int i = 0; i < 4; i++) {
      Chapter chapter = new Chapter();
      chapter.setText(CHAPTERS_TEXTS[i]);
      documentPart.getChapters().add(chapter);
      chapters.add(chapter);
    }
  }

  /**
   * Tests that the module has stored all the supplied chapters
   */
  @Test
  public void testStoresChapters() throws Exception {
    luceneModule.execute(moduleResultProvider, progressListener);
    verify(providedTextRepository).storeChaptersTexts(chapters, documentId);
  }
  
  @Test
  public void testReturnsIndexSearcher() throws Exception {
    LuceneResult luceneResult = luceneModule.execute(moduleResultProvider, progressListener);
    assertEquals(providedIndexSearcher.getIndexReader(), luceneResult.getIndexReader());
  }
}
