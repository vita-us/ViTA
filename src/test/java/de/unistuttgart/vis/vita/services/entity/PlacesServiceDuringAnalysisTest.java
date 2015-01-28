package de.unistuttgart.vis.vita.services.entity;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Tests whether the PlacesService returns a HTTP 409 Conflict when its called during the Analysis.
 */
public class PlacesServiceDuringAnalysisTest extends PlacesServiceTest {

  @Override
  public AnalysisStatus getCurrentAnalysisStatus() {
    return AnalysisStatus.RUNNING;
  }

  /**
   * Checks whether requesting list of places during analysis will result in HTTP 409 Conflict.
   */
  @Override
  @Test
  public void testGetPlaces() {
    Response actualResponse = target(path).queryParam("offset", 0)
        .queryParam("count", 10)
        .request().get();
    assertEquals(409, actualResponse.getStatus());
  }

}
