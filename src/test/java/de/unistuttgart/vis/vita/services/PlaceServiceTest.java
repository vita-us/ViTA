package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PlaceTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Performs test on PlaceService to check whether GET works correctly.
 */
public class PlaceServiceTest extends ServiceTest {
  
  private String docId;
  private String placeId;
  private PlaceTestData testData;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    // set up test data
    testData = new PlaceTestData();
    Place testPlace = testData.createTestPlace(1);
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPlaces().add(testPlace);
    
    // save ids for request
    docId = testDoc.getId();
    placeId = testPlace.getId();
    
    // persist test data
    em.getTransaction().begin();
    em.persist(testPlace);
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
    
    testData.checkData(responsePlace, 1);
  }

}
