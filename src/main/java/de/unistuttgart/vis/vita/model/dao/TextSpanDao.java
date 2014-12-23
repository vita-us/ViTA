package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * Represents a data access object for accessing TextSpans.
 */
public class TextSpanDao extends JpaDao<TextSpan, String> {

  /**
   * Creates a new data access object for TextSpans.
   */
  public TextSpanDao() {
    super(TextSpan.class);
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
    entityQuery.setParameter("entityId", entityId);
    entityQuery.setParameter("rangeStart", rangeStart);
    entityQuery.setParameter("rangeEnd", rangeEnd);
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
  public int getNumberOfTextSpansForEntity(String entityId, int rangeStart, int rangeEnd) {
    return performNamedCountQuery("TextSpan.getNumberOfTextSpansForEntity",
                              entityId,
                              rangeStart, 
                              rangeEnd);
  }
  
  /**
   * Finds all TextSpans for a given Attribute in an also given document range.
   * 
   * @param attrId - the id of the Attribute which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return List of TextSpans for the given Attribute in the also given document range
   */
  public List<TextSpan> findTextSpansForAttribute(String attrId, int rangeStart, int rangeEnd) {
    TypedQuery<TextSpan> attributeQuery = em.createNamedQuery("TextSpan.findTextSpansForAttribute",
                                                              TextSpan.class);
    attributeQuery.setParameter("attributeId", attrId);
    attributeQuery.setParameter("rangeStart", rangeStart);
    attributeQuery.setParameter("rangeEnd", rangeEnd);
    return attributeQuery.getResultList();
  }
  
  /**
   * Returns the number of TextSpans for a given Attribute in an also given document range.
   * 
   * @param attrId - the id of the attribute which TextSpans should be found
   * @param rangeStart - the start of the document range to search in
   * @param rangeEnd - the end of the document range to search in
   * @return the number of TextSpans for the given Attribute in the also given document range
   */
  public int getNumberOfTextSpansForAttribute(String attrId, int rangeStart, int rangeEnd) {
    return performNamedCountQuery("TextSpan.getNumberOfTextSpansForAttribute", 
                              attrId, 
                              rangeStart, 
                              rangeEnd);
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
    entitiesQuery.setParameter("entityIds", eIds);
    entitiesQuery.setParameter("rangeStart", rangeStart);
    entitiesQuery.setParameter("rangeEnd", rangeEnd);
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
  public int getNumberOfTextSpansForEntities(List<String> eIds, int rangeStart, int rangeEnd) {
    return performNamedCountQuery("TextSpan.getNumberOfOccurringEntities", 
                                  eIds, 
                                  rangeStart, 
                                  rangeEnd);
  }

}
