package de.unistuttgart.vis.vita.services;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

/**
 * Provides method returning EntityRelations requested using GET.
 */
public class EntityRelationsService {
  
  private String documentId;

  /**
   * Sets the id of the Document this resource should refer to
   * 
   * @param docId - the id of the Document which this RelationsService should refer to
   */
  public EntityRelationsService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }
  
  /**
   * Returns the EntityRelations matching the given criteria.
   * 
   * @param steps - maximum amount of elements in the result collection
   * @param rangeStart - the relative start position of the text to be checked
   * @param rangeEnd - the relative end position of the text to be checked
   * @param entityIds - the ids of the entities for which relations should be found, as a 
   *                    comma-separated String
   * @param type - the type of the entities
   * @return the response including the relations
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RelationsResponse getRelations(@QueryParam("steps") int steps,
                                        @QueryParam("rangeStart") double rangeStart,
                                        @QueryParam("rangeEnd") double rangeEnd,
                                        @QueryParam("entityIds") String entityIds,
                                        @QueryParam("type") String type) {
    
    // TODO not implemented yet!
    
    return null;
  } 

}
