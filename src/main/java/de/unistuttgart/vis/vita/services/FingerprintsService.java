package de.unistuttgart.vis.vita.services;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.services.occurrence.OccurrencesService;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Provides a method to GET the fingerprint of a given list of entities.
 */
public class FingerprintsService extends OccurrencesService {

  /**
   * Creates a new FingerprintsService and injects Model.
   * 
   * @param model - the injected Model
   */
  @Inject
  public FingerprintsService(Model model) {
    em = model.getEntityManager();
  }

  /**
   * Sets the id of the document in which the entities occur in.
   * 
   * @param docId - the id of the Document
   * @return this FingerprintsService
   */
  public FingerprintsService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getFingerprint(@QueryParam("steps") int steps,
                                              @QueryParam("rangeStart") double rangeStart,
                                              @QueryParam("rangeEnd") double rangeEnd,
                                              @QueryParam("entityIds") String eIds) {
    // TODO implement FingerprintsService!
    return null;
  }

  @Override
  protected int getNumberOfSpansInStep(int stepStart, int stepEnd) {
    // TODO not implemented yet!
    return 0;
  }

}
