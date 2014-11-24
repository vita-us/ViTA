package de.unistuttgart.vis.vita.services.entity;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Performs tests on PlaceService to check whether GET works correctly.
 */
public class PlaceServiceTest extends EntityServiceTest {

  @Override
  protected Application configure() {
    return new ResourceConfig(PlaceService.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    // create places
    Place testPlace = placeTestData.createTestPlace(1);
    Place relatedPlace = placeTestData.createTestPlace(2);

    // create relation
    EntityRelation testRelation = relationTestData.createTestRelation(testPlace, relatedPlace);
    testPlace.getEntityRelations().add(testRelation);

    // create attribute
    Attribute testAttribute = attributeTestData.createTestAttribute(1);
    testPlace.getAttributes().add(testAttribute);

    // add places to document
    testDoc.getContent().getPlaces().add(testPlace);
    
    // save id for request
    entityId = testPlace.getId();
    
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
  public void testGetEntity() {
    Place responseEntity = target(getPath("entities")).request().get(Place.class);
    check(responseEntity);
  }

  /**
   * Checks whether a Place can be got using REST.
   */
  @Test
  public void testGetPerson() {
    Place responsePlace = target(getPath("places")).request().get(Place.class);
    check(responsePlace);
  }

}
