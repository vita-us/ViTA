package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * A service which provides a method to GET the analysis progress for a specific document.
 */
public class ProgressService {

  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  
  private String documentId;
  private EntityManager em;

  /**
   * Creates a new service returning the progress of the analysis of the current document.
   * 
   * @param uriInfo
   * @param request
   * @param docId
   */
  public ProgressService(UriInfo uriInfo, Request request, String docId) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.documentId = docId;
    
    this.em = Model.createUnitTestModelWithoutDrop().getEntityManager();
  }
  
  /**
   * @return the progress of the analysis of the current document.
   */
  @GET
  public AnalysisProgress getProgress() {
    return readProgressFromDatabase();
  }
  
  /**
   * Reads the analysis progress of the current document from database and returns it.
   * 
   * @return analysis progress of the current document
   */
  private AnalysisProgress readProgressFromDatabase() {
    TypedQuery<AnalysisProgress> query =
        em.createNamedQuery("AnalysisProgress.findProgressByDocumentId", AnalysisProgress.class);
    query.setParameter("documentId", documentId);
    return query.getSingleResult();
  }

}
