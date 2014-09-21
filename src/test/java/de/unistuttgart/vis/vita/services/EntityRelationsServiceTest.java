package de.unistuttgart.vis.vita.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.RelationConfiguration;
import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

public class EntityRelationsServiceTest extends ServiceTest {

  private static final int TEST_STEPS = 100;
  private static final double TEST_RANGE_START = 0.0;
  private static final double TEST_RANGE_END = 1.0;
  private static final String TEST_RELATION_TYPE = "person";
  
  private PersonTestData personTestData;
  private EntityRelationTestData relationTestData;
  private String docId;
  private String originId;
  private String targetId;
  private List<String> ids;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    this.personTestData = new PersonTestData();
    this.relationTestData = new EntityRelationTestData();
    this.ids = new ArrayList<>();
    
    // set up test data
    Document testDoc = new DocumentTestData().createTestDocument(1);
    this.docId = testDoc.getId();
    
    Person testPerson = personTestData.createTestPerson(1);
    ids.add(testPerson.getId());
    originId = testPerson.getId();
    
    Person relatedPerson = personTestData.createTestPerson(2);
    targetId = relatedPerson.getId();
    
    EntityRelation<Entity> testRelation = relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(testRelation);
    
    // persist the created test data
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(testDoc);
    em.persist(testPerson);
    em.persist(relatedPerson);
    em.persist(testRelation);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(EntityRelationsService.class);
  }

  @Test
  public void testGetRelations() {
    String path = "/documents/" + docId + "/entities/relations";
    
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
    
    // check relation configurations
    List<RelationConfiguration> actualRelations = actualResponse.getRelations();
    assertEquals(1, actualRelations.size());
    
    // check configuration of one and only relation
    RelationConfiguration actualConfig = actualRelations.get(0);
    assertEquals(originId, actualConfig.getEntityAId());
    assertEquals(targetId, actualConfig.getEntityBId());
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, 
                  actualConfig.getWeight(), 
                  EntityRelationTestData.DELTA);
  }

}
