package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import gate.Annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

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
  public void setUp() {
    module = new ANNIEModule();
    resultProvider = mock(ModuleResultProvider.class);
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    progressListener = mock(ProgressListener.class, withSettings());
    fillText();
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
