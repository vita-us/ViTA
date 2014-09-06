package de.unistuttgart.vis.vita.services;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.vis.vita.data.ProgressTestData;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * A service which provides a method to GET the analysis progress for a specific document.
 */
public class ProgressService {

  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String id;

  /**
   * Creates a new service returning the progress of the analysis of the current document.
   * 
   * @param uriInfo
   * @param request
   * @param id
   */
  public ProgressService(UriInfo uriInfo, Request request, String id) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.id = id;
  }
  
  /**
   * @return the progress of the analysis of the current document.
   */
  @GET
  public AnalysisProgress getProgress() {
    // TODO not implemented yet!
    return new ProgressTestData().createTestProgress();
  }

}
