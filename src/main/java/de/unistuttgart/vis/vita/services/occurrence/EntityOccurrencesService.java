package de.unistuttgart.vis.vita.services.occurrence;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Provides a method to GET the occurrences of the current entity.
 */
public class EntityOccurrencesService {
  
  private EntityManager em;
  
  private String entityId;
  
  @Inject
  public EntityOccurrencesService(Model model) {
    em = model.getEntityManager();
  }

  /**
   * Sets the id of the entity which occurrences should be got and returns this 
   * EntityOccurrencesService.
   * 
   * @param eId - the id of the entity which occurrences should be got
   * @return this AttributeOccurrencesService
   */
  public EntityOccurrencesService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }
  
  /**
   * Reads occurrences of the specific entity from database and returns them in JSON.
   * 
   * @param steps - maximum amount of occurrences
   * @param rangeStart - start of range to be searched in
   * @param rangeEnd - end of range to be searched in
   * @return an OccurenceResponse holding all found Occurrences
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart,
                                            @QueryParam("rangeEnd") double rangeEnd) {
    // TODO implement AttributeOccurrencesService
    return new OccurrencesResponse();
  }

}
