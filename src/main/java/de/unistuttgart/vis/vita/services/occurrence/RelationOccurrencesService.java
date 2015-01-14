package de.unistuttgart.vis.vita.services.occurrence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.services.entity.EntityRelationsUtil;
import de.unistuttgart.vis.vita.services.responses.occurrence.FlatOccurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * /** Service providing a method to get the occurrences for relations in the current document via
 * GET.
 */
@ManagedBean
public class RelationOccurrencesService extends OccurrencesService {

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
    // first check amount of steps
    if (steps < 0 || steps > 1000) {
      throw new WebApplicationException(new IllegalArgumentException("Illegal amount of steps!"),
          500);
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
    } catch (IllegalRangeException ire) {
      throw new WebApplicationException(ire);
    }

    entityIds = EntityRelationsUtil.convertIdStringToList(eIds);

    if (entityIds.size() == 1) {
      // If there is only one entityId, that can be better handled by the entity occurrence service
      return entityOccurrenceService.setDocumentId(documentId).setEntityId(entityIds.get(0))
          .getOccurrences(steps, rangeStart, rangeEnd);
    }

    List<FlatOccurrence> occs = null;
    if (steps == 0) {
      occs = getExactEntityOccurrences(startOffset, endOffset);
    } else {
      occs = getGranularEntityOccurrences(steps, startOffset, endOffset);
    }

    // create response and return it
    return new OccurrencesResponse(occs);
  }

  private List<FlatOccurrence> getExactEntityOccurrences(int startOffset, int endOffset) {
    List<List<Range>> spanLists = new ArrayList<>();
    for (String entityId : entityIds) {

      List<TextSpan> spans = textSpanDao.findTextSpansForEntity(entityId, startOffset, endOffset);
      List<TextSpan> newSpans = new ArrayList<>();
      for (TextSpan span : spans) {
        newSpans.add(span.widen(HIGHLIGHT_LENGTH / 2));
      }
      spanLists.add(Range.normalizeOverlaps(newSpans));
    }

    List<Range> intersectSpans = Range.intersect(spanLists);

    // convert TextSpans into Occurrences and return them
    return convertSpansToOccurrences(intersectSpans);
  }

  @Override
  protected long getNumberOfSpansInStep(int stepStart, int stepEnd) {
    return textSpanDao.getNumberOfTextSpansForEntities(entityIds, stepStart, stepEnd);
  }

}
