package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private Chapter chapter;
  private EntityRelationModule module;

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
    chapter.setRange(new Range(TextPosition.fromGlobalOffset(chapter, 0, DOCUMENT_LENGTH),
        TextPosition.fromGlobalOffset(chapter, DOCUMENT_LENGTH, DOCUMENT_LENGTH)));

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
    // TODO: Check -> are results correct after sentence-Refactoring
    assertThat(relations.getRelatedEntities(entity1), hasEntry(entity2, 0.5));
    assertThat(relations.getRelatedEntities(entity1), hasEntry(entity3, 1.0));
  }

  @Test
  public void testRelationsOverTime() throws Exception {
    EntityRelations relations = module.execute(resultProvider, progressListener);
    // TODO: Check -> are results correct after sentence-Refactoring
    assertThat(
        Doubles.asList(relations.getWeightOverTime(entity1, entity2)),
        contains(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0));
    assertThat(
        Doubles.asList(relations.getWeightOverTime(entity1, entity3)),
        contains(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0));
  }

  private BasicEntityCollection createBasicEntities() {
    final List<BasicEntity> list = new ArrayList<>();

    Range sentence1Range = new Range(chapter, 0, 75, DOCUMENT_LENGTH);
    Range sentence2Range = new Range(chapter, 635, 745, DOCUMENT_LENGTH);
    Sentence sentence1 = new Sentence(sentence1Range, chapter, 0);
    Sentence sentence2 = new Sentence(sentence2Range, chapter, 1);

    Range entity1_occurrence1 = new Range(chapter, 20, 30, DOCUMENT_LENGTH);
    Range entity1_occurrence2 = new Range(chapter, 680, 690, DOCUMENT_LENGTH);
    Range entity2_occurrence1 = new Range(chapter, 40, 50, DOCUMENT_LENGTH);
    Range entity3_occurrence1 = new Range(chapter, 660, 670, DOCUMENT_LENGTH);
    Range entity3_occurrence2 = new Range(chapter, 710, 720, DOCUMENT_LENGTH);

    entity1 = new BasicEntity();
    entity1.getOccurences().add(new Occurrence(sentence1, entity1_occurrence1));
    entity1.getOccurences().add(new Occurrence(sentence2, entity1_occurrence2));
    list.add(entity1);

    entity2 = new BasicEntity();
    entity2.getOccurences().add(new Occurrence(sentence1, entity2_occurrence1));
    list.add(entity2);

    entity3 = new BasicEntity();
    entity3.getOccurences().add(new Occurrence(sentence2, entity3_occurrence1));
    entity3.getOccurences().add(new Occurrence(sentence2, entity3_occurrence2));
    list.add(entity3);

    return new BasicEntityCollection() {
      @Override
      public Collection<BasicEntity> getEntities() {
        return list;
      }
    };
  }
}
