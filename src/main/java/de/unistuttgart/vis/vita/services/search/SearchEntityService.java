package de.unistuttgart.vis.vita.services.search;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

public class SearchEntityService {
  
  private String documentId;
  private String entityId;

  private EntityManager em;

  @Context
  private ResourceContext resourceContext;

  @Inject
  public SearchEntityService(Model model) {
    em = model.getEntityManager();
  }

  public SearchEntityService setDocumentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  public SearchEntityService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart, 
                                            @QueryParam("rangeEnd") double rangeEnd) {

    // TODO implement SearchEntityService

    return new OccurrencesResponse();
  }

}
