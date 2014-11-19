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

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.PlaceTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.RelationConfiguration;
import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

public class EntityRelationsServiceTest extends ServiceTest {

  private static final String TEST_RELATION_TYPE_ALL = "all";
  private static final int TEST_STEPS = 100;
  private static final double TEST_RANGE_START = 0.0;
  private static final double TEST_RANGE_END = 1.0;
  private static final String TEST_RELATION_TYPE = "person";
  
  private static final int ERROR_STATUS = 400;
  
  private static final String TEST_RELATION_TYPE_ILLEGAL = "hobbits";
  private static final String TEST_NO_ENTITY_IDS = "";
  private static final double TEST_RANGE_ILLEGAL = -0.1;
  private static final int TEST_STEPS_ILLEGAL = -1;
  
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
  
  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    // set up test data instances
    this.personTestData = new PersonTestData();
    this.placeTestData = new PlaceTestData();
    this.relationTestData = new EntityRelationTestData();
    this.ids = new ArrayList<>();
    
    // set up test document (only needed for the path)
    Document testDoc = new DocumentTestData().createTestDocument(1);
    this.docId = testDoc.getId();
    
    // set up test persons and relation
    Person testPerson = personTestData.createTestPerson(1);
    ids.add(testPerson.getId());
    originPersonId = testPerson.getId();
    Person relatedPerson = personTestData.createTestPerson(2);
    ids.add(relatedPerson.getId());
    targetPersonId = relatedPerson.getId();
    EntityRelation testPersonRelation = relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(testPersonRelation);
    
    // set up test place and relation
    Place testPlace = placeTestData.createTestPlace(1);
    ids.add(testPlace.getId());
    originPlaceId = testPlace.getId();
    Place relatedPlace = placeTestData.createTestPlace(2);
    ids.add(relatedPlace.getId());
    targetPlaceId = relatedPlace.getId();
    EntityRelation testPlaceRelation = relationTestData.createTestRelation(testPlace, relatedPlace);
    testPlace.getEntityRelations().add(testPlaceRelation);
    
    EntityManager em = getModel().getEntityManager();
    
    // persist persons and their relation
    em.getTransaction().begin();
    em.persist(testDoc);
    em.persist(testPerson);
    em.persist(relatedPerson);
    em.persist(testPersonRelation);
    em.getTransaction().commit();
    
    // persist places and their relation
    em.getTransaction().begin();
    em.persist(testPlace);
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
    RelationsResponse actualResponse = target(path).queryParam("steps", TEST_STEPS)
                                                    .queryParam("rangeStart", TEST_RANGE_START)
                                                    .queryParam("rangeEnd", TEST_RANGE_END)
                                                    .queryParam("entityIds", ids)
                                                    .queryParam("type", TEST_RELATION_TYPE)
                                                    .request().get(RelationsResponse.class);
    
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
    assertEquals(originPersonId, actualConfig.getEntityAId());
    assertEquals(targetPersonId, actualConfig.getEntityBId());
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, 
                  actualConfig.getWeight(), 
                  EntityRelationTestData.DELTA);
  }
  
  /**
   * Check whether place relations can be caught via GET but contain no data.
   */
  @Test
  public void testGetPlaceRelations() {
    RelationsResponse actualResponse = target(path).queryParam("steps", TEST_STEPS)
                                                    .queryParam("rangeStart", TEST_RANGE_START)
                                                    .queryParam("rangeEnd", TEST_RANGE_END)
                                                    .queryParam("entityIds", ids)
                                                    .queryParam("type", "place")
                                                    .request().get(RelationsResponse.class);
    
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
    assertEquals(originPlaceId, actualConfig.getEntityAId());
    assertEquals(targetPlaceId, actualConfig.getEntityBId());
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, 
                  actualConfig.getWeight(), 
                  EntityRelationTestData.DELTA);
  }
  
  /**
   * Check whether all relations can be caught via GET.
   */
  @Test
  public void testGetAllRelations() {
    RelationsResponse actualResponse = target(path).queryParam("steps", TEST_STEPS)
                                                    .queryParam("rangeStart", TEST_RANGE_START)
                                                    .queryParam("rangeEnd", TEST_RANGE_END)
                                                    .queryParam("entityIds", ids)
                                                    .queryParam("type", TEST_RELATION_TYPE_ALL)
                                                    .request().get(RelationsResponse.class);
    assertNotNull(actualResponse);
    assertEquals(2, actualResponse.getRelations().size());
  }
  
  /**
   * Check whether server response is 500 when requesting relations with illegal range values.
   */
  @Test
  public void testGetRelationsWithIllegalRange() {
    Response response1 = target(path).queryParam("steps", TEST_STEPS)
        .queryParam("rangeStart", TEST_RANGE_ILLEGAL)
        .queryParam("rangeEnd", TEST_RANGE_END)
        .queryParam("entityIds", ids)
        .queryParam("type", TEST_RELATION_TYPE)
        .request().get();
    assertEquals(ERROR_STATUS, response1.getStatus());
    
    Response response2 = target(path).queryParam("steps", TEST_STEPS)
        .queryParam("rangeStart", TEST_RANGE_START)
        .queryParam("rangeEnd", TEST_RANGE_ILLEGAL)
        .queryParam("entityIds", ids)
        .queryParam("type", TEST_RELATION_TYPE)
        .request().get();
    assertEquals(ERROR_STATUS, response2.getStatus());
  }
  
  /**
   * Check whether server response is 500 when requesting relations without any entities.
   */
  @Test
  public void testGetRelationsWithoutEntities() {
    Response actualResponse = target(path).queryParam("steps", TEST_STEPS)
        .queryParam("rangeStart", TEST_RANGE_START)
        .queryParam("rangeEnd", TEST_RANGE_END)
        .queryParam("entityIds", TEST_NO_ENTITY_IDS)
        .queryParam("type", TEST_RELATION_TYPE)
        .request().get();
    assertEquals(ERROR_STATUS, actualResponse.getStatus());
  }
  
  /**
   * Check whether server response is 500 when requesting relations with illegal type.
   */
  @Test
  public void testGetRelationsWithIllegalType() {
    Response actualResponse = target(path).queryParam("steps", TEST_STEPS)
                                          .queryParam("rangeStart", TEST_RANGE_START)
                                          .queryParam("rangeEnd", TEST_RANGE_END)
                                          .queryParam("entityIds", ids)
                                          .queryParam("type", TEST_RELATION_TYPE_ILLEGAL)
                                          .request().get();
    assertEquals(ERROR_STATUS, actualResponse.getStatus());
  }

}
