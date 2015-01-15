package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * Represents a data access object for accessing TextSpans.
 */
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "TextSpan.findAllTextSpans",
      query = "SELECT ts "
            + "FROM TextSpan ts"),

  // for returning the exact spans for an entity in a given range
  @NamedQuery(name = "TextSpan.findTextSpansForEntity",
    query = "SELECT ts "
          + "FROM TextSpan ts, Entity e "
          + "WHERE e.id = :entityId "
          + "AND ts MEMBER OF e.occurrences "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // right ordering
          + "ORDER BY ts.start.offset"),

  // for checking the amount of spans for an entity in a given range
  @NamedQuery(name = "TextSpan.getNumberOfTextSpansForEntity",
  query = "SELECT COUNT(ts) "
        + "FROM TextSpan ts, Entity e "
        + "WHERE e.id = :entityId "
        + "AND ts MEMBER OF e.occurrences "
        // range checks
        + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
        + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd"),

  // for returning the exact spans for an attribute in a given range
  @NamedQuery(name = "TextSpan.findTextSpansForAttribute",
    query = "SELECT ts "
          + "FROM TextSpan ts, Entity e, Attribute a "
          + "WHERE e.id = :entityId "
          + "AND a MEMBER OF e.attributes "
          + "AND a.id = :attributeId "
          + "AND ts MEMBER OF a.occurrences "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // right ordering
          + "ORDER BY ts.start.offset"),

  // for checking the amount of spans for an attribute in a given range
  @NamedQuery(name = "TextSpan.getNumberOfTextSpansForAttribute",
  query = "SELECT COUNT(ts) "
        + "FROM TextSpan ts, Entity e, Attribute a "
        + "WHERE e.id = :entityId "
        + "AND a MEMBER OF e.attributes "
        + "AND a.id = :attributeId "
        + "AND ts MEMBER OF a.occurrences "
        // range checks
        + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
        + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd"),

  // gets the occurrences of all entities
  @NamedQuery(name = "TextSpan.findTextSpansForEntities",
    query = "SELECT ts "
          + "FROM TextSpan ts, Entity e "
          + "WHERE e.id IN :entityIds "
          + "AND ts MEMBER OF e.occurrences "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // Null checks
          + "AND ts.start.chapter IS NOT NULL "
          // right ordering
          + "ORDER BY ts.start.offset"),

  // checks whether a set of entities occur in a range (for relation occurrences)
  @NamedQuery(name = "TextSpan.getNumberOfOccurringEntities",
    query = "SELECT COUNT(DISTINCT e.id) "
          + "FROM Entity e "
          + "INNER JOIN e.occurrences ts "
          + "WHERE e.id IN :entityIds "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          // Null checks
          + "AND ts.start.chapter IS NOT NULL " + "AND ts.start.chapter IS NOT NULL"),

  // gets the persons that occur within a given range
  @NamedQuery(name = "TextSpan.getOccurringPersons",
    query = "SELECT DISTINCT e "
          + "FROM Entity e "
          + "INNER JOIN e.occurrences ts "
          + "WHERE e IN :entities "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "GROUP BY e "
          + "HAVING COUNT(ts) > 0.25 * ("
          + "SELECT COUNT(ts2) "
          + "FROM Person p "
          + "INNER JOIN p.occurrences ts2 "
          + "WHERE ts2.start.offset BETWEEN :rangeStart AND :rangeEnd"
          + ") / ("
          + "SELECT COUNT(DISTINCT p) "
          + "FROM Person p "
          + "INNER JOIN p.occurrences ts2 "
          + "WHERE ts2.start.offset BETWEEN :rangeStart AND :rangeEnd"
          + ")"),

  // gets the places that occur within a given range
  @NamedQuery(name = "TextSpan.getOccurringPlaces",
    query = "SELECT DISTINCT e "
          + "FROM Entity e "
          + "INNER JOIN e.occurrences ts "
          + "WHERE e IN :entities "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "GROUP BY e "
          + "HAVING COUNT(ts) > 0.25 * ("
          + "SELECT COUNT(ts2) "
          + "FROM Place pl "
          + "INNER JOIN pl.occurrences ts2 "
          + "WHERE ts2.start.offset BETWEEN :rangeStart AND :rangeEnd"
          + ") / ("
          + "SELECT COUNT(DISTINCT pl) "
          + "FROM Place pl "
          + "INNER JOIN pl.occurrences ts2 "
          + "WHERE ts2.start.offset BETWEEN :rangeStart AND :rangeEnd"
          + ")"),

  @NamedQuery(name = "TextSpan.findTextSpanById", query = "SELECT ts "
      + "FROM TextSpan ts "
      + "WHERE ts.id = :textSpanId")
  })
public class TextSpanDao extends JpaDao<TextSpan, String> {

  private static final String ENTITY_ID_PARAMETER = "entityId";
  private static final String ENTITY_IDS_PARAMETER = "entityIds";
  private static final String ATTRIBUTE_ID_PARAMETER = "attributeId";
  private static final String RANGE_END_PARAMETER = "rangeEnd";
  private static final String RANGE_START_PARAMETER = "rangeStart";


  /**
   * Creates a new data access object for TextSpans using the given {@link EntityManager}.
   * 
   * @param em - the EntityManager to be used in the new TextSpanDao
   */
  public TextSpanDao(EntityManager em) {
    super(TextSpan.class, em);
  }

  /**
   * Finds all TextSpans for an Entity with the given id in an also given document range.
   * 
   * @param entityId - the id of the entity which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return List of TextSpans for the given Entity in the also given document range
   */
  public List<TextSpan> findTextSpansForEntity(String entityId, int rangeStart, int rangeEnd) {
    TypedQuery<TextSpan> entityQuery = em.createNamedQuery("TextSpan.findTextSpansForEntity", 
                                                            TextSpan.class);
    entityQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    entityQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    entityQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return entityQuery.getResultList();
  }
  
  /**
   * Returns the number of TextSpans for a given Entity in an also given document range.
   * 
   * @param entityId - the id of the entity which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return the number of TextSpans for the given Entity in the also given document range
   */
  public long getNumberOfTextSpansForEntity(String entityId, int rangeStart, int rangeEnd) {
    Query numberQuery = em.createNamedQuery("TextSpan.getNumberOfTextSpansForEntity");
    numberQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    numberQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    numberQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return (long) numberQuery.getSingleResult();
  }
  
  /**
   * Finds all TextSpans for a given Attribute in an also given document range.
   * 
   * @param attrId - the id of the Attribute which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return List of TextSpans for the given Attribute in the also given document range
   */
  public List<TextSpan> findTextSpansForAttribute(String entityId, String attrId, 
                                                    int rangeStart, int rangeEnd) {
    TypedQuery<TextSpan> attributeQuery = em.createNamedQuery("TextSpan.findTextSpansForAttribute",
                                                              TextSpan.class);
    attributeQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    attributeQuery.setParameter(ATTRIBUTE_ID_PARAMETER, attrId);
    attributeQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    attributeQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return attributeQuery.getResultList();
  }
  
  /**
   * Returns the number of TextSpans for a given Attribute in an also given document range.
   * 
   * @param entityId - the id of the entity with this attribute
   * @param attrId - the id of the attribute which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return the number of TextSpans for the given Attribute in the also given document range
   */
  public long getNumberOfTextSpansForAttribute(String entityId, String attrId, 
                                                int rangeStart, int rangeEnd) {
    Query numberQuery = em.createNamedQuery("TextSpan.getNumberOfTextSpansForAttribute");
    numberQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    numberQuery.setParameter(ATTRIBUTE_ID_PARAMETER, attrId);
    numberQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    numberQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return (long) numberQuery.getSingleResult();
  }
  
  /**
   * Finds all TextSpans for a given list of Entities in an also given document range.
   * 
   * @param eIds - the ids of entities which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return List of TextSpans for the given entities in the also given document range
   */
  public List<TextSpan> findTextSpansForEntities(List<String> eIds, int rangeStart, int rangeEnd) {
    TypedQuery<TextSpan> entitiesQuery = em.createNamedQuery("TextSpan.FindTextSpansForEntities",
                                                              TextSpan.class);
    entitiesQuery.setParameter(ENTITY_IDS_PARAMETER, eIds);
    entitiesQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    entitiesQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return entitiesQuery.getResultList();
  }
  
  /**
   * Returns the number of TextSpans for a given list of entities in an also given document range.
   * 
   * @param eIds - the ids of entities which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return the number of TextSpans for the given list of entities in the also given document 
   *    range
   */
  public long getNumberOfTextSpansForEntities(List<String> eIds, int rangeStart, int rangeEnd) {
    Query numberQuery = em.createNamedQuery("TextSpan.getNumberOfOccurringEntities");
    numberQuery.setParameter(ENTITY_IDS_PARAMETER, eIds);
    numberQuery.setParameter(RANGE_START_PARAMETER, rangeStart);
    numberQuery.setParameter(RANGE_END_PARAMETER, rangeEnd);
    return (long) numberQuery.getSingleResult();
  }

}
