package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.primitives.Doubles;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;


public class EntityRelationModuleTest {
  private static final int ENTITY1_OCCURRENCE1_START_OFFSET = 510;
  private static final int ENTITY1_OCCURRENCE1_END_OFFSET = 520;
  private static final int ENTITY1_OCCURRENCE2_START_OFFSET = 634;
  private static final int ENTITY1_OCCURRENCE2_END_OFFSET = 655;

  private static final int ENTITY2_OCCURRENCE1_START_OFFSET = 534;
  private static final int ENTITY2_OCCURRENCE1_END_OFFSET = 579;
  private static final int ENTITY2_OCCURRENCE2_START_OFFSET = 820;
  private static final int ENTITY2_OCCURRENCE2_END_OFFSET = 835;

  private static final int ENTITY3_OCCURRENCE1_START_OFFSET = 660;
  private static final int ENTITY3_OCCURRENCE1_END_OFFSET = 670;
  private static final int ENTITY3_OCCURRENCE2_START_OFFSET = 804;
  private static final int ENTITY3_OCCURRENCE2_END_OFFSET = 819;
  
  private static final int RELATION_TIME_STEPS_COUNT = 20;

  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private Chapter chapter;
  private EntityRelationModule module;
  private List<Sentence> sentences;

  /*
   * The relations are as follows:
   * 
   * in the first half, entity1 and entity2 have a relation in the second half, entity1 and entity3
   * have a relation
   */
  private BasicEntity entity1;
  private BasicEntity entity2;
  private BasicEntity entity3;

  private static final int DOCUMENT_LENGTH = 1000;

  @Before
  public void setUp() {
    chapter = new Chapter();

    sentences = new ArrayList<Sentence>();
    int startOffSet = 500;
    int endOffset = 600;
    for (int i = 0; i < 5; i++) {
      sentences.add(new Sentence(new Range(TextPosition.fromGlobalOffset(chapter, startOffSet,
          DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(chapter, endOffset, DOCUMENT_LENGTH)),
          chapter, i));
      startOffSet += 100;
      endOffset += 100;
    }
    chapter.setSentences(sentences);
    chapter.setRange(new Range(TextPosition.fromGlobalOffset(chapter, 500, 0), TextPosition
        .fromGlobalOffset(chapter, 900, DOCUMENT_LENGTH)));


    resultProvider = mock(ModuleResultProvider.class);
    progressListener = mock(ProgressListener.class);

    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getTotalLength()).thenReturn(DOCUMENT_LENGTH);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);

    BasicEntityCollection entities = createBasicEntities();
    when(resultProvider.getResultFor(BasicEntityCollection.class)).thenReturn(entities);

    AnalysisParameters parameters = new AnalysisParameters();
    parameters.setRelationTimeStepCount(RELATION_TIME_STEPS_COUNT);
    when(resultProvider.getResultFor(AnalysisParameters.class)).thenReturn(parameters);
    module = new EntityRelationModule();
  }

  @Test
  public void testRelations() throws Exception {
    EntityRelations relations = module.execute(resultProvider, progressListener);
     assertThat(relations.getRelatedEntities(entity1), hasEntry(entity2, 1.0));
     assertThat(relations.getRelatedEntities(entity1), hasEntry(entity3, 1.0));
  }

  @Test
  public void testRelationsOverTime() throws Exception {
    EntityRelations relations = module.execute(resultProvider, progressListener);
     assertThat(
     Doubles.asList(relations.getWeightOverTime(entity1, entity2)),
     contains(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
     0.0, 0.0, 0.0, 0.0));
     assertThat(
     Doubles.asList(relations.getWeightOverTime(entity1, entity3)),
     contains(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
     0.0, 0.0, 0.0, 0.0));


  }

  private BasicEntityCollection createBasicEntities() {
    final List<BasicEntity> entities = new ArrayList<>();

    entity1 = new BasicEntity();
    entity1.setDisplayName("entity1");
    entity1.getOccurences().add(
        new Occurrence(sentences.get(0), new Range(TextPosition.fromGlobalOffset(chapter,
            ENTITY1_OCCURRENCE1_START_OFFSET, DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(
            chapter, ENTITY1_OCCURRENCE1_END_OFFSET, DOCUMENT_LENGTH))));
    entity1.getOccurences().add(
        new Occurrence(sentences.get(1), new Range(TextPosition.fromGlobalOffset(chapter,
            ENTITY1_OCCURRENCE2_START_OFFSET, DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(
            chapter, ENTITY1_OCCURRENCE2_END_OFFSET, DOCUMENT_LENGTH))));
    entities.add(entity1);

    entity2 = new BasicEntity();
    entity2.setDisplayName("entity2");
    entity2.getOccurences().add(
        new Occurrence(sentences.get(0), new Range(TextPosition.fromGlobalOffset(chapter,
            ENTITY2_OCCURRENCE1_START_OFFSET, DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(
            chapter, ENTITY2_OCCURRENCE1_END_OFFSET, DOCUMENT_LENGTH))));
    entity2.getOccurences().add(
        new Occurrence(sentences.get(2), new Range(TextPosition.fromGlobalOffset(chapter,
            ENTITY2_OCCURRENCE2_START_OFFSET, DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(
            chapter, ENTITY2_OCCURRENCE2_END_OFFSET, DOCUMENT_LENGTH))));
    entities.add(entity2);

    entity3 = new BasicEntity();
    entity3.setDisplayName("entity3");
    entity3.getOccurences().add(
        new Occurrence(sentences.get(1), new Range(TextPosition.fromGlobalOffset(chapter,
            ENTITY3_OCCURRENCE1_START_OFFSET, DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(
            chapter, ENTITY3_OCCURRENCE1_END_OFFSET, DOCUMENT_LENGTH))));
    entity3.getOccurences().add(
        new Occurrence(sentences.get(4), new Range(TextPosition.fromGlobalOffset(chapter,
            ENTITY3_OCCURRENCE2_START_OFFSET, DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(
            chapter, ENTITY3_OCCURRENCE2_END_OFFSET, DOCUMENT_LENGTH))));
    entities.add(entity3);


    return new BasicEntityCollection() {
      @Override
      public Collection<BasicEntity> getEntities() {
        return entities;
      }
    };
  }
}
