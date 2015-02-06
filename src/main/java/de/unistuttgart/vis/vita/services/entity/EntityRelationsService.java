package de.unistuttgart.vis.vita.services.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.dao.EntityRelationDao;
import de.unistuttgart.vis.vita.model.dao.OccurrenceDao;
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

  private OccurrenceDao occurrenceDao;

  private EntityRelationDao entityRelationDao;

  @Inject
  private RelationOccurrencesService relationOccurrencesService;

  @Override
  public void postConstruct() {
    super.postConstruct();
    occurrenceDao = getDaoFactory().getOccurrenceDao();
    entityRelationDao = getDaoFactory().getEntityRelationDao();
  }

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
   * @param eIds - the ids of the entities for which relations should be found, as a comma-separated
   *        String
   * @param type - the type of the entities
   * @return the response including the relations
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RelationsResponse getRelations(
      @QueryParam("rangeStart") @DefaultValue("0") double rangeStart,
      @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd,
      @QueryParam("entityIds") String eIds, @QueryParam("type") String type) {
    // initialize lists
    List<String> occurringEntityIds;
    List<EntityRelation> relations = new ArrayList<>();

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
      occurringEntityIds = getOccurringEntitiesInRange(startOffset, endOffset, eIds);
      // check whether there are entities left
      if (!occurringEntityIds.isEmpty()) {
        relations = getRelationsFromDatabase(occurringEntityIds, type);
      }
    }

    // create the response and return it
    return new RelationsResponse(occurringEntityIds, createConfiguration(relations, rangeStart,
        rangeEnd));
  }

  /**
   * Get relations from database how they are read depends on 'type'
   * 
   * @param idsOfEntities - The IDs of the entities to search for.
   * @param type - the type of the entities (for example: person).
   * @return all found relations with the given type.
   */
  private List<EntityRelation> getRelationsFromDatabase(List<String> idsOfEntities, String type){
    List<EntityRelation> relations;
    switch (type.toLowerCase()) {
      case "person":
        relations = readRelationsFromDatabase(idsOfEntities, Person.class.getSimpleName());
        break;
      case "place":
        relations = readRelationsFromDatabase(idsOfEntities, Place.class.getSimpleName());
        break;
      case "all":
        relations = readRelationsFromDatabase(idsOfEntities);
        break;
      default:
        throw new BadRequestException("Unknown type, must be 'person', 'place' or 'all'!");
    }
    return relations;
  }
  
  /**
   * Check which of these entities are in the given range.
   * 
   * @param startOffset - The global start of the Range.
   * @param endOffset - The global end of the Range.
   * @param eIds - the IDs of the entities for which relations should be found, as a comma-separated
   *        String
   * @return All entities which are in the given range.
   */
  private List<String> getOccurringEntitiesInRange(int startOffset, int endOffset, String eIds) {
    List<String> occurringEntityIds = new ArrayList<>();
    List<String> entityIds = EntityRelationsUtil.convertIdStringToList(eIds);

    for (String entityId : entityIds) {
      if (occurrsInRange(entityId, startOffset, endOffset)) {
        occurringEntityIds.add(entityId);
      }
    }
    return occurringEntityIds;
  }

  /**
   * Checks whether the given Entity has an Occurrence in the given Range.
   * 
   * @param entityId - The id of the entity.
   * @param startOffset - The global start of the Range.
   * @param endOffset - The global end of the Range.
   * @return true: there is at least one Occurrence. false: there is no Occurrence.
   */
  private boolean occurrsInRange(String entityId, int startOffset, int endOffset) {
    return (long) occurrenceDao.getNumberOfOccurrencesForEntity(entityId, startOffset, endOffset) > 0;
  }

  /**
   * Reads EntityRelations with given ids from the database.
   * 
   * @param steps - the maximum amount of EntityRelations being returned
   * @param ids - the list of entity id to be searched for
   * @return list of EntityRelations matching the given criteria
   */
  private List<EntityRelation> readRelationsFromDatabase(List<String> ids) {
    return entityRelationDao.findRelationsForEntities(ids);
  }

  /**
   * Reads EntityRelations with given ids and type from the database.
   * 
   * @param steps - the maximum amount of EntityRelations being returned
   * @param ids - the list of entity id to be searched for
   * @param type - the type of the related entities
   * @return list of EntityRelations matching the given criteria
   */
  private List<EntityRelation> readRelationsFromDatabase(List<String> ids, String type) {
    return entityRelationDao.findRelationsForEntitiesAndType(ids, type);
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
