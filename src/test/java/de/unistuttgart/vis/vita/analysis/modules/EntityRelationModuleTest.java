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
import de.unistuttgart.vis.vita.model.document.Chapter;
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
   * in the first half, entity1 and entity2 have a relation
   * in the second half, entity1 and entity3 have a relation
   */
  private BasicEntity entity1;
  private BasicEntity entity2;
  private BasicEntity entity3;

  private static final int DOCUMENT_LENGTH = 1000;

  @Before
  public void setUp() {
    chapter = new Chapter();
    chapter.setRange(new Range(
        TextPosition.fromGlobalOffset(chapter, 0),
        TextPosition.fromGlobalOffset(chapter, DOCUMENT_LENGTH)));

    resultProvider = mock(ModuleResultProvider.class);
    progressListener = mock(ProgressListener.class);

    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getTotalLength()).thenReturn(DOCUMENT_LENGTH);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);

    BasicEntityCollection entities = createBasicEntities();
    when(resultProvider.getResultFor(BasicEntityCollection.class)).thenReturn(entities);

    module = new EntityRelationModule();
  }

  @Test
  public void testRelations() throws Exception {
    EntityRelations relations = module.execute(resultProvider, progressListener);
    assertThat(relations.getRelatedEntities(entity1), hasEntry(entity2, 0.5));
    assertThat(relations.getRelatedEntities(entity1), hasEntry(entity3, 1.0));
  }

  @Test
  public void testRelationsOverTime() throws Exception {
    EntityRelations relations = module.execute(resultProvider, progressListener);
    assertThat(Doubles.asList(relations.getWeightOverTime(entity1, entity2)),
        contains(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0));
    assertThat(Doubles.asList(relations.getWeightOverTime(entity1, entity3)),
        contains(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0));
  }

  private BasicEntityCollection createBasicEntities() {
    final List<BasicEntity> list = new ArrayList<>();

    entity1 = new BasicEntity();
    entity1.getOccurences().add(new Range(chapter, 20, 30));
    entity1.getOccurences().add(new Range(chapter, 680, 690));
    list.add(entity1);

    entity2 = new BasicEntity();
    entity2.getOccurences().add(new Range(chapter, 40, 50));
    list.add(entity2);

    entity3 = new BasicEntity();
    entity3.getOccurences().add(new Range(chapter, 660, 670));
    entity3.getOccurences().add(new Range(chapter, 710, 720));
    list.add(entity3);

    return new BasicEntityCollection() {
      @Override
      public Collection<BasicEntity> getEntities() {
        return list;
      }
    };
  }
}
