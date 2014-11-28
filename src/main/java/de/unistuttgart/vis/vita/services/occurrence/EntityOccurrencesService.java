package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.DefaultValue;
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
   * @param steps - amount of steps the given range should be divided into (default value 0 means 
   * exact)
   * @param rangeStart - start of range to be searched in
   * @param rangeEnd - end of range to be searched in
   * @return an OccurenceResponse holding all found Occurrences
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@DefaultValue("0") @QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart,
                                            @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd) {
    // first check amount of steps
    if (steps < 0 || steps > 1000) {
      throw new WebApplicationException(new IllegalArgumentException("Illegal amount of steps!"), 500);
    }
    
    // check range
    if (rangeEnd < rangeStart) {
      throw new WebApplicationException("Illegal range!");
    }
       
    int startOffset;
    int endOffset;
    
    // calculate offsets
    try {
      startOffset = getStartOffset(rangeStart);
      endOffset = getEndOffset(rangeEnd);
    } catch(IllegalRangeException ire) {
      throw new WebApplicationException(ire);
    }
    
    List<Occurrence> occs = null;
    if (steps == 0) {
      occs = getExactEntityOccurrences(startOffset, endOffset);
    } else {
      occs = getGranularEntityOccurrences(steps, startOffset, endOffset);
    }
    
    // put occurrences into a response and send it
    return new OccurrencesResponse(occs);
  }

  private List<Occurrence> getExactEntityOccurrences(int startOffset, int endOffset) {
    // fetch the data
    List<TextSpan> readTextSpans = readTextSpansFromDatabase(startOffset, endOffset);
    
    // convert TextSpans into Occurrences and return them
    return convertSpansToOccurrences(readTextSpans);
  }

  private List<TextSpan> readTextSpansFromDatabase(int startOffset, int endOffset) {
    TypedQuery<TextSpan> query = em.createNamedQuery("TextSpan.findTextSpansForEntity", 
                                                      TextSpan.class);
    query.setParameter("entityId", entityId);
    query.setParameter("rangeStart", startOffset);
    query.setParameter("rangeEnd", endOffset);
    return query.getResultList();
  }
  
  private long getNumberOfSpansFromDatabase(int startOffset, int endOffset) {
    Query numberOfTextSpansQuery = em.createNamedQuery("TextSpan.getNumberOfTextSpansForEntity");
    numberOfTextSpansQuery.setParameter("entityId", entityId);
    numberOfTextSpansQuery.setParameter("rangeStart", startOffset);
    numberOfTextSpansQuery.setParameter("rangeEnd", endOffset);
    return (long) numberOfTextSpansQuery.getSingleResult();
  }

  @Override
  protected long getNumberOfSpansInStep(int stepStart, int stepEnd) {
    return getNumberOfSpansFromDatabase(stepStart, stepEnd);
  }

}
