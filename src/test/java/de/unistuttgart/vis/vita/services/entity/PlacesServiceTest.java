package de.unistuttgart.vis.vita.services.entity;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PlaceTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.services.AnalysisAware;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.PlacesResponse;

/**
 * Performs test on the PlacesService.
 */
public class PlacesServiceTest extends ServiceTest implements AnalysisAware {
  
  private String docId;
  private PlaceTestData testData;
  protected String path;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em  = getModel().getEntityManager();
    
    testData = new PlaceTestData();
    Place testPlace = testData.createTestPlace();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPlaces().add(testPlace);
    testDoc.getProgress().setStatus(getCurrentAnalysisStatus());
    
    docId = testDoc.getId();

    path = "documents/" + docId + "/places";
    
    em.getTransaction().begin();
    em.persist(testPlace);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(PlacesService.class);
  }

  @Override
  public AnalysisStatus getCurrentAnalysisStatus() {
    return AnalysisStatus.FINISHED;
  }

  /**
   * Checks whether places can be caught using the REST interface.
   */
  @Test
  public void testGetPlaces() {
    PlacesResponse actualResponse = target(path).queryParam("offset", 0)
                                                .queryParam("count", 10)
                                                .request().get(PlacesResponse.class);
    assertEquals(1, actualResponse.getTotalCount());
    List<Place> places = actualResponse.getPlaces();
    assertEquals(1, places.size());
    testData.checkData(places.get(0));
  }

}
