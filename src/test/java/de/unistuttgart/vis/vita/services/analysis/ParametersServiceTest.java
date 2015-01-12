package de.unistuttgart.vis.vita.services.analysis;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.ServiceTest;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
    String path = "documents/" + documentId + "/parameters/available";

    // send request and get response
    Response actualResponse = target(path).request().get();

    // check if response is returned and not null
    assertNotNull(actualResponse);
    assertEquals(200, actualResponse.getStatus());
  }

  /**
   * Checks if the correct current parameters are returned.
   */
  @Test
  public void testGetParameters() {
    // set up request
    String path = "documents/" + documentId + "/parameters/current";

    // send request and get response
    AnalysisParameters actualResponse = target(path).request().get(AnalysisParameters.class);

    // check response for validity
    assertNotNull(actualResponse);
    assertThat(actualResponse.getRelationTimeStepCount(), is(20));
    assertThat(actualResponse.getWordCloudItemsCount(), is(100));
    assertThat(actualResponse.isStopWordListEnabled(), is(true));

  }

}
