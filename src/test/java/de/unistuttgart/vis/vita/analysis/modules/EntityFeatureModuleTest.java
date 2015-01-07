package de.unistuttgart.vis.vita.analysis.modules;

import com.google.common.collect.ImmutableMap;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.EntityAttributes;
import de.unistuttgart.vis.vita.analysis.results.EntityRanking;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.analysis.results.EntityWordCloudResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityType;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityFeatureModuleTest {
  private static final String NAME1_1 = "Frodo";
  private static final String NAME1_2 = "Mr. Frodo";
  private static final String GENDER1 = "male";
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
  private BasicEntity entity1;
  private BasicEntity entity2;

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
    EntityWordCloudResult wordClouds = getWordClouds();
    EntityAttributes entityAttributes = createEntityAttributes();

    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(Model.class)).thenReturn(model);
    when(resultProvider.getResultFor(EntityRanking.class)).thenReturn(entities);
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(context);
    when(resultProvider.getResultFor(EntityRelations.class)).thenReturn(relations);
    when(resultProvider.getResultFor(EntityWordCloudResult.class)).thenReturn(wordClouds);
    when(resultProvider.getResultFor(EntityAttributes.class)).thenReturn(entityAttributes);

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
    assertThat(person.getFrequency(), is(2));

    assertThat(document.getContent().getPlaces(), hasSize(1));
    Place place = document.getContent().getPlaces().get(0);
    assertThat(place.getDisplayName(), is(NAME2));
    assertThat(place.getType(), is(EntityType.PLACE));
    assertThat(place.getFrequency(), is(1));
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

    Person person1 = document.getContent().getPersons().get(0);
    Attribute gender1 = getGenderAttribute(person1);
    assertThat(gender1.getContent(), is("male"));
  }

  @Test
  public void testOccurrencesArePersisted() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    Place place = document.getContent().getPlaces().get(0);
    assertThat(place.getOccurrences(), contains(
        new Range(chapter, OCCURANCE3_START, OCCURANCE3_END)));
  }

  @Test
  public void testWordCloudIsPersisted() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    Person person = document.getContent().getPersons().get(0);
    WordCloud wordCloud = person.getWordCloud();
    assertThat(wordCloud.getItems(), contains(
        new WordCloudItem("peril", 4), new WordCloudItem("comfortable", 1)));
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

    entity1 = new BasicEntity();
    entity1.setDisplayName(NAME1_1);
    entity1.setType(EntityType.PERSON);
    entity1.getOccurences().add(new Range(chapter, OCCURANCE1_START, OCCURANCE1_END));
    entity1.getOccurences().add(new Range(chapter, OCCURANCE2_START, OCCURANCE2_END));
    entity1.getNameAttributes().add(new Attribute(AttributeType.NAME, NAME1_1));
    entity1.getNameAttributes().add(new Attribute(AttributeType.NAME, NAME1_2));
    list.add(entity1);

    entity2 = new BasicEntity();
    entity2.setType(EntityType.PLACE);
    entity2.setDisplayName(NAME2);
    entity2.getOccurences().add(new Range(chapter, OCCURANCE3_START, OCCURANCE3_END));
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

      @Override
      public double[] getWeightOverTime(BasicEntity entity1, BasicEntity entity2) {
        return new double[] { 0, 2, 1 };
      }
    };
  }

  private EntityWordCloudResult getWordClouds() {
    WordCloud wordCloud = new WordCloud(Arrays.asList(
        new WordCloudItem("peril", 4), new WordCloudItem("comfortable", 1)));

    EntityWordCloudResult result = mock(EntityWordCloudResult.class);
    when(result.getWordCloudForEntity(entity1)).thenReturn(wordCloud);
    when(result.getWordCloudForEntity(entity2)).thenReturn(wordCloud);

    return result;
  }

  private EntityAttributes createEntityAttributes() {
    final Map<BasicEntity, Set<Attribute>> entityToAttributes = new HashMap<>();
    Set<Attribute> forEntity1 = new HashSet<>();
    forEntity1.add(new Attribute(AttributeType.GENDER, GENDER1));
    entityToAttributes.put(entity1, forEntity1);

    EntityAttributes result = mock(EntityAttributes.class);
    when(result.getAttributesForEntity(entity1)).thenReturn(forEntity1);
    when(result.getAttributesForEntity(entity2)).thenReturn(new HashSet<Attribute>());

    return result;
  }

  private Attribute getGenderAttribute(Entity entity) {
    Set<Attribute> attributesForEntity = entity.getAttributes();

    for (Attribute attribute : attributesForEntity) {
      if (attribute.getType() == AttributeType.GENDER) {
        return attribute;
      }
    }

    return null;
  }
}
