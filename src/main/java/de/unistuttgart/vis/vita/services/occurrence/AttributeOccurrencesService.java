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
 * Provides a method to GET the occurrences of the current attribute and entity.
 */
public class AttributeOccurrencesService extends OccurrencesService {
  
  private String attributeId;

  private String entityId;
  
  /**
   * Creates new AttributeOccurrencesService and injects Model.
   * 
   * @param model - the injected Model
   */
  @Inject
  public AttributeOccurrencesService(Model model) {
    em = model.getEntityManager();
  }
  
  /**
   * Sets the id of the document this service refers to and returns this
   * AttributeOccurrencesService.
   * 
   * @param docId - the id of the document in which this service should find occurrences of
   *        attributes
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
   * @param eId - the id of the entity which occurrences should be got
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }
  
  /**
   * Sets the id of the attribute which occurrences should be got and returns this 
   * AttributeOccurrencesService.
   * 
   * @param attrId - the id of the attribute which occurrences should be got
   * @return this AttributeOccurrencesService
   */
  public AttributeOccurrencesService setAttributeId(String attrId) {
    this.attributeId = attrId;
    return this;
  }

  /**
   * Reads occurrences of the specific attribute and entity from database and returns them in JSON.
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
    // gets the data
	List<TextSpan> readTextSpans = readTextSpansFromDatabase(steps);
	
	// convert TextSpans to Occurences
	List<Occurrence> occurences = covertSpansToOccurrences(readTextSpans);
	
    return new OccurrencesResponse(occurences);
  }

private List<TextSpan> readTextSpansFromDatabase(int steps) {
	TypedQuery<TextSpan> query = em.createNamedQuery("TextSpan.findTextSpansForAttribute",
													  TextSpan.class);
	query.setParameter("attributeId", attributeId);
	query.setMaxResults(steps);
	return query.getResultList();
}

}
