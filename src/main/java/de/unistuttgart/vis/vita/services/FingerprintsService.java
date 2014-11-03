package de.unistuttgart.vis.vita.services;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;

public class FingerprintsService {
  
  private String documentId;

  private EntityManager em;

  @Context
  private ResourceContext resourceContext;

  @Inject
  public FingerprintsService(Model model) {
    em = model.getEntityManager();
  }

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
