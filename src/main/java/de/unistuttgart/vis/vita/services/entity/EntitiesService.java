package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import de.unistuttgart.vis.vita.services.FingerprintsService;

/**
 * Redirects entity and relations requests for the current Document to the right sub service.
 */
@ManagedBean
public class EntitiesService {
  
  private String documentId;
  
  @Inject
  private EntityService entityService;

  @Inject
  private EntityRelationsService entityRelationsService;

  @Inject
  private FingerprintsService fingerprintsService;

  /**
   * Sets the id of the Document this service should refer to
   * 
   * @param docId - the id of the Document which this EntitiesService should refer to
   */
  public EntitiesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }
  
  /**
   * Returns the Service to access the Entity with the given id.
   * 
   * @param id - the id of the Entity to be accessed
   * @return the EntityService to access the given Entity with the given id
   */
  @Path("{entityId}")
  public EntityService getEntity(@PathParam("entityId") String id) {
    return entityService.setEntityId(id)
                                                            .setDocumentId(documentId);
  }
  
  /**
   * Returns the RelationsService for the current document.
   * 
   * @return the RelationsService which answers the request
   */
  @Path("/relations")
  public EntityRelationsService getRelations() {
    return entityRelationsService.setDocumentId(documentId);
  }
  
  /**
   * Returns the FingerprintsService for the current document.
   * 
   * @return the FingerprintsService which answers the request
   */
  @Path("/fingerprints")
  public FingerprintsService getFingerprints() {
    return fingerprintsService.setDocumentId(documentId);
  }

}
