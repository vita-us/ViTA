package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.SentenceDetectionResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import gate.Annotation;
import gate.AnnotationSet;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.creole.ANNIEConstants;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;

import static de.unistuttgart.vis.vita.model.document.TextPosition.fromGlobalOffset;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class SentenceDetectionModuleTest {

  private static final int L = 115;
  private static final String CONTENT = "This are exactly 115 characters. This are exactly 115 "
                                        + "characters. This are exactly 115 characters. The last "
                                        + "bits...";
  private static SentenceDetectionResult result;
  private static Chapter chapter;

  @BeforeClass
  public static void setUp() throws Exception {
    ImportResult importResult = mock(ImportResult.class);
    List<DocumentPart> parts = createParts();
    when(importResult.getParts()).thenReturn(parts);
    when(importResult.getTotalLength()).thenReturn(L);

    AnnieNLPResult annieNLPResult = mock(AnnieNLPResult.class);
    when(annieNLPResult.getAnnotationsForChapter(any(Chapter.class), anyCollection()))
        .thenReturn(createAnnotations());

    ModuleResultProvider resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    when(resultProvider.getResultFor(AnnieNLPResult.class)).thenReturn(annieNLPResult);

    ProgressListener progressListener = mock(ProgressListener.class, withSettings());

    SentenceDetectionModule module = new SentenceDetectionModule();
    result = module.execute(resultProvider, progressListener);
  }

  private static List<DocumentPart> createParts() {
    chapter = new Chapter();
    chapter.setLength(L);

    DocumentPart part = mock(DocumentPart.class);
    when(part.getChapters()).thenReturn(Arrays.asList(chapter));

    return Arrays.asList(part);
  }

  private static Set<Annotation> createAnnotations() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl();
    DocumentContentImpl documentContent = new DocumentContentImpl(CONTENT);
    document.setContent(documentContent);
    AnnotationSet set = document.getAnnotations();
    set.add(0, 0L, 10L, ANNIEConstants.SENTENCE_ANNOTATION_TYPE, new SimpleFeatureMapImpl());
    set.add(1, 11L, 100L, ANNIEConstants.SENTENCE_ANNOTATION_TYPE, new SimpleFeatureMapImpl());
    set.add(2, 101L, (long) L, ANNIEConstants.SENTENCE_ANNOTATION_TYPE, new SimpleFeatureMapImpl());
    return set;
  }

  @Test
  public void testGetSentencesInChapter() {
    List<Sentence> sentences = result.getSentencesInChapter(chapter);
    assertThat(sentences.size(), is(3));
  }

  @Test
  public void testGetSentenceAtBoundaries() {
    List<Sentence> sen = result.getSentencesInChapter(chapter);
    Sentence check1 = sen.get(0);
    Sentence check2 = sen.get(1);
    Sentence check3 = sen.get(2);
    Sentence test;

    test = result.getSentenceAt(fromGlobalOffset(0, L));
    assertThat(test, is(check1));
    test = result.getSentenceAt(fromGlobalOffset(10, L));
    assertThat(test, is(check1));

    test = result.getSentenceAt(fromGlobalOffset(11, L));
    assertThat(test, is(check2));
    test = result.getSentenceAt(fromGlobalOffset(100, L));
    assertThat(test, is(check2));

    test = result.getSentenceAt(fromGlobalOffset(101, L));
    assertThat(test, is(check3));
    test = result.getSentenceAt(fromGlobalOffset(109, L));
    assertThat(test, is(check3));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetSentenceAtToHigh() {
    result.getSentenceAt(fromGlobalOffset(L + 1, L + 2));
  }


  @Test
  public void testCreateOccurrence() {
    List<Sentence> sentences = result.getSentencesInChapter(chapter);
    Occurrence occurrence = result.createOccurrence(chapter, 0, 55);

    assertThat(occurrence.getSentence(), is(sentences.get(0)));
    assertThat(occurrence.getRange().getStart().getOffset(), is(0));
    assertThat(occurrence.getRange().getEnd().getOffset(), is(55));
  }
}
