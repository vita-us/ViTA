package de.unistuttgart.vis.vita.services.search;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.services.occurrence.OccurrencesService;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

@ManagedBean
public class SearchInDocumentService extends OccurrencesService {
  
  private String documentId;

  @Inject
  private EntityManager em;

  public SearchInDocumentService setDocumentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart, 
                                            @QueryParam("rangeEnd") double rangeEnd,
                                            @QueryParam("query") String searchQuery) {

    // TODO implement SearchInDocumentService

    return new OccurrencesResponse();
  }

}
