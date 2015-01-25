package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

public class EntityWordCloudModuleTest {

  private static final String FIRST_ENTITY_NAME = "Mr. Dursley";
  private static final String SECOND_ENTITY_NAME = "Potter";
  private static final int WORD_CLOUD_ITEMS_COUNT = 5;

  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private AnalysisParameters parameters;
  private EntityWordCloudModule module;
  private List<BasicEntity> entities;
  private int documentLength;
  private Chapter chapter;
  private final static String[] SENTENCES = {
      "Mr. Dursley was the director of a firm called drills Grunnings called, which made drills.",
      "Potter was sure there were lots of people called he who had a son called Harry."};

  public void createWordCloudModule(boolean stopWordListEnabled) {
    documentLength = SENTENCES[0].length() + SENTENCES[1].length();

    resultProvider = mock(ModuleResultProvider.class);
    BasicEntityCollection collection = mock(BasicEntityCollection.class);
    createChapter();
    entities = new ArrayList<BasicEntity>();
    createBasicEntities(entities, FIRST_ENTITY_NAME);
    createBasicEntities(entities, SECOND_ENTITY_NAME);

    when(resultProvider.getResultFor(BasicEntityCollection.class)).thenReturn(collection);
    when(collection.getEntities()).thenReturn(entities);
    parameters = new AnalysisParameters();
    parameters.setStopWordListEnabled(stopWordListEnabled);
    parameters.setWordCloudItemsCount(WORD_CLOUD_ITEMS_COUNT);
    when(resultProvider.getResultFor(AnalysisParameters.class)).thenReturn(parameters);
    progressListener = mock(ProgressListener.class);
    module = new EntityWordCloudModule();
  }

  private void createChapter() {
    chapter = new Chapter();
    chapter.setText(SENTENCES[0] + SENTENCES[1]);
    chapter.setRange(new Range(TextPosition.fromGlobalOffset(0, documentLength), TextPosition
        .fromGlobalOffset(documentLength, documentLength)));
  }

  private void createBasicEntities(List<BasicEntity> basicEntities, String name) {
    BasicEntity entity = new BasicEntity();
    Set<Attribute> set = new HashSet<Attribute>();
    Attribute attribute = new Attribute();
    attribute.setContent(name);
    set.add(attribute);
    entity.setNameAttributes(set);
    List<Occurrence> occurrences = new ArrayList<Occurrence>();
    if (name.equals(FIRST_ENTITY_NAME)) {
      createOccurrencesForEntityOne(occurrences);
    } else {
      createOccurrencesForEntityTwo(occurrences);
    }
    entity.setOccurences(occurrences);
    basicEntities.add(entity);
  }

  private void createOccurrencesForEntityOne(List<Occurrence> occurrences) {

    Sentence sentenceOne =
        new Sentence(new Range(TextPosition.fromLocalOffset(chapter, 0, documentLength),
            TextPosition.fromLocalOffset(chapter, SENTENCES[0].length(), documentLength)), chapter, 0);
    sentenceOne.setChapter(chapter);

    occurrences.add(new Occurrence(sentenceOne, new Range(TextPosition.fromGlobalOffset( 0,
        documentLength), TextPosition.fromGlobalOffset( 10, documentLength))));

  }

  private void createOccurrencesForEntityTwo(List<Occurrence> occurrences) {
    Sentence sentenceTwo =
        new Sentence(new Range(TextPosition.fromLocalOffset(chapter, SENTENCES[0].length(), documentLength),
            TextPosition.fromLocalOffset(chapter, documentLength,documentLength)), chapter, 1);
    sentenceTwo.setChapter(chapter);

    occurrences.add(new Occurrence(sentenceTwo, new Range(TextPosition.fromGlobalOffset(
        SENTENCES[0].length(), documentLength), TextPosition.fromGlobalOffset(
        SENTENCES[0].length() + 5, documentLength))));
  }

  @Test
  public void testEntityWordCloud() throws IOException {
    createWordCloudModule(true);
    Set<WordCloudItem> entityOneWordCloud =
        module.execute(resultProvider, progressListener).getWordCloudForEntity(entities.get(0))
            .getItems();

    assertThat(
        entityOneWordCloud,
        contains(new WordCloudItem("drills", 2), new WordCloudItem("called", 2), new WordCloudItem(
            "grunnings", 1), new WordCloudItem("firm", 1), new WordCloudItem("director", 1)));

    Set<WordCloudItem> entityTwoWordCloud =
        module.execute(resultProvider, progressListener).getWordCloudForEntity(entities.get(1))
            .getItems();

    assertThat(
        entityTwoWordCloud,
        contains(new WordCloudItem("called", 2), new WordCloudItem("son", 1), new WordCloudItem(
            "people", 1), new WordCloudItem("lots", 1), new WordCloudItem("harry", 1)));
  }

  @Test
  public void testEntityWordCloudWithStopWords() throws IOException {
    createWordCloudModule(false);
    Set<WordCloudItem> entityOneWordCloud =
        module.execute(resultProvider, progressListener).getWordCloudForEntity(entities.get(0))
            .getItems();

    assertThat(
        entityOneWordCloud,
        contains(new WordCloudItem("drills", 2), new WordCloudItem("called", 2), new WordCloudItem(
            "which", 1), new WordCloudItem("was", 1), new WordCloudItem("the", 1)));

    Set<WordCloudItem> entityTwoWordCloud =
        module.execute(resultProvider, progressListener).getWordCloudForEntity(entities.get(1))
            .getItems();

    assertThat(
        entityTwoWordCloud,
        contains(new WordCloudItem("called", 2), new WordCloudItem("who", 1), new WordCloudItem(
            "were", 1), new WordCloudItem("was", 1), new WordCloudItem("there", 1)));
  }
}
