package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.entity.EntityRelationsUtil;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Service providing a method to get the occurrences for relations in the current document via GET.
 */
public class RelationOccurrencesService extends OccurrencesService {

  /**
   * Creates new RelationOccurrencesService and injects Model.
   * 
   * @param model - the injected Model
   */
  @Inject
  public RelationOccurrencesService(Model model) {
    em = model.getEntityManager();
  }

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
                                            @QueryParam("entityIds") String entityIds) {
    // check parameters
    if (steps <= 0) {
      throw new WebApplicationException("Illegal amount of steps!");
    } 
    
    // convert comma separated String into List of ids
    List<String> entityIdList = EntityRelationsUtil.convertIdStringToList(entityIds);
    
    // fetch the data
    List<TextSpan> readTextSpans = readTextSpansFromDatabase(steps, entityIdList);

    // convert TextSpans into Occurrences
    // TODO compute intersections
    List<Occurrence> occurrences = covertSpansToOccurrences(readTextSpans);

    // put occurrences into a response and send it
    return new OccurrencesResponse(occurrences);
  }

  private List<TextSpan> readTextSpansFromDatabase(int steps, List<String> entityIdList) {
    TypedQuery<TextSpan> query =
        em.createNamedQuery("TextSpan.findTextSpansForRelations", TextSpan.class);
    query.setParameter("entityIds", entityIdList);
    query.setMaxResults(steps);
    return query.getResultList();
  }

}
