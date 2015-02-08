package de.unistuttgart.vis.vita.services.analysis;

import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;
import de.unistuttgart.vis.vita.services.BaseService;

import javax.annotation.ManagedBean;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * A service which provides a method to GET the analysis progress for a specific document.
 */
@ManagedBean
public class ProgressService extends BaseService {
  
  private String documentId;

  private DocumentDao documentDao;

  @Override public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
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
    // Make sure the document exists
    Document doc;
    
    try {
      doc = documentDao.findById(documentId);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }
    
    // Right after document creation, the progress is not persisted. It is assumed to be zero.
    if (doc.getProgress() == null) {
      return new AnalysisProgress();
    }

    return doc.getProgress();
  }

}
