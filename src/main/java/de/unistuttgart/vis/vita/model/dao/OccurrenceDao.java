package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;

/**
 * Represents a data access object for accessing Occurrences.
 */
@MappedSuperclass
@NamedQueries({
    // for returning the exact occurrences for an entity in a given range
    @NamedQuery(name = "Occurrence.findOccurrencesForEntity", query = "SELECT occ "
        + "FROM Occurrence occ, Entity e "
        + "WHERE e.id = :entityId "
        + "AND occ MEMBER OF e.occurrences "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "

        // right ordering
        + "ORDER BY occ.range.start.offset"),

    // for checking the amount of occurrences for an entity in a given range
    @NamedQuery(name = "Occurrence.getNumberOfOccurrencesForEntity", query = "SELECT COUNT(occ) "
        + "FROM Occurrence occ, Entity e "
        + "WHERE e.id = :entityId "
        + "AND occ MEMBER OF e.occurrences "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "),

    // for returning the exact occurrences for an attribute in a given range
    @NamedQuery(name = "Occurrence.findOccurrencesForAttribute", query = "SELECT occ "
        + "FROM Occurrence occ, Entity e, Attribute a " + "WHERE e.id = :entityId "
        + "AND a MEMBER OF e.attributes "
        + "AND a.id = :attributeId "
        + "AND occ MEMBER OF a.occurrences "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "
        // right ordering
        + "ORDER BY occ.range.start.offset"),

    // for checking the amount of occurrences for an attribute in a given range
    @NamedQuery(name = "Occurrence.getNumberOfOccurrencesForAttribute", query = "SELECT COUNT(occ) "
        + "FROM Occurrence occ, Entity e, Attribute a "
        + "WHERE e.id = :entityId "
        + "AND a MEMBER OF e.attributes "
        + "AND a.id = :attributeId "
        + "AND occ MEMBER OF a.occurrences "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "),

    // gets the occurrences of all entities
    @NamedQuery(name = "Occurrence.findOccurrencesForEntities", query = "SELECT occ "
        + "FROM Occurrence occ, Entity e "
        + "WHERE e.id IN :entityIds "
        + "AND occ MEMBER OF e.occurrences "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "
        // right ordering
        + "ORDER BY occ.range.start.offset"),

    // checks whether a set of entities occur in a range (for relation
    // occurrences)
    @NamedQuery(name = "Occurrence.getNumberOfOccurringEntities", query = "SELECT COUNT(DISTINCT e.id) "
        + "FROM Entity e "
        + "INNER JOIN e.occurrences occ "
        + "WHERE e.id IN :entityIds "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "),

    // gets the entities that occur within a given range
    @NamedQuery(name = "Occurrence.getOccurringPersons", query = "SELECT DISTINCT e "
        + "FROM Person e "
        + "INNER JOIN e.occurrences occ "
        + "WHERE e IN :entities "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "
        + "GROUP BY e "
        + "HAVING COUNT(occ) > 0.25 * ("
        + "SELECT COUNT(occ2) "
        + "FROM Person p "
        + "INNER JOIN p.occurrences occ2 "
        + "WHERE occ2.sentence.range.start.offset >= :rangeStart AND occ2.sentence.range.start.offset < :rangeEnd "
        + ") / ("
        + "SELECT COUNT(DISTINCT p) "
        + "FROM Person p "
        + "INNER JOIN p.occurrences occ2 "
        + "WHERE occ2.sentence.range.start.offset >= :rangeStart AND occ2.sentence.range.start.offset < :rangeEnd "
        + ")"),

    @NamedQuery(name = "Occurrence.getOccurringPlaces", query = "SELECT DISTINCT e "
        + "FROM Place e "
        + "INNER JOIN e.occurrences occ "
        + "WHERE e IN :entities "
        // range checks
        + "AND occ.sentence.range.start.offset >= :rangeStart AND occ.sentence.range.start.offset < :rangeEnd "
        + "GROUP BY e "
        + "HAVING COUNT(occ) > 0.25 * ("
        + "SELECT COUNT(occ2) "
        + "FROM Place p "
        + "INNER JOIN p.occurrences occ2 "
        + "WHERE occ2.sentence.range.start.offset >= :rangeStart AND occ2.sentence.range.start.offset < :rangeEnd "
        + ") / ("
        + "SELECT COUNT(DISTINCT p) "
        + "FROM Place p "
        + "INNER JOIN p.occurrences occ2 "
        + "WHERE occ2.sentence.range.start.offset >= :rangeStart AND occ2.sentence.range.start.offset < :rangeEnd "
        + ")"),

    @NamedQuery(name = "Occurrence.findOccurrenceById", query = "SELECT occ "
        + "FROM Occurrence occ " + "WHERE occ.id = :occurrenceId"),

    @NamedQuery(name = "Occurrence.getSentencesForAllEntities", query =
        "SELECT sentence "
        + "FROM Sentence sentence "
        + "GROUP BY sentence "
        + "HAVING (SELECT COUNT(DISTINCT e) "
        + "FROM Entity e INNER JOIN e.occurrences occ2 WHERE occ2.sentence = sentence AND e.id IN (:entityIds)) = :entityCount "
        // range checks
        + "AND sentence.range.start.offset >= :rangeStart AND sentence.range.start.offset < :rangeEnd "
        + "ORDER BY sentence.index ASC")})
public class OccurrenceDao extends JpaDao<Occurrence, String> {

  private static final String ENTITY_ID_PARAMETER    = "entityId";
  private static final String ENTITY_IDS_PARAMETER   = "entityIds";
  private static final String ATTRIBUTE_ID_PARAMETER = "attributeId";
  private static final String RANGE_END_PARAMETER    = "rangeEnd";
  private static final String RANGE_START_PARAMETER  = "rangeStart";
  private static final String ENTITY_COUNT_PARAMETER = "entityCount";

  /**
   * Creates a new data access object for Occurrences using the given
   * {@link EntityManager}.
   * 
   * @param em
   *          - the EntityManager to be used in the new OccurrenceDao
   */
  public OccurrenceDao(EntityManager em) {
    super(Occurrence.class, em);
  }

  /**
   * Finds all Occurrences for an Entity with the given id in an also given
   * document range.
   * 
   * @param entityId
   *          - the id of the entity which Occurrences should be found
   * @param rangeStart
   *          - the start of the document range to search in
   * @param rangeEnd
   *          - the end of the document range to search in
   * @return List of Occurrences for the given Entity in the also given document
   *         range
   */
  public List<Occurrence> findOccurrencesForEntity(String entityId, int rangeStart, int rangeEnd) {
    TypedQuery<Occurrence> entityQuery = em.createNamedQuery("Occurrence.findOccurrencesForEntity",
        Occurrence.class);
    entityQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    entityQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    entityQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return entityQuery.getResultList();
  }

  /**
   * Returns the number of Occurrences for a given Entity in an also given
   * document range.
   * 
   * @param entityId
   *          - the id of the entity which Occurrences should be found
   * @param rangeStart
   *          - the start of the document range to search in
   * @param rangeEnd
   *          - the end of the document range to search in
   * @return the number of Occurrences for the given Entity in the also given
   *         document range
   */
  public long getNumberOfOccurrencesForEntity(String entityId, int rangeStart, int rangeEnd) {
    Query numberQuery = em.createNamedQuery("Occurrence.getNumberOfOccurrencesForEntity");
    numberQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    numberQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    numberQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return (long) numberQuery.getSingleResult();
  }

  /**
   * Finds all Occurrences for a given Attribute in an also given document range.
   * 
   * @param attrId
   *          - the id of the Attribute which Occurrences should be found
   * @param rangeStart
   *          - the start of the document range to search in
   * @param rangeEnd
   *          - the end of the document range to search in
   * @return List of Occurrences for the given Attribute in the also given
   *         document range
   */
  public List<Occurrence> findOccurrencesForAttribute(String entityId, String attrId,
      int rangeStart, int rangeEnd) {
    TypedQuery<Occurrence> attributeQuery = em.createNamedQuery(
        "Occurrence.findOccurrencesForAttribute", Occurrence.class);
    attributeQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    attributeQuery.setParameter(ATTRIBUTE_ID_PARAMETER, attrId);
    attributeQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    attributeQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return attributeQuery.getResultList();
  }

  /**
   * Returns the number of Occurrences for a given Attribute in an also given
   * document range.
   * 
   * @param entityId
   *          - the id of the entity with this attribute
   * @param attrId
   *          - the id of the attribute which Occurrences should be found
   * @param rangeStart
   *          - the start of the document range to search in
   * @param rangeEnd
   *          - the end of the document range to search in
   * @return the number of Occurrences for the given Attribute in the also given
   *         document range
   */
  public long getNumberOfOccurrencesForAttribute(String entityId, String attrId, int rangeStart,
      int rangeEnd) {
    Query numberQuery = em.createNamedQuery("Occurrence.getNumberOfOccurrencesForAttribute");
    numberQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    numberQuery.setParameter(ATTRIBUTE_ID_PARAMETER, attrId);
    numberQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    numberQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return (long) numberQuery.getSingleResult();
  }

  /**
   * Finds all Occurrences for a given list of Entities in an also given document
   * range.
   * 
   * @param eIds
   *          - the ids of entities which Occurrences should be found
   * @param rangeStart
   *          - the start of the document range to search in
   * @param rangeEnd
   *          - the end of the document range to search in
   * @return List of Occurrences for the given entities in the also given document
   *         range
   */
  public List<Occurrence> findOccurrencesForEntities(List<String> eIds, int rangeStart, int rangeEnd) {
    TypedQuery<Occurrence> entitiesQuery = em.createNamedQuery(
        "Occurrence.findOccurrencesForEntities", Occurrence.class);
    entitiesQuery.setParameter(ENTITY_IDS_PARAMETER, eIds);
    entitiesQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    entitiesQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return entitiesQuery.getResultList();
  }

  /**
   * Returns the number of Occurrences for a given list of entities in an also
   * given document range.
   * 
   * @param eIds
   *          - the ids of entities which Occurrences should be found
   * @param rangeStart
   *          - the start of the document range to search in
   * @param rangeEnd
   *          - the end of the document range to search in
   * @return the number of Occurrences for the given list of entities in the also
   *         given document range
   */
  public long getNumberOfOccurrencesForEntities(List<String> eIds, int rangeStart, int rangeEnd) {
    Query numberQuery = em.createNamedQuery("Occurrence.getNumberOfOccurringEntities");
    numberQuery.setParameter(ENTITY_IDS_PARAMETER, eIds);
    numberQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    numberQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return (long) numberQuery.getSingleResult();
  }

  public List<Sentence> getSentencesForAllEntities(List<String> eIds, int rangeStart, int rangeEnd) {
    TypedQuery<Sentence> query = em.createNamedQuery("Occurrence.getSentencesForAllEntities",
        Sentence.class);
    query.setParameter(ENTITY_IDS_PARAMETER, eIds);
    query.setParameter(RANGE_START_PARAMETER, rangeStart);
    query.setParameter(RANGE_END_PARAMETER, rangeEnd);
    query.setParameter(ENTITY_COUNT_PARAMETER, (long)eIds.size());
    return query.getResultList();
  }

}
