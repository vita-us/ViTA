package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.StanfordNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

public class StanfordNLPModuleTest {
  private StanfordNLPModule module;
  private List<DocumentPart> parts = new ArrayList<>();
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private List<Chapter> chapterObjects;
  
  private final static String[] CHAPTERS = {
    "The text of the first chapter which consists of two sentences. The second one is short.",
    "Dave went to New York this summer.",
    "Just some more chapters ...",
    "... to properly test the progress feature ...",
    "... because the granularity can not be higher than the number of chapters."
  };
  
  @Before
  public void setUp() {
    module = new StanfordNLPModule();
    resultProvider = mock(ModuleResultProvider.class);
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    progressListener = mock(ProgressListener.class);
    fillText();
  }
  
  private void fillText() {
    Document document = new Document();
    DocumentPart part = new DocumentPart();
    parts.add(part);

    chapterObjects = new ArrayList<>();
    for (String chapterText : CHAPTERS) {
      Chapter chapter = new Chapter(document);
      chapter.setText(chapterText);
      chapter.setLength(chapterText.length());
      part.getChapters().add(chapter);
      chapterObjects.add(chapter);
    }
  }
  
  @Test
  public void testBasicAnnotations() throws Exception {
    StanfordNLPResult result = module.execute(resultProvider, progressListener);
    
    Annotation annotation1 = result.getAnnotationForChapter(chapterObjects.get(0));
    assertThat(annotation1, is(not(nullValue())));
    assertThat(annotation1.get(SentencesAnnotation.class), hasSize(2));
    assertThat(annotation1.get(TokensAnnotation.class), hasSize(18));
  }
  
  @Test
  public void testEntityAnnotations() throws Exception {
    StanfordNLPResult result = module.execute(resultProvider, progressListener);
    
    Annotation annotation2 = result.getAnnotationForChapter(chapterObjects.get(1));
    assertThat(annotation2, is(not(nullValue())));
    
    List<String> actualEntities = new ArrayList<String>();
    for (CoreLabel token : annotation2.get(TokensAnnotation.class)) {
      if (token.has(NamedEntityTagAnnotation.class)) {
        String name = token.get(TextAnnotation.class);
        String type = token.get(NamedEntityTagAnnotation.class);
        if (!type.equals("O")) {
          actualEntities.add(name + ": " + type);
        }
      }
    }
    
    assertThat(actualEntities, contains("Dave: PERSON", "New: LOCATION", "York: LOCATION",
        "this: DATE", "summer: DATE"));
  }
  
  @Test
  public void testProgressIsReported() throws Exception {
    module.execute(resultProvider, progressListener);
    
    // Check that the 0%-100% range is covered approximately
    // This does not test smoothness as the call times are not considered.
    int steps = 5;
    for (int i = 0; i < steps; i++) {
      verify(progressListener, atLeastOnce()).observeProgress(
          doubleThat(closeTo((double) i / steps, (double) 1 / steps)));
    }
  }
}