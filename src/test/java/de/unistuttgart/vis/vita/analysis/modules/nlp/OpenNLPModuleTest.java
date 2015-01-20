package de.unistuttgart.vis.vita.analysis.modules.nlp;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.modules.gate.GateInitializeModule;
import de.unistuttgart.vis.vita.analysis.modules.gate.OpenNLPModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.OpenNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gate.Annotation;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class OpenNLPModuleTest {

  private final static String[] CHAPTERS = {
      "The text of the first chapter which consists of two sentences. The second one is short.",
      "Dave went to New York this summer.",
      "Just some more chapters ...",
      "... to properly test the progress feature ...",
      "... because the granularity can not be higher than the number of chapters."
  };
  private static OpenNLPModule module;
  private static OpenNLPResult openNLPResult;
  private static List<DocumentPart> parts = new ArrayList<>();
  private static ModuleResultProvider resultProvider;
  private static ProgressListener progressListener;
  private static List<Chapter> chapterObjects;

  @BeforeClass
  public static void setUp() throws Exception {
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    GateInitializeModule initializeModule = new GateInitializeModule();
    initializeModule.execute(resultProvider, progressListener);
    DocumentPersistenceContext testingID = new DocumentPersistenceContext() {
      @Override
      public String getDocumentId() {
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

    module = new OpenNLPModule();
    openNLPResult = module.execute(resultProvider, progressListener);
  }

  private static void fillText() {
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
    Set<Annotation> annotations = openNLPResult.getAnnotationsForChapter(chapterObjects.get(1));
    assertThat(annotations, is(not(empty())));
    assertThat(annotations, is(hasSize(2)));

    for (Annotation annotation : annotations) {
      assertThat(annotation, is(notNullValue()));
      assertThat(annotation.getType(), isOneOf("Location", "Person"));
    }
  }

  @Test
  public void testProgressIsReported() throws Exception {
    // Check that the 0%-100% range is covered approximately
    // This does not test smoothness as the call times are not considered.
    int steps = 5;
    for (int i = 0; i < steps; i++) {
      verify(progressListener, atLeastOnce()).observeProgress(doubleThat(
          closeTo((double) i / steps, (double) 1 / steps)));
    }
  }
}
