package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.services.entity.EntityRelationsUtil;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * /** Service providing a method to get the occurrences for relations in the current document via
 * GET.
 */
@ManagedBean
public class RelationOccurrencesService extends ExtendedOccurrencesService {

  private List<String> entityIds;

  @Inject
  private EntityOccurrencesService entityOccurrenceService;

  /**
   * The length up to which relation occurrences should be found and highlighted
   */
  public static final int HIGHLIGHT_LENGTH = 200;

  /**
   * Sets the id of the document this service refers to.
   *
   * @param docId - the id of the Document to which this service refers to
   * @return this RelationOccurrencesService
   */
  public RelationOccurrencesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@DefaultValue("0") @QueryParam("steps") int steps,
      @QueryParam("rangeStart") double rangeStart,
      @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd,
      @QueryParam("entityIds") String eIds) {
    checkSteps(steps);
    checkRange(rangeStart, rangeEnd);
    int startOffset = checkStartOffset(rangeStart);
    int endOffset = checkEndOffset(rangeEnd);

    entityIds = EntityRelationsUtil.convertIdStringToList(eIds);

    if (entityIds.size() == 1) {
      // If there is only one entityId, that can be better handled by the entity occurrence service
      return entityOccurrenceService.setDocumentId(documentId).setEntityId(entityIds.get(0))
          .getOccurrences(steps, rangeStart, rangeEnd);
    }

    List<Range> occs = null;
    if (steps == 0) {
      occs = getExactEntityOccurrences(startOffset, endOffset);
    } else {
      occs = getGranularEntityOccurrences(steps, startOffset, endOffset);
    }

    // create response and return it
    return new OccurrencesResponse(occs);
  }
  
  @Override
  protected List<Range> getExactEntityOccurrences(int startOffset, int endOffset) {
    List<Sentence> sentences = occurrenceDao.getSentencesForAllEntities(entityIds, startOffset, endOffset);

    return mergeAdjacentSentences(sentences);
  }

  @Override
  protected boolean hasOccurrencesInStep(int stepStart, int stepEnd) {
    return occurrenceDao.hasSentencesForAllEntities(entityIds, stepStart, stepEnd);
  }

}
