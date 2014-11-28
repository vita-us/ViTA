package de.unistuttgart.vis.vita.services.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.services.RangeService;
import de.unistuttgart.vis.vita.services.occurrence.IllegalRangeException;
import de.unistuttgart.vis.vita.services.occurrence.RelationOccurrencesService;
import de.unistuttgart.vis.vita.services.responses.RelationConfiguration;
import de.unistuttgart.vis.vita.services.responses.RelationsResponse;

/**
 * Provides methods returning EntityRelations and its occurrences requested using GET.
 */
@ManagedBean
public class EntityRelationsService extends RangeService {
  
  @Inject
  private RelationOccurrencesService relationOccurrencesService;
  
  /**
   * Sets the id of the Document this service should refer to
   * 
   * @param docId - the id of the Document which this EntityRelationsService should refer to
   */
  public EntityRelationsService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Returns the EntityRelations matching the given criteria.
   * 
   * @param steps - maximum amount of elements in the result collection
   * @param rangeStart - the relative start position of the text to be checked
   * @param rangeEnd - the relative end position of the text to be checked
   * @param eIds - the ids of the entities for which relations should be found, as a 
   *                    comma-separated String
   * @param type - the type of the entities
   * @return the response including the relations
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RelationsResponse getRelations(@QueryParam("rangeStart") @DefaultValue("0") double rangeStart,
                                        @QueryParam("rangeEnd")  @DefaultValue("1") double rangeEnd,
                                        @QueryParam("entityIds") String eIds,
                                        @QueryParam("type") String type) {
    // initialize lists
    List<String> entityIds = null;
    List<String> occurringEntityIds = new ArrayList<>();
    List<EntityRelation> relations = null;
    
    // calculate offsets
    int startOffset;
    int endOffset;
    try {
      startOffset = getStartOffset(rangeStart);
      endOffset = getEndOffset(rangeEnd);
    } catch (IllegalRangeException ire) {
      throw new BadRequestException("Illegal Range", ire);
    }
    
    // check entityIds
    if (eIds == null || "".equals(eIds)) {
      throw new BadRequestException("No entities specified!");
    } else {
      // convert entity id string
      entityIds = EntityRelationsUtil.convertIdStringToList(eIds);
      
      // first check whether this entity occurs in the given range
      for (String entityId : entityIds) {
        if (occurrsInRange(entityId, startOffset, endOffset)) {
          occurringEntityIds.add(entityId);
        }
      }
      
      // get relations from database how they are read depends on 'type'
      switch (type.toLowerCase()) {
        case "person":
          relations = readRelationsFromDatabase(occurringEntityIds, Person.class.getSimpleName());
          break;
        case "place":
          relations = readRelationsFromDatabase(occurringEntityIds, Place.class.getSimpleName());
          break;
        case "all":
          relations = readRelationsFromDatabase(occurringEntityIds);
          break;
        default:
          throw new BadRequestException("Unknown type, must be 'person', 'place' or 'all'!");
      }
    }
    
    // create the response and return it
    return new RelationsResponse(occurringEntityIds, createConfiguration(relations, rangeStart, rangeEnd));
  }

  private boolean occurrsInRange(String entityId, int startOffset, int endOffset) {
    Query numberOfTextSpansQuery = em.createNamedQuery("TextSpan.getNumberOfTextSpansForEntity");
    numberOfTextSpansQuery.setParameter("entityId", entityId);
    numberOfTextSpansQuery.setParameter("rangeStart", startOffset);
    numberOfTextSpansQuery.setParameter("rangeEnd", endOffset);
    return ((long) numberOfTextSpansQuery.getSingleResult() > 0);
  }

  /**
   * Reads EntityRelations with given ids from the database.
   * 
   * @param steps - the maximum amount of EntityRelations being returned
   * @param ids - the list of entity id to be searched for
   * @return list of EntityRelations matching the given criteria
   */
  @SuppressWarnings("unchecked")
  private List<EntityRelation> readRelationsFromDatabase(List<String> ids) {
    Query query = em.createNamedQuery("EntityRelation.findRelationsForEntities");
    query.setParameter("entityIds", ids);
    return query.getResultList();
  }
  
  /**
   * Reads EntityRelations with given ids and type from the database.
   * 
   * @param steps - the maximum amount of EntityRelations being returned
   * @param ids - the list of entity id to be searched for
   * @param type - the type of the related entities
   * @return list of EntityRelations matching the given criteria
   */
  @SuppressWarnings("unchecked")
  private List<EntityRelation> readRelationsFromDatabase(List<String> ids, String type) {
    Query query = em.createNamedQuery("EntityRelation.findRelationsForEntitiesAndType");
    query.setParameter("entityIds", ids);
    query.setParameter("type", type);
    return query.getResultList();
  } 

  /**
   * Creates a list of RelationConfigurations by mapping the given EntityRelations to a flat 
   * representation.
   * 
   * @param relations - the EntityRelations to be mapped
   * @return the configurations as a flat representation of the given relations
   */
  private List<RelationConfiguration> createConfiguration(List<EntityRelation> relations,
      double rangeStart, double rangeEnd) {
    List<RelationConfiguration> configurations = new ArrayList<>();
    for (EntityRelation entityRelation : relations) {
      if (entityRelation.getWeightForRange(rangeStart, rangeEnd) > 0) {
        configurations.add(new RelationConfiguration(entityRelation, rangeStart, rangeEnd));
      }
    }
    return configurations;
  }
  
  /**
   * Returns the RelationOccurrencesService for the current document.
   * 
   * @return the RelationOccurrencesService which answers the request
   */
  @Path("/occurrences")
  public RelationOccurrencesService getOccurrences() {
    return relationOccurrencesService.setDocumentId(documentId);
  }

}
