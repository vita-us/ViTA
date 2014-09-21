package de.unistuttgart.vis.vita.services;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

/**
 * Provides method returning EntityRelations requested using GET.
 */
public class EntityRelationsService {
  
  private EntityManager em;
  
  @Inject
  public EntityRelationsService(Model model) {
    em = model.getEntityManager();
  }
  
  /**
   * Returns the EntityRelations matching the given criteria.
   * 
   * @param steps - maximum amount of elements in the result collection
   * @param rangeStart - the relative start position of the text to be checked
   * @param rangeEnd - the relative end position of the text to be checked
   * @param eIds - the ids of the entities for which relations should be found, as a 
   *                    comma-separated String
   * @param type - the type of the entities
   * @return the response including the relations
   */
  @SuppressWarnings("unchecked")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RelationsResponse getRelations(@QueryParam("steps") int steps,
                                        @QueryParam("rangeStart") double rangeStart,
                                        @QueryParam("rangeEnd") double rangeEnd,
                                        @QueryParam("entityIds") String eIds,
                                        @QueryParam("type") String type) {
    if (steps <= 0) {
      throw new WebApplicationException("Illegal amount of steps!");
    }
    
    if (!isValidRangeValue(rangeStart) || !isValidRangeValue(rangeEnd)) {
      throw new WebApplicationException("Illegal range!");
    }
    
    if (eIds == null || "".equals(eIds)) {
      throw new WebApplicationException("No entities specified!");
    }
    
    List<String> entityIds = Arrays.asList(eIds.split(","));
    
    switch (type) {
      case "person":
        break;
        
      case "place":
        break;
        
      case "all":
        break;
        
      default:
        throw new WebApplicationException("Unknown type, must be 'person', 'place' or 'all'!");
    }
    
    Query query = em.createNamedQuery("EntityRelation.findRelationsForEntities");
    query.setParameter("entityIds", entityIds);
    query.setMaxResults(steps);
    List<EntityRelation<Entity>> relations = query.getResultList();
    
    return new RelationsResponse(relations);
  }
  
  /**
   * Returns whether given number is a valid range value or not.
   * 
   * @param value - the value to be checked
   * @return true if given value is a valid range value, false otherwise
   */
  private boolean isValidRangeValue(double value) {
    return value >= 0.0 && value <= 1.0;
  }

}
