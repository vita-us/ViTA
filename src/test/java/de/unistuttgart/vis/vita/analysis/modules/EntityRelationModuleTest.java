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
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;


public class EntityRelationModuleTest {
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
    chapter.setRange(new Range(TextPosition.fromGlobalOffset(chapter, 500, 0), TextPosition
        .fromGlobalOffset(chapter, 900, DOCUMENT_LENGTH)));

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

    resultProvider = mock(ModuleResultProvider.class);
    progressListener = mock(ProgressListener.class);

    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getTotalLength()).thenReturn(DOCUMENT_LENGTH);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);

    BasicEntityCollection entities = createBasicEntities();
    when(resultProvider.getResultFor(BasicEntityCollection.class)).thenReturn(entities);

    AnalysisParameters parameters = new AnalysisParameters();
    when(resultProvider.getResultFor(AnalysisParameters.class)).thenReturn(parameters);
    module = new EntityRelationModule();
  }

  @Test
  public void testRelations() throws Exception {
    EntityRelations relations = module.execute(resultProvider, progressListener);
//    assertThat(relations.getRelatedEntities(entity1), hasEntry(entity2, 0.5));
//    assertThat(relations.getRelatedEntities(entity1), hasEntry(entity3, 1.0));
    for(BasicEntity entity: relations.getRelatedEntities(entity1).keySet()){
      System.out.println("entity: "+ entity.getDisplayName());
    }
  }

  @Test
  public void testRelationsOverTime() throws Exception {
    EntityRelations relations = module.execute(resultProvider, progressListener);
//    assertThat(
//        Doubles.asList(relations.getWeightOverTime(entity1, entity2)),
//        contains(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
//            0.0, 0.0, 0.0, 0.0));
//    assertThat(
//        Doubles.asList(relations.getWeightOverTime(entity1, entity3)),
//        contains(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
//            0.0, 0.0, 0.0, 0.0));
    
  }

  private BasicEntityCollection createBasicEntities() {
    final List<BasicEntity> entities = new ArrayList<>();

    entity1 = new BasicEntity();
    entity1.setDisplayName("entity1");
    entity1.getOccurences().add(
        new Occurrence(sentences.get(0), new Range(TextPosition.fromGlobalOffset(chapter, 510,
            DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(chapter, 520, DOCUMENT_LENGTH))));
    entity1.getOccurences().add(
        new Occurrence(sentences.get(1), new Range(TextPosition.fromGlobalOffset(chapter, 634,
            DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(chapter, 655, DOCUMENT_LENGTH))));
    entities.add(entity1);

    entity2 = new BasicEntity();
    entity2.setDisplayName("entity2");
    entity2.getOccurences().add(
        new Occurrence(sentences.get(0), new Range(TextPosition.fromGlobalOffset(chapter, 543,
            DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(chapter, 579, DOCUMENT_LENGTH))));
    entity2.getOccurences().add(
        new Occurrence(sentences.get(2), new Range(TextPosition.fromGlobalOffset(chapter, 820,
            DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(chapter, 835, DOCUMENT_LENGTH))));
    entities.add(entity2);

    entity3 = new BasicEntity();
    entity3.setDisplayName("entity3");
    entity3.getOccurences().add(
        new Occurrence(sentences.get(1), new Range(TextPosition.fromGlobalOffset(chapter, 660,
            DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(chapter, 670, DOCUMENT_LENGTH))));
    entity3.getOccurences().add(
        new Occurrence(sentences.get(4), new Range(TextPosition.fromGlobalOffset(chapter, 804,
            DOCUMENT_LENGTH), TextPosition.fromGlobalOffset(chapter, 819, DOCUMENT_LENGTH))));
    entities.add(entity3);
    
    return new BasicEntityCollection() {
      @Override
      public Collection<BasicEntity> getEntities() {
        return entities;
      }
    };
  }
}
