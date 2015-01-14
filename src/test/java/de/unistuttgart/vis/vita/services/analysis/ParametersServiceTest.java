package de.unistuttgart.vis.vita.services.analysis;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.ServiceTest;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParametersServiceTest extends ServiceTest {

  private String documentId;
  private EntityManager em;

  @Before
  public void setUp() throws Exception {
    super.setUp();

    em = getModel().getEntityManager();

    // need a document to stop its analysis
    Document testDocument = new DocumentTestData().createTestDocument(1);

    // save id for request
    documentId = testDocument.getId();

    // persist test document
    em.getTransaction().begin();
    em.persist(testDocument);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(ParametersService.class);
  }

  /**
   * Checks if response is returned for all available parameters.
   */
  @Test
  public void testGetAvailableParameters() {
    // set up request
    String path = "analysis-parameters";

    // send request and get response
    Response actualResponse = target(path).request().get();

    // check if response is returned and not null
    assertNotNull(actualResponse);
    assertEquals(200, actualResponse.getStatus());
  }
}
