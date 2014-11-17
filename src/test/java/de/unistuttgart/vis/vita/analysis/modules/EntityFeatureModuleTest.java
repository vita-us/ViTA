package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.EntityRanking;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;

public class EntityFeatureModuleTest {
  private static final String NAME1_1 = "Frodo";
  private static final String NAME1_2 = "Mr. Frodo";
  private static final String NAME2 = "Hobbiton";
  private static final int OCCURANCE1_START = 10;
  private static final int OCCURANCE1_END = 20;
  private static final int OCCURANCE2_START = 30;
  private static final int OCCURANCE2_END = 40;
  private static final int OCCURANCE3_START = 15;
  private static final int OCCURANCE3_END = 25;
  
  private Document document;
  private Chapter chapter;
  private EntityFeatureModule module;
  private EntityManager em;
  private ModuleResultProvider resultProvider;
  private ProgressListener listener;
  
  @Before
  public void setUp() throws Exception {
    UnitTestModel.startNewSession();
    Model model = new UnitTestModel();
    em = model.getEntityManager();
    prepareDatabase();
    EntityRanking entities = createBasicEntities();
    EntityRelations relations = createEntityRelations();
    DocumentPersistenceContext context = mock(DocumentPersistenceContext.class);
    when(context.getDocumentId()).thenReturn(document.getId());
    
    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(Model.class)).thenReturn(model);
    when(resultProvider.getResultFor(EntityRanking.class)).thenReturn(entities);
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(context);
    when(resultProvider.getResultFor(EntityRelations.class)).thenReturn(relations);
    
    listener = mock(ProgressListener.class);
    
    module = new EntityFeatureModule();
  }
  
  @Test
  public void testEntitiesArePersisted() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    assertThat(document.getContent().getPersons(), hasSize(1));
    Person person = document.getContent().getPersons().get(0);
    assertThat(person.getDisplayName(), is(NAME1_1));
    assertThat(person.getType(), is(EntityType.PERSON));

    assertThat(document.getContent().getPlaces(), hasSize(1));
    Place place = document.getContent().getPlaces().get(0);
    assertThat(place.getDisplayName(), is(NAME2));
    assertThat(place.getType(), is(EntityType.PLACE));
  }
  
  @Test
  public void testAttributesArePersisted() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    Place place = document.getContent().getPlaces().get(0);
    assertThat(place.getAttributes(), hasSize(1));
    Attribute attribute = place.getAttributes().iterator().next();
    assertThat(attribute.getContent(), is(NAME2));
    assertThat(attribute.getType(), is(AttributeType.NAME));
  }
  
  @Test
  public void testOccurrencesArePersisted() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    Place place = document.getContent().getPlaces().get(0);
    assertThat(place.getOccurrences(), contains(
        new TextSpan(chapter, OCCURANCE3_START, OCCURANCE3_END)));
  }

  @Test
  public void testProgressIsSetToReadyAfterwards() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    assertThat(document.getProgress().getPersonsProgress().getProgress(), is(1.0));
    assertThat(document.getProgress().getPlacesProgress().getProgress(), is(1.0));
    assertThat(document.getProgress().getPersonsProgress().isReady(), is(true));
    assertThat(document.getProgress().getPlacesProgress().isReady(), is(true));
  }

  @Test
  public void testProgressIsReportedDuringExecution() {
    module.observeProgress(0.2); // should be ignored as model is not provided yet

    module.dependencyFinished(Model.class, resultProvider.getResultFor(Model.class));
    module.dependencyFinished(DocumentPersistenceContext.class,
        resultProvider.getResultFor(DocumentPersistenceContext.class));
    module.observeProgress(0.5);

    em.refresh(document);
    assertThat(document.getProgress().getPersonsProgress().getProgress(), is(0.5));
    assertThat(document.getProgress().getPlacesProgress().getProgress(), is(0.5));
    assertThat(document.getProgress().getPersonsProgress().isReady(), is(false));
    assertThat(document.getProgress().getPlacesProgress().isReady(), is(false));
  }

  private void prepareDatabase() {
    document = new Document();
    chapter = new Chapter();
    em.getTransaction().begin();
    em.persist(document);
    em.persist(chapter);
    em.getTransaction().commit();
  }
  
  private EntityRanking createBasicEntities() {
    final List<BasicEntity> list = new ArrayList<>();
    
    BasicEntity entity1 = new BasicEntity();
    entity1.setDisplayName(NAME1_1);
    entity1.setType(EntityType.PERSON);
    entity1.getOccurences().add(new TextSpan(chapter, OCCURANCE1_START, OCCURANCE1_END));
    entity1.getOccurences().add(new TextSpan(chapter, OCCURANCE2_START, OCCURANCE2_END));
    entity1.getNameAttributes().add(new Attribute(AttributeType.NAME, NAME1_1));
    entity1.getNameAttributes().add(new Attribute(AttributeType.NAME, NAME1_2));
    list.add(entity1);
    
    BasicEntity entity2 = new BasicEntity();
    entity2.setType(EntityType.PLACE);
    entity2.setDisplayName(NAME2);
    entity2.getOccurences().add(new TextSpan(chapter, OCCURANCE3_START, OCCURANCE3_END));
    entity2.getNameAttributes().add(new Attribute(AttributeType.NAME, NAME2));
    list.add(entity2);
    
    return new EntityRanking() {
      @Override
      public List<BasicEntity> getRankedEntities() {
        return list;
      }
    };
  }

  private EntityRelations createEntityRelations() {
    return new EntityRelations() {
      @Override
      public Map<BasicEntity, Double> getRelatedEntities(BasicEntity entity) {
        return ImmutableMap.of();
      }
    };
  }
}
