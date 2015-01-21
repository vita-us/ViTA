package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityType;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Represents a generic data access object for entities.
 */
@MappedSuperclass
@NamedQueries({
    @NamedQuery(name = "Entity.findEntityById",
            query = "SELECT e "
                  + "FROM Entity e "
                  + "WHERE e.id = :entityId"),
                  
    @NamedQuery(name = "Entity.deleteEntityById",
                query = "DELETE "+ "FROM Entity e "+ "WHERE e.id = :entityId")})
public class EntityDao extends JpaDao<Entity, String> {
  
  private static final String ENTITIES_PARAMETER = "entities";
  
  private static final String RANGE_START_PARAMETER = "rangeStart";
  private static final String RANGE_END_PARAMETER = "rangeEnd";

  /**
   * Creates a new EntityDao with the given {@link EntityManager}.
   * 
   * @param em - the EntityManager to be used in the new EntityDao
   */
  public EntityDao(EntityManager em) {
    super(Entity.class, em);
  }

  /**
   * @return the name of the {@link NamedQuery} for searching in a specific Document
   */
  public String getInDocumentQueryName() {
    String className = getPersistentClassName();
    return className + "." + "find" + className + "InDocument";
  }

  /**
   * Finds all Entities which occur in the document with the given id.
   * 
   * @param docId - the id of the document to search in
   * @param offset - the first place to be returned
   * @param count - the maximum of places to be returned
   * @return list of all Entities occurring in the given document
   */
  public List<Entity> findInDocument(String docId, int offset, int count) {
    TypedQuery<Entity> docQuery = em.createNamedQuery(getInDocumentQueryName(), Entity.class);
    docQuery.setParameter(0, docId);
    return docQuery.getResultList();
  }
  
  /**
   * @return the name of the {@link NamedQuery} for searching a specific name
   */
  public String getByNameQueryName() {
    String className = getPersistentClassName();
    return className + "." + "find" + className + "ByName";
  }

  /**
   * Finds the Entity with the given name.
   * 
   * @param name - the name of the Entity to search for
   * @return the Entity with the given name
   */
  public Entity findByName(String name) {
    TypedQuery<Entity> nameQuery = em.createNamedQuery(getByNameQueryName(), getPersistentClass());
    nameQuery.setParameter(0, name);
    return nameQuery.getSingleResult();
  }
  
  @SuppressWarnings("unchecked")
  public List<Entity> findOccurringPersons(int startOffset, int endOffset, List<Person> entities) {
    Query query = em.createNamedQuery("TextSpan.getOccurringEntities");
    query.setParameter(ENTITIES_PARAMETER, entities);
    query.setParameter(RANGE_START_PARAMETER, startOffset);
    query.setParameter(RANGE_END_PARAMETER, endOffset);
    return (List<Entity>) query.getResultList();
  }

  /**
   * Returns a the sublist of given entities occurring in a given range.
   *
   * @param startOffset - the start offset of the range
   * @param endOffset - the end offset of the range
   * @param entities - the list of entities to search for
   * @param type - the type of entities to be returned
   * @return sublist of occurring entities
   */
  @SuppressWarnings("unchecked")
  public List<Entity> getOccurringEntities(int startOffset,
      int endOffset,
      List<?> entities,
      EntityType type) {
    Query query;

    switch (type) {
      case PERSON:
        query = em.createNamedQuery("TextSpan.getOccurringPersons");
        break;
      case PLACE:
        query = em.createNamedQuery("TextSpan.getOccurringPlaces");
        break;
      default:
        throw new IllegalArgumentException("Unknown type of entity");
    }

    query.setParameter("entities", entities);
    query.setParameter("rangeStart", startOffset);
    query.setParameter("rangeEnd", endOffset);

    return (List<Entity>) query.getResultList();
  }
  
  public void deleteEntityById(String entityId){
    Query query = em.createNamedQuery("Entity.deleteEntityById");
    query.setParameter("entityId", entityId).executeUpdate();
    
  }
}
