package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@ManagedBean
public class FingerprintsService {
  
  private String documentId;

  @Inject
  private EntityManager em;

  public FingerprintsService setDocumentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public FingerprintsService getFingerprint(@QueryParam("steps") int steps,
                                              @QueryParam("rangeStart") double rangeStart,
                                              @QueryParam("rangeEnd") double rangeEnd,
                                              @QueryParam("entityIds") String eIds) {
    // TODO not implemented yet!
    return null;
  }

}
