package de.unistuttgart.vis.vita.services.entity;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class PersonsServiceDuringAnalysisTest extends PersonsServiceTest {

  @Override
  public AnalysisStatus getCurrentAnalysisStatus() {
    return AnalysisStatus.RUNNING;
  }

  /**
   * Tests whether a HTTP 409 Conflict response is sent if persons are requested during analysis.
   */
  @Override
  @Test
  public void testGetPersons() {
    Response actualResponse = target(path).queryParam("offset", DEFAULT_OFFSET)
        .queryParam("count", DEFAULT_COUNT)
        .request().get();
    assertEquals(409, actualResponse.getStatus());
  }
}