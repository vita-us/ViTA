package de.unistuttgart.vis.vita.services.occurrence;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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

import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.entity.EntityRelationsUtil;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
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
  public OccurrencesResponse getOccurrences(@DefaultValue("0") @QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart, 
                                            @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd,
                                            @QueryParam("entityIds") String eIds) {
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
    
    entityIds = EntityRelationsUtil.convertIdStringToList(eIds);
    
    List<Occurrence> occs = null;
    if (steps == 0) {
      occs = getExactEntityOccurrences(startOffset, endOffset);
    } else {
      occs = getGranularEntityOccurrences(steps, startOffset, endOffset); 
    }

    // create response and return it
    return new OccurrencesResponse(occs);
  }

  private List<Occurrence> getExactEntityOccurrences(int startOffset, int endOffset) {
    // fetch the data
    List<TextSpan> readTextSpans = readTextSpansFromDatabase(startOffset, endOffset);
    
    List<TextSpan> intersectSpans = computeIntersection(readTextSpans);
    
    // convert TextSpans into Occurrences and return them
    return convertSpansToOccurrences(intersectSpans);
  }

  private List<TextSpan> computeIntersection(List<TextSpan> readTextSpans) {
    // if List is empty or there is only one element, there is nothing to do
    if (readTextSpans.size() < 2) {
      return readTextSpans;
    }
 
    // Create an empty stack of intervals
    Deque<TextSpan> s = new ArrayDeque<>();
 
    // push the first interval to stack
    s.push(readTextSpans.get(0));
 
    // Start from the next TextSpan and merge if necessary
    for (int i = 1 ; i < readTextSpans.size(); i++) {
        // get interval from stack top
        TextSpan top = s.peek();
        TextSpan currentSpan = readTextSpans.get(i);
        
        // if current TextSpan is not overlapping with stack top, push it to the stack
        if (!top.overlapsWith(currentSpan)) {
            s.push(readTextSpans.get(i));
        } else {
          TextPosition start = top.getStart();
          TextPosition end = top.getEnd();
          
          TextPosition currentStart = currentSpan.getStart();
          if (top.getStart().getOffset() < currentStart.getOffset() 
              && currentStart.getOffset() <= end.getOffset()) {
            start = currentStart;
          }
          
          if (top.getEnd().getOffset() > currentSpan.getEnd().getOffset()) {
            end = currentSpan.getEnd();
          }
          
          s.pop();
          s.push(new TextSpan(start, end));
        }
    }
    
    List<TextSpan> resultList = new ArrayList<>();
    while (!s.isEmpty()) {
      resultList.add(s.pop());
    }
    return resultList;
  }

  private List<TextSpan> readTextSpansFromDatabase(int startOffset, int endOffset) {
    TypedQuery<TextSpan> query =
        em.createNamedQuery("TextSpan.findTextSpansForRelations", TextSpan.class);
    query.setParameter("entityIds", entityIds);
    query.setParameter("rangeStart", startOffset);
    query.setParameter("rangeEnd", endOffset);
    return query.getResultList();
  }
  
  private long getNumberOfSpansFromDatabase(int startOffset, int endOffset) {
    Query numberOfTextSpansQuery = em.createNamedQuery("TextSpan.getNumberOfTextSpansForRelations");
    numberOfTextSpansQuery.setParameter("entityIds", entityIds);
    numberOfTextSpansQuery.setParameter("rangeStart", startOffset);
    numberOfTextSpansQuery.setParameter("rangeEnd", endOffset);
    return (long) numberOfTextSpansQuery.getSingleResult();
  }

  @Override
  protected long getNumberOfSpansInStep(int stepStart, int stepEnd) {
    return getNumberOfSpansFromDatabase(stepStart, stepEnd);
  }

}
