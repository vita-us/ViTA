package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Provides a method to GET the occurrences of the current entity.
 */
public class EntityOccurrencesService extends OccurrencesService {
  
  private String entityId;
  
  /**
   * Creates new EntityOccurrencesService and injects Model.
   * 
   * @param model - the injected Model
   */
  @Inject
  public EntityOccurrencesService(Model model) {
    em = model.getEntityManager();
  }

  /**
   * Sets the id of the document in which the current entity occurs in.
   * 
   * @param docId - the id of the Document in which the current entity occurs in
   * @return this AttributeOccurrencesService
   */
  public EntityOccurrencesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
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
    // TODO make EntityOccurrencesService also consider the given range!
    
    // fetch the data
    List<TextSpan> readTextSpans = readTextSpansFromDatabase(steps);
    
    // convert TextSpans into Occurrences
    List<Occurrence> occurrences = covertSpansToOccurrences(readTextSpans);
    
    // put occurrences into a response and send it
    return new OccurrencesResponse(occurrences);
  }

  private List<TextSpan> readTextSpansFromDatabase(int steps) {
    TypedQuery<TextSpan> query = em.createNamedQuery("TextSpan.findTextSpansForEntity", 
                                                      TextSpan.class);
    query.setParameter("entityId", entityId);
    query.setMaxResults(steps);
    return query.getResultList();
  }

}
