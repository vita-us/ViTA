package de.unistuttgart.vis.vita.services.occurrence;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

/**
 * Service providing a method to get the occurrences for relations in the current document via 
 * GET.
 */
public class RelationOccurrencesService {
  
  private EntityManager em;
  
  private String documentId;
  
  @Inject
  public RelationOccurrencesService(Model model) {
    em = model.getEntityManager();
  }
  
  public RelationOccurrencesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RelationsResponse getOccurrences(@QueryParam("steps") int steps,
                                          @QueryParam("rangeStart") double rangeStart,
                                          @QueryParam("rangeEnd") double rangeEnd,
                                          @QueryParam("entityIds") String entityIds) {
    // TODO not implemented yet!
    return null;
  }

}
