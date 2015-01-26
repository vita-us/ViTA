package de.unistuttgart.vis.vita.services.search;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SearchDuringAnalysisTest extends SearchInDocumentServiceTest {

  @Override
  public AnalysisStatus getCurrentAnalysisStatus() {
    return AnalysisStatus.RUNNING;
  }

  @Override
  public void testGetExactOccurrences() {
    Response actualResponse = target(getPath()).queryParam("rangeStart", DEFAULT_RANGE_START)
                                                .queryParam("rangeEnd", DEFAULT_RANGE_END)
                                                .queryParam("query", "very").request().get();
    assertEquals(409, actualResponse.getStatus());
  }

  @Override
  public void testGetStepwiseOccurences() {
    Response actualResponse = target(getPath()).queryParam("rangeStart", DEFAULT_RANGE_START)
                                                .queryParam("rangeEnd", DEFAULT_RANGE_END)
                                                .queryParam("steps", 2)
                                                .queryParam("query", "very")
                                                .request().get();
    assertEquals(409, actualResponse.getStatus());
  }
}
