package de.unistuttgart.vis.vita.services.document;

import javax.annotation.ManagedBean;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.dao.DocumentPartDao;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.DocumentPartsResponse;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a method to GET all parts of the document this service refers to.
 */
@ManagedBean
public class DocumentPartsService extends BaseService {
  
  private String documentId;

  private DocumentDao documentDao;
  private DocumentPartDao documentPartDao;

  private final Logger LOGGER = Logger.getLogger(DocumentPartsService.class.getName());

  @Override public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
    documentPartDao = getDaoFactory().getDocumentPartDao();
  }

  /**
   * Sets the id of the document for which this service should provide the DocumentParts.
   * 
   * @param docId - the id of the document
   * @return this PartsService
   */
  public DocumentPartsService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }
  
  /**
   * Returns a PartsResponse including a list of DocumentParts in current document.
   * 
   * @return PartsResponse including a list of DocumentParts
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public DocumentPartsResponse getParts() {
    List<DocumentPart> parts = documentPartDao.findPartsInDocument(documentId);

    // check whether analysis is still running
    if (!documentDao.isAnalysisFinished(documentId)) {

      // parts and chapters are not detected yet
      if (parts.isEmpty()) {
        LOGGER.log(Level.FINEST, "List of document parts requested, but not detected yet.");
        throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
      }
    }

    return new DocumentPartsResponse(parts);
  }

}
