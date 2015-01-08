package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gate.Annotation;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class ANNIEModuleTest {

  private final static String[] CHAPTERS = {
      "The text of the first chapter which consists of two sentences. The second one is short.",
      "Dave went to New York this summer.",
      "Just some more chapters ...",
      "... to properly test the progress feature ...",
      "... because the granularity can not be higher than the number of chapters."
  };
  private ANNIEModule module;
  private List<DocumentPart> parts = new ArrayList<>();
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private List<Chapter> chapterObjects;

  @Before
  public void setUp() throws Exception {
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    GateInitializeModule initializeModule = new GateInitializeModule();
    initializeModule.execute(resultProvider, progressListener);
    DocumentPersistenceContext testingID = new DocumentPersistenceContext() {
      @Override
      public Document getDocument() {
        return null;
      }

      @Override
      public String getDocumentId() {
        return "testID123";
      }

      @Override
      public String getDocumentContentId() {
        return "testID123";
      }

      @Override
      public String getFileName() {
        return "testID123";
      }
    };
    AnnieDatastore datastore = mock(AnnieDatastore.class);
    when(datastore.getStoredAnalysis(anyString())).thenReturn(null);

    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    when(resultProvider.getResultFor(AnnieDatastore.class)).thenReturn(datastore);
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(testingID);

    progressListener = mock(ProgressListener.class, withSettings());
    fillText();

    module = new ANNIEModule();
  }

  private void fillText() {
    DocumentPart part = new DocumentPart();
    parts.add(part);

    chapterObjects = new ArrayList<>();
    for (String chapterText : CHAPTERS) {
      Chapter chapter = new Chapter();
      chapter.setText(chapterText);
      chapter.setLength(chapterText.length());
      part.getChapters().add(chapter);
      chapterObjects.add(chapter);
    }
  }


  @Test
  public void testEntityAnnotations() throws Exception {
    AnnieNLPResult result = module.execute(resultProvider, progressListener);

    Set<Annotation> annotations = result.getAnnotationsForChapter(chapterObjects.get(1));
    assertThat(annotations, is(not(empty())));
    assertThat(annotations, is(hasSize(2)));

    boolean nameExists = false;

    for (Annotation i : annotations) {
      if (i.getFeatures().get("firstName") != null) {
        nameExists = true;
      }
    }

    assertThat(nameExists, is(true));
  }

  @Test
  public void testProgressIsReported() throws Exception {
    module.execute(resultProvider, progressListener);

    // Check that the 0%-100% range is covered approximately
    // This does not test smoothness as the call times are not considered.
    int steps = 5;
    for (int i = 0; i < steps; i++) {
      verify(progressListener, atLeastOnce()).observeProgress(doubleThat(
          closeTo((double) i / steps, (double) 1 / steps)));
    }
  }
}
