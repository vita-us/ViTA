package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Provides a method to GET the occurrences of the current entity.
 */
@ManagedBean
public class EntityOccurrencesService extends OccurrencesService {
  
  private String entityId;

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
    // check steps
    if (steps <= 0) {
      throw new WebApplicationException("Illegal amount of steps!");
    } 
    
    int startOffset;
    int endOffset;
    
    // compute offsets for the range
    try {
      startOffset = getStartOffset(rangeStart);
      endOffset = getEndOffset(rangeEnd);
    } catch (IllegalRangeException e) {
      throw new WebApplicationException(e);
    }
    
    // fetch the data
    List<TextSpan> readTextSpans = readTextSpansFromDatabase(steps, startOffset, endOffset);
    
    // convert TextSpans into Occurrences
    List<Occurrence> occurrences = covertSpansToOccurrences(readTextSpans);
    
    // put occurrences into a response and send it
    return new OccurrencesResponse(occurrences);
  }

  private List<TextSpan> readTextSpansFromDatabase(int steps, int startOffset, int endOffset) {
    TypedQuery<TextSpan> query = em.createNamedQuery("TextSpan.findTextSpansForEntity", 
                                                      TextSpan.class);
    query.setParameter("entityId", entityId);
    query.setParameter("rangeStart", startOffset);
    query.setParameter("rangeEnd", endOffset);
    query.setMaxResults(steps);
    return query.getResultList();
  }

}
