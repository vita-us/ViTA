package de.unistuttgart.vis.vita.model.dao;

import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

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
                query = "DELETE "
                        + "FROM Entity e "
                        + "WHERE e.id = :entityId")})
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

  /**
   * Returns a the sub list of given entities occurring in a given range.
   *
   * @param startOffset - the start offset of the range
   * @param endOffset - the end offset of the range
   * @param entities - the list of entities to search for
   * @param type - the type of entities to be returned
   * @return sub list of occurring entities
   */
  @SuppressWarnings("unchecked")
  public List<Entity> getOccurringEntities(int startOffset,
      int endOffset,
      List<?> entities,
      EntityType type) {
    if (entities.isEmpty()) {
      // combination of mysql and hibernate does not like empty WHERE IN statements
      return new ArrayList<>();
    }

    Query query;

    switch (type) {
      case PERSON:
        query = em.createNamedQuery("Occurrence.getOccurringPersons");
        break;
      case PLACE:
        query = em.createNamedQuery("Occurrence.getOccurringPlaces");
        break;
      default:
        throw new IllegalArgumentException("Unknown type of entity");
    }

    query.setParameter(ENTITIES_PARAMETER, entities);
    query.setParameter(RANGE_START_PARAMETER, startOffset);
    query.setParameter(RANGE_END_PARAMETER, endOffset);

    return (List<Entity>) query.getResultList();
  }

  /**
   * Deletes an Entity with the given id from the database.
   *
   * @param entityId - the id of the entity to be deleted
   */
  public void deleteEntityById(String entityId){
    try {
      Entity entity = findById(entityId);
      remove(entity);
    } catch (NoResultException e) {
      // nothing to do
    }
  }

}
