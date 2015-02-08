package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Provides a method to GET the occurrences of the current entity.
 */
@ManagedBean
public class EntityOccurrencesService extends ExtendedOccurrencesService {

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
   * Reads occurrences of the specific attribute and entity from database and returns them in JSON.
   * 
   * @param steps - amount of steps, the range should be divided into (default value 0 means exact)
   * @param rangeStart - start of range to be searched in
   * @param rangeEnd - end of range to be searched in
   * @return an OccurenceResponse holding all found Occurrences
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@DefaultValue("0") @QueryParam("steps") int steps,
      @QueryParam("rangeStart") double rangeStart,
      @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd) {
    return getOccurrencesImpl(steps, rangeStart, rangeEnd);
  }
  
  @Override
  protected List<Range> getExactEntityOccurrences(int startOffset, int endOffset) {
    // fetch the data
    List<Occurrence> readOccurrences =
        occurrenceDao.findOccurrencesForEntity(entityId, startOffset, endOffset);
    return Range.mergeOverlappingRanges(convertOccurrencesToRanges(readOccurrences));
  }

  @Override
  protected boolean hasOccurrencesInStep(int firstSentenceIndex, int lastSentenceIndex) {
    return occurrenceDao.getNumberOfOccurrencesForEntity(entityId, firstSentenceIndex,
        lastSentenceIndex) > 0;
  }

}
