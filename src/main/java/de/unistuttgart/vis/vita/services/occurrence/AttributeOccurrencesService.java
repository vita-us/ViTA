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

import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.services.responses.occurrence.FlatOccurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Provides a method to GET the occurrences of the current attribute and entity.
 */
@ManagedBean
public class AttributeOccurrencesService extends OccurrencesService {

  private String attributeId;

  private String entityId;

  /**
   * Sets the id of the document this service refers to and returns this
   * AttributeOccurrencesService.
   * 
   * @param docId
   *          - the id of the document in which this service should find
   *          occurrences of attributes
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Sets the id of the entity which occurrences should be got and returns this
   * AttributeOccurrencesService.
   * 
   * @param eId
   *          - the id of the entity which occurrences should be got
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }

  /**
   * Sets the id of the attribute which occurrences should be got and returns
   * this AttributeOccurrencesService.
   * 
   * @param attrId
   *          - the id of the attribute which occurrences should be got
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setAttributeId(String attrId) {
    this.attributeId = attrId;
    return this;
  }

  /**
   * Reads occurrences of the specific attribute and entity from database and
   * returns them in JSON.
   * 
   * @param steps
   *          - amount of steps, the range should be divided into (default value 0 means exact)
   * @param rangeStart
   *          - start of range to be searched in
   * @param rangeEnd
   *          - end of range to be searched in
   * @return an OccurenceResponse holding all found Occurrences
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@DefaultValue("0") @QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart, 
                                            @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd) {
    // check amount of steps
    if (steps < 0 || steps > 1000) {
      throw new WebApplicationException("Illegal amount of steps!");
    }
    
    // check range
    if (rangeEnd < rangeStart) {
      throw new WebApplicationException("Illegal range!");
    }
    
    int startOffset;
    int endOffset;
    
    // calculate offsets for the range
    try {
      startOffset = getStartOffset(rangeStart);
      endOffset = getEndOffset(rangeEnd);
    } catch (IllegalRangeException e) {
      throw new WebApplicationException(e);
    }
    
    List<FlatOccurrence> occs = null;
    if (steps == 0) {
      occs = getExactEntityOccurrences(startOffset, endOffset);
    } else {
      occs = getGranularEntityOccurrences(steps, startOffset, endOffset);
    }

    return new OccurrencesResponse(occs);
  }

  private List<FlatOccurrence> getExactEntityOccurrences(int startOffset, int endOffset) {
    // get the TextSpans
    List<Range> readTextSpans = readTextSpansFromDatabase(startOffset, endOffset);
    
    // convert TextSpans into Occurrences and return them
    return convertSpansToOccurrences(readTextSpans);
  }

  private List<Range> readTextSpansFromDatabase(int startOffset, int endOffset) {
    TypedQuery<Range> query = em.createNamedQuery("TextSpan.findTextSpansForAttribute",
        Range.class);
    query.setParameter("entityId", entityId);
    query.setParameter("attributeId", attributeId);
    query.setParameter("rangeStart", startOffset);
    query.setParameter("rangeEnd", endOffset);
    return query.getResultList();
  }
  
  private long getNumberOfSpansFromDatabase(int startOffset, int endOffset) {
    Query numberOfTextSpansQuery = em.createNamedQuery("TextSpan.getNumberOfTextSpansForAttribute");
    numberOfTextSpansQuery.setParameter("entityId", entityId);
    numberOfTextSpansQuery.setParameter("attributeId", attributeId);
    numberOfTextSpansQuery.setParameter("rangeStart", startOffset);
    numberOfTextSpansQuery.setParameter("rangeEnd", endOffset);
    return (long) numberOfTextSpansQuery.getSingleResult();
  }

  @Override
  protected long getNumberOfSpansInStep(int stepStart, int stepEnd) {
    return getNumberOfSpansFromDatabase(stepStart, stepEnd);
  }

}
