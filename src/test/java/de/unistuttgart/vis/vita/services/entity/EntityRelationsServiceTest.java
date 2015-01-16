package de.unistuttgart.vis.vita.services.entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.PlaceTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.RelationConfiguration;
import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

public class EntityRelationsServiceTest extends ServiceTest {

  private static final String TEST_RELATION_TYPE_ALL = "all";
  private static final double TEST_RANGE_START = 0.0;
  private static final double TEST_RANGE_END = 1.0;
  private static final String TEST_RELATION_TYPE = "person";

  private static final int ERROR_STATUS = 400;

  private static final String TEST_RELATION_TYPE_ILLEGAL = "hobbits";
  private static final String TEST_NO_ENTITY_IDS = "";
  private static final double TEST_RANGE_ILLEGAL = -0.1;

  private PersonTestData personTestData;
  private PlaceTestData placeTestData;
  private EntityRelationTestData relationTestData;

  private String docId;
  private String originPersonId;
  private String targetPersonId;
  private String originPlaceId;
  private String targetPlaceId;

  private List<String> ids;

  private String path;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    // set up test data instances
    this.personTestData = new PersonTestData();
    this.placeTestData = new PlaceTestData();
    this.relationTestData = new EntityRelationTestData();
    this.ids = new ArrayList<>();

    // set up test document
    Document testDoc = new DocumentTestData().createTestDocument(1);
    this.docId = testDoc.getId();

    // set up chapter
    Chapter testChapter = new ChapterTestData().createTestChapter();
    testChapter.setNumber(1);
    testChapter.setTitle("First Chapter");

    // Set range of the chapter
    TextPosition rangeStartPos =
        TextPosition.fromGlobalOffset(testChapter, 0,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition rangeEndPos =
        TextPosition.fromGlobalOffset(testChapter, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range chapterRangeSpan = new Range(rangeStartPos, rangeEndPos);
    testChapter.setRange(chapterRangeSpan);

    // set up document part
    DocumentPart docPart = new DocumentPart();
    docPart.getChapters().add(testChapter);
    testDoc.getContent().getParts().add(docPart);

    // set up test persons and relation
    Person testPerson = personTestData.createTestPerson(1);
    ids.add(testPerson.getId());
    originPersonId = testPerson.getId();

    Person relatedPerson = personTestData.createTestPerson(2);
    ids.add(relatedPerson.getId());
    targetPersonId = relatedPerson.getId();

    EntityRelation testPersonRelation =
        relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(testPersonRelation);
    // back-relation
    testPerson.getEntityRelations().add(
        relationTestData.createTestRelation(relatedPerson, testPerson));

    // create sentence
    Range testPersonSentenceRange = new Range(testChapter, 0, 1500, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence testPersonSentence = new Sentence(testPersonSentenceRange, testChapter, 0);
    
    // add occurrences
    Range testPersonRange = new Range(testChapter, 0, 1000, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence testPersonOccurrence = new Occurrence(testPersonSentence, testPersonRange);
    testPerson.getOccurrences().add(testPersonOccurrence);

    Range relatedPersonRange = new Range(testChapter, 500, 1500, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence relatedPersonOccurrence = new Occurrence(testPersonSentence, relatedPersonRange);
    relatedPerson.getOccurrences().add(relatedPersonOccurrence);

    // set up test place and relation
    Place testPlace = placeTestData.createTestPlace(1);
    ids.add(testPlace.getId());
    originPlaceId = testPlace.getId();

    Place relatedPlace = placeTestData.createTestPlace(2);
    ids.add(relatedPlace.getId());
    targetPlaceId = relatedPlace.getId();

    EntityRelation testPlaceRelation = relationTestData.createTestRelation(testPlace, relatedPlace);
    testPlace.getEntityRelations().add(testPlaceRelation);
    // back-relation
    testPlace.getEntityRelations()
        .add(relationTestData.createTestRelation(relatedPlace, testPlace));

    // create sentence
    Range testPlaceSentenceRange = new Range(testChapter, 1500, 3000, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence testPlaceSentence = new Sentence(testPlaceSentenceRange, testChapter, 0);
    
    // add occurrences
    Range testPlaceRange = new Range(testChapter, 1500, 2500, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence testPlaceOccurrence = new Occurrence(testPlaceSentence, testPlaceRange);
    testPlace.getOccurrences().add(testPlaceOccurrence);

    Range relatedPlaceRange = new Range(testChapter, 2000, 3000, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence relatedPlaceOccurrence = new Occurrence(testPlaceSentence, relatedPlaceRange);
    relatedPlace.getOccurrences().add(relatedPlaceOccurrence);

    EntityManager em = getModel().getEntityManager();

    // persist persons and their relation
    em.getTransaction().begin();

    em.persist(chapterRangeSpan);
    em.persist(testChapter);
    em.persist(docPart);
    em.persist(testPersonOccurrence);
    em.persist(testPerson);
    em.persist(relatedPersonOccurrence);
    em.persist(relatedPerson);
    em.persist(testPersonRelation);
    em.persist(testDoc);

    em.getTransaction().commit();

    // persist places and their relation
    em.getTransaction().begin();
    em.persist(testPlaceOccurrence);
    em.persist(testPlace);
    em.persist(relatedPlaceOccurrence);
    em.persist(relatedPlace);
    em.persist(testPlaceRelation);
    em.getTransaction().commit();

    em.close();

    // set up path for all tests
    path = "/documents/" + docId + "/entities/relations";
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(EntityRelationsService.class);
  }

  /**
   * Check whether person relations can be caught via GET.
   */
  @Test
  public void testGetPersonRelations() {
    RelationsResponse actualResponse =
        target(path).queryParam("rangeStart", TEST_RANGE_START)
            .queryParam("rangeEnd", TEST_RANGE_END).queryParam("entityIds", ids)
            .queryParam("type", TEST_RELATION_TYPE).request().get(RelationsResponse.class);

    // check list of entity ids
    List<String> actualEntityIds = actualResponse.getEntityIds();
    assertEquals(ids.size(), actualEntityIds.size());
    assertEquals(ids.get(0), actualEntityIds.get(0));
    assertEquals(ids.get(1), actualEntityIds.get(1));

    // check relation configurations
    List<RelationConfiguration> actualRelations = actualResponse.getRelations();
    assertEquals(1, actualRelations.size());

    // check configuration of one and only relation
    RelationConfiguration actualConfig = actualRelations.get(0);
    // relation in either direction
    if (actualConfig.getEntityAId().equals(originPersonId)) {
      assertEquals(targetPersonId, actualConfig.getEntityBId());
    } else {
      assertEquals(originPersonId, actualConfig.getEntityBId());
      assertEquals(targetPersonId, actualConfig.getEntityAId());
    }
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, actualConfig.getWeight(),
        EntityRelationTestData.DELTA);
  }

  /**
   * Check whether place relations can be caught via GET but contain no data.
   */
  @Test
  public void testGetPlaceRelations() {
    RelationsResponse actualResponse =
        target(path).queryParam("rangeStart", TEST_RANGE_START)
            .queryParam("rangeEnd", TEST_RANGE_END).queryParam("entityIds", ids)
            .queryParam("type", "place").request().get(RelationsResponse.class);

    // check list of entity ids
    List<String> actualEntityIds = actualResponse.getEntityIds();
    assertEquals(ids.size(), actualEntityIds.size());
    assertEquals(ids.get(0), actualEntityIds.get(0));
    assertEquals(ids.get(1), actualEntityIds.get(1));

    // check relation configurations
    List<RelationConfiguration> actualRelations = actualResponse.getRelations();
    assertEquals(1, actualRelations.size());

    // check configuration of one and only relation
    RelationConfiguration actualConfig = actualRelations.get(0);
    // relation in either direction
    if (actualConfig.getEntityAId().equals(originPlaceId)) {
      assertEquals(targetPlaceId, actualConfig.getEntityBId());
    } else {
      assertEquals(originPlaceId, actualConfig.getEntityBId());
      assertEquals(targetPlaceId, actualConfig.getEntityAId());
    }
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, actualConfig.getWeight(),
        EntityRelationTestData.DELTA);
  }

  /**
   * Check whether all relations can be caught via GET.
   */
  @Test
  public void testGetAllRelations() {
    RelationsResponse actualResponse =
        target(path).queryParam("rangeStart", TEST_RANGE_START)
            .queryParam("rangeEnd", TEST_RANGE_END).queryParam("entityIds", ids)
            .queryParam("type", TEST_RELATION_TYPE_ALL).request().get(RelationsResponse.class);
    assertNotNull(actualResponse);
    assertEquals(2, actualResponse.getRelations().size());
  }

  /**
   * Check whether server response is 500 when requesting relations with illegal range values.
   */
  @Test
  public void testGetRelationsWithIllegalRange() {
    Response response1 =
        target(path).queryParam("rangeStart", TEST_RANGE_ILLEGAL)
            .queryParam("rangeEnd", TEST_RANGE_END).queryParam("entityIds", ids)
            .queryParam("type", TEST_RELATION_TYPE).request().get();
    assertEquals(ERROR_STATUS, response1.getStatus());

    Response response2 =
        target(path).queryParam("rangeStart", TEST_RANGE_START)
            .queryParam("rangeEnd", TEST_RANGE_ILLEGAL).queryParam("entityIds", ids)
            .queryParam("type", TEST_RELATION_TYPE).request().get();
    assertEquals(ERROR_STATUS, response2.getStatus());
  }

  /**
   * Check whether server response is 500 when requesting relations without any entities.
   */
  @Test
  public void testGetRelationsWithoutEntities() {
    Response actualResponse =
        target(path).queryParam("rangeStart", TEST_RANGE_START)
            .queryParam("rangeEnd", TEST_RANGE_END).queryParam("entityIds", TEST_NO_ENTITY_IDS)
            .queryParam("type", TEST_RELATION_TYPE).request().get();
    assertEquals(ERROR_STATUS, actualResponse.getStatus());
  }

  /**
   * Check whether server response is 500 when requesting relations with illegal type.
   */
  @Test
  public void testGetRelationsWithIllegalType() {
    Response actualResponse =
        target(path).queryParam("rangeStart", TEST_RANGE_START)
            .queryParam("rangeEnd", TEST_RANGE_END).queryParam("entityIds", ids)
            .queryParam("type", TEST_RELATION_TYPE_ILLEGAL).request().get();
    assertEquals(ERROR_STATUS, actualResponse.getStatus());
  }

}
