package de.unistuttgart.vis.vita.services.analysis;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.services.requests.DocumentIdRequest;

/**
 * Provides a method to tell the server to stop the analysis for the current Document.
 */
@ManagedBean
public class AnalysisService {
  
  private String documentId;
  
  @Inject
  private AnalysisController analysisController;

  /**
   * Sets the id of the document this service refers to.
   * 
   * @param documentId the id
   */
  public AnalysisService setDocumentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  /**
   * Tells the AnalysisController to stop the analysis for the current Document.
   * 
   * @return a response indicating whether analysis was stopped or not
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/stop")
  public Response stopAnalysis(DocumentIdRequest idRequest) {
    Response response = null;
    
    if (documentId.equals(idRequest.getId())) {
      analysisController.cancelAnalysis(idRequest.getId());
      response = Response.noContent().build();
    } else {
      response = Response.serverError().build();
    }
    
    return response;
  }
  
  /**
   * Restarts a previously canceled analysis.
   * 
   * @return Response with HTTP 204 status if analysis could be restarted
   */
  @PUT
  @Path("/restart")
  public Response restartAnalysis() {
    Response response = null;
    
    try {
      analysisController.restartAnalysis(documentId);
      response = Response.noContent().build();
    } catch (IllegalArgumentException iae) {
      response = Response.serverError().build();
    }
    
    return response;
  }

}
