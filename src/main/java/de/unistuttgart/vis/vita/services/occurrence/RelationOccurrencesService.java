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
import de.unistuttgart.vis.vita.services.entity.EntityRelationsUtil;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Service providing a method to get the occurrences for relations in the current document via GET.
 */
@ManagedBean
public class RelationOccurrencesService extends OccurrencesService {
  private List<String> entityIds;

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
  public OccurrencesResponse getOccurrences(@QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart, 
                                            @QueryParam("rangeEnd") double rangeEnd,
                                            @QueryParam("entityIds") String eIds) {
    
    // first check steps
    if (steps <= 0) {
      throw new WebApplicationException(new IllegalArgumentException("Illegal amount of steps!"), 500);
    }
        
    // then check range and calculate offsets
    int startOffset, endOffset;
    try {
      startOffset = getStartOffset(rangeStart);
      endOffset = getEndOffset(rangeEnd);
    } catch(IllegalRangeException ire) {
      throw new WebApplicationException(ire);
    }
    
    entityIds = EntityRelationsUtil.convertIdStringToList(eIds);
    
    // get occurrences in the given granularity
    List<Occurrence> occs = getGranularEntityOccurrences(steps, startOffset, endOffset);
    
    // create response and return it
    return new OccurrencesResponse(occs);
    
  }

  private List<TextSpan> readTextSpansFromDatabase(int startOffset, int endOffset) {
    TypedQuery<TextSpan> query =
        em.createNamedQuery("TextSpan.findTextSpansForRelations", TextSpan.class);
    query.setParameter("entityIds", entityIds);
    query.setParameter("rangeStart", startOffset);
    query.setParameter("rangeEnd", endOffset);
    return query.getResultList();
  }

  @Override
  protected int getNumberOfSpansInStep(int stepStart, int stepEnd) {
    return readTextSpansFromDatabase(stepStart, stepEnd).size();
  }

}
