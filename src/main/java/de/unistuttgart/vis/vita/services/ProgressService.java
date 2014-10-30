package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * A service which provides a method to GET the analysis progress for a specific document.
 */
@ManagedBean
public class ProgressService {
  private String documentId;

  private EntityManager em;
  
  /**
   * Creates new ProgressService and injects Model.
   * 
   * @param model - the injected Model
   */
  @Inject
  public ProgressService(Model model) {
    em = model.getEntityManager();
  }
  
  /**
   * Sets the id of the document this should represent the progress of
   * @param id the id
   * @return this
   */
  public ProgressService setDocumentId(String id) {
    this.documentId = id;
    return this;
  }
  
  /**
   * @return the progress of the analysis of the current document.
   */
  @GET
  public AnalysisProgress getProgress() {
    AnalysisProgress readProgress = null;
    
    try {
      readProgress = readProgressFromDatabase();
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }
    
    return readProgress;
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
