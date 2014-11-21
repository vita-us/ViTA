package de.unistuttgart.vis.vita.services.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.AttributeTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PlaceTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.entity.PlaceService;

/**
 * Performs test on PlaceService to check whether GET works correctly.
 */
public class PlaceServiceTest extends ServiceTest {
  
  private String docId;
  private String placeId;
  private PlaceTestData placeTestData;
  private AttributeTestData attributeTestData;
  private EntityRelationTestData relationTestData;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = getModel().getEntityManager();
    
    // set up test data
    placeTestData = new PlaceTestData();
    attributeTestData = new AttributeTestData();
    relationTestData = new EntityRelationTestData();
    Attribute testAttribute = attributeTestData.createTestAttribute(1);
    Place testPlace = placeTestData.createTestPlace(1);
    Place relatedPlace = placeTestData.createTestPlace(2);
    EntityRelation testRelation = relationTestData.createTestRelation(testPlace, relatedPlace);
    testPlace.getEntityRelations().add(testRelation);
    
    testPlace.getAttributes().add(testAttribute);
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPlaces().add(testPlace);
    
    // save ids for request
    docId = testDoc.getId();
    placeId = testPlace.getId();
    
    // persist test data
    em.getTransaction().begin();
    em.persist(testAttribute);
    em.persist(testPlace);
    em.persist(testRelation);
    em.persist(relatedPlace);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(PlaceService.class);
  }

  /**
   * Checks whether a Place can be got using REST.
   */
  @Test
  public void testGetPerson() {
    String path = "documents/" + docId + "/places/" + placeId;
    Place responsePlace = target(path).request().get(Place.class);
    
    placeTestData.checkData(responsePlace, 1);
    
    // check the attributes
    Set<Attribute> personsAttributes = responsePlace.getAttributes();
    
    assertNotNull(personsAttributes);
    assertEquals(1, personsAttributes.size());
    
    for (Attribute attribute: personsAttributes) {
      attributeTestData.checkData(attribute, 1);
    }
    
    Set<EntityRelation> relations = responsePlace.getEntityRelations();
    assertEquals(1, relations.size());
    
    // can only check whether the weight is correct, other fields are null
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, relations.iterator().next().getWeight(), 0.001);
  }

}
