package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.MatchesPattern;

import de.unistuttgart.vis.vita.analysis.results.SentenceDetectionResult;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.modules.gate.ANNIEModule;
import de.unistuttgart.vis.vita.analysis.modules.gate.GateInitializeModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.NLPResult;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.EnumNLP;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.Entity;
import static org.mockito.Matchers.anyString;

/**
 * Unit tests for analysis modules
 */
public class EntityRecognitionModuleTest {

  // Short story: The Cunning Fox and the Clever Stork
  private final static String[] CHAPTERS = {
      "Alice went to the party.",
      "Alice met Bob. Frodo is rain. rain is Frodo."};

  // Position of the Person/Place Names (relative)
  private final static int[][] RELATIVE_ENTITY_OFFSETS = {
      // Chapter 1: "Alice"
      {0, 4},
      // Chapter 2: "Alice"
      {0, 4,
          // Chapter 2: "Bob"
          10, 12}};

  // Number of the Sentences to each Person/Place. Should be ordered and ascending: 0,1,2,...
  private final static int[][] ENTITY_SENTENCE_INDEX = {
      // Chapter 1: "Alice" -> Sentence 0
      {0},
      // Chapter 2: "Alice" -> Sentence 1
      {1,
          // Chapter 2: "Bob" -> Sentence 1
          1}};

  // The (relative) start and end position of sentences in chapters
  private final static int[][] SENTENCE_OFFSETS = {
      // Sentence 0
      {0, 22},
      // Sentence 1
      {0, 13}};

  private List<Sentence> sentences = new ArrayList<>();
  private List<DocumentPart> parts = new ArrayList<>();
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private List<Chapter> chapterObjects;
  private BasicEntityCollection collection;
  private AnalysisParameters analysisParameters;
  private EntityRecognitionModule entityRecognitionModule;

  @Before
  public void setUp() throws Exception {
    resultProvider = mock(ModuleResultProvider.class);

    // mock import result
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    when(importResult.getTotalLength()).thenReturn(CHAPTERS[0].length() + CHAPTERS[1].length());
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);

    progressListener = mock(ProgressListener.class, withSettings());

    // create chapters and occurrences -> mock Sentence Detection
    List<Chapter> chapters = fillText();
    SentenceDetectionResult sentenceResult = mock(SentenceDetectionResult.class);
    mockOccurrenceResults(sentenceResult, chapters);
    when(resultProvider.getResultFor(SentenceDetectionResult.class)).thenReturn(sentenceResult);

    GateInitializeModule initializeModule = new GateInitializeModule();
    initializeModule.execute(resultProvider, progressListener);

    DocumentPersistenceContext docId = mock(DocumentPersistenceContext.class);
    when(docId.getDocumentId()).thenReturn("");
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(docId);

    AnnieDatastore datastore = mock(AnnieDatastore.class);
    when(datastore.getStoredAnalysis(anyString())).thenReturn(null);
    when(resultProvider.getResultFor(AnnieDatastore.class)).thenReturn(datastore);

    AnalysisParameters parameters = mock(AnalysisParameters.class);
    when(parameters.getNlpTool()).thenReturn(EnumNLP.ANNIE);
    when(resultProvider.getResultFor(AnalysisParameters.class)).thenReturn(parameters);

    ANNIEModule annieModule = new ANNIEModule();
    AnnieNLPResult annieNLPResult = annieModule.execute(resultProvider, progressListener);
    when(resultProvider.getResultFor(NLPResult.class)).thenReturn(annieNLPResult);
    when(resultProvider.getResultFor(AnnieDatastore.class)).thenReturn(datastore);
    
    analysisParameters = mock(AnalysisParameters.class);
    when(resultProvider.getResultFor(AnalysisParameters.class)).thenReturn(analysisParameters);
    
    entityRecognitionModule = new EntityRecognitionModule();

  }

  /**
   * Mock the results of the SentenceDetection to return occurrences.
   * 
   * @param sentenceResult - the mocked result of the sentence detection.
   * @param chapters - the mocked chapters.
   */
  private void mockOccurrenceResults(SentenceDetectionResult sentenceResult,
      List<Chapter> chapters) {
    if (!(chapters.size() == RELATIVE_ENTITY_OFFSETS.length)) {
      throw new IllegalArgumentException("chapters size does not fit to relative entity offsets");
    }
    for (int chapterIndex = 0; chapterIndex < chapters.size(); chapterIndex++) {
      for (int entityIndex = 0; entityIndex < RELATIVE_ENTITY_OFFSETS[chapterIndex].length / 2; entityIndex++) {
        int sentenceIndex = ENTITY_SENTENCE_INDEX[chapterIndex][entityIndex];
        int documentLength = getDocumentLength();

        // create sentence if there are not enough
        if (sentenceIndex <= sentences.size()) {
          Range sentenceRange =
              new Range(TextPosition.fromGlobalOffset(SENTENCE_OFFSETS[chapterIndex][0],
                  documentLength), TextPosition.fromGlobalOffset(SENTENCE_OFFSETS[chapterIndex][1],
                  documentLength));
          sentences.add(new Sentence(sentenceRange, chapters.get(chapterIndex), sentenceIndex));
        }

        // create mock occurrences for input
        Range occurrenceRange =
            new Range(TextPosition.fromLocalOffset(chapters.get(chapterIndex),
                RELATIVE_ENTITY_OFFSETS[chapterIndex][2 * entityIndex], documentLength),
                TextPosition.fromLocalOffset(chapters.get(chapterIndex),
                    RELATIVE_ENTITY_OFFSETS[chapterIndex][2 * entityIndex + 1], documentLength));
        when(
            sentenceResult.createOccurrence(chapters.get(chapterIndex),
                RELATIVE_ENTITY_OFFSETS[chapterIndex][2 * entityIndex],
                RELATIVE_ENTITY_OFFSETS[chapterIndex][2 * entityIndex + 1])).thenReturn(
            new Occurrence(sentences.get(sentenceIndex), occurrenceRange));
      }
    }
  }

  private List<Chapter> fillText() {
    List<Chapter> chapters = new ArrayList<Chapter>();

    DocumentPart part = new DocumentPart();
    parts.add(part);

    int documentLength = getDocumentLength();

    chapterObjects = new ArrayList<>();
    int pos = 0;
    for (String chapterText : CHAPTERS) {
      Chapter chapter = new Chapter();
      chapter.setText(chapterText);
      chapter.setLength(chapterText.length());
      chapter.setRange(new Range(TextPosition.fromGlobalOffset(pos, documentLength), TextPosition
          .fromGlobalOffset(pos + chapterText.length(), documentLength)));
      pos += chapterText.length();
      part.getChapters().add(chapter);
      chapterObjects.add(chapter);
      chapters.add(chapter);
    }
    return chapters;
  }

  @Test
  public void checkEntitiesAreDetectedAcrossChapters() throws Exception {
    collection = entityRecognitionModule.execute(resultProvider, progressListener);
    BasicEntity person1 = getEntityByName("Alice");
    assertThat(person1, not(nullValue()));
    assertThat(person1.getOccurences(), hasSize(2));
  }

  @Test
  public void checkEntitiesWithOneOccurrenceAreRemoved() throws Exception {
    collection = entityRecognitionModule.execute(resultProvider, progressListener);
    BasicEntity person2 = getEntityByName("Bob");
    assertThat(person2, nullValue());
  }

  private BasicEntity getEntityByName(String name) {
    for (BasicEntity entity : collection.getEntities()) {
      for (Attribute attribute : entity.getNameAttributes()) {
        if (attribute.getContent().equals(name)) {
          return entity;
        }
      }
    }
    return null;
  }
  
  @Test
  public void testWithStopEntityFilter() throws Exception {
    when(analysisParameters.getStopEntityFilter()).thenReturn(true);
    collection = entityRecognitionModule.execute(resultProvider, progressListener);

    BasicEntity person1 = getEntityByName("rain");
    assertThat(person1, nullValue());
    
  }
  
  @Test
  public void testWithoutStopEntityFilter() throws Exception {
    System.out.println(analysisParameters);
    when(analysisParameters.getStopEntityFilter()).thenReturn(false);
    collection = entityRecognitionModule.execute(resultProvider, progressListener);

    BasicEntity person1 = getEntityByName("rain");
    assertThat(person1, not(nullValue()));
  }
  

  private static int getDocumentLength() {
    int documentLength = 0;
    for (String chapter : CHAPTERS) {
      documentLength += chapter.length();
    }
    return documentLength;
  }
}
