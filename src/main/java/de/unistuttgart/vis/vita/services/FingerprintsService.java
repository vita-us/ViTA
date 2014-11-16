package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.services.occurrence.OccurrencesService;

/**
 * Provides a method to GET the fingerprint of a given list of entities.
 */
@ManagedBean
public class FingerprintsService extends OccurrencesService {

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
    // TODO implement FingerprintsService!
    return null;
  }

  @Override
  protected long getNumberOfSpansInStep(int stepStart, int stepEnd) {
    // TODO not implemented yet!
    return 0;
  }

}
