package de.unistuttgart.vis.vita.services.entity;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Checks whether a plot can be caught during analysis.
 */
public class PlotViewDuringAnalysisTest extends PlotViewServiceTest {

  @Override
  public AnalysisStatus getCurrentAnalysisStatus() {
    return AnalysisStatus.RUNNING;
  }

  @Override
  public void getPlotView() {
    Response actualResponse = target(path).request().get();
    assertEquals(409, actualResponse.getStatus());
  }

}
