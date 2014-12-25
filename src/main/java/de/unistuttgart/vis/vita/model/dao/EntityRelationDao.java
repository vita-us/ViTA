package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.EntityRelation;

/**
 * Represents a data access object for accessing EntityRelations.
 */
public class EntityRelationDao extends JpaDao<EntityRelation, String> {

  /**
   * Creates a new data access object for accessing  EntityRelations
   */
  public EntityRelationDao() {
    super(EntityRelation.class);
  }

  /**
   * Finds EntityRelations between entities with the given ids.
   * 
   * @param entityIds - list of entity ids, which relations should be found
   * @return list of EntityRelations found for the given entities
   */
  public List<EntityRelation> findRelationsForEntities(List<String> entityIds) {
    TypedQuery<EntityRelation> entityQuery =
        em.createNamedQuery("EntityRelation.findRelationsForEntities", EntityRelation.class);
    entityQuery.setParameter("entityIds", entityIds);
    return entityQuery.getResultList();
  }

  /**
   * Finds EntityRelations of a given type between entities with the given ids.
   * 
   * @param entityIds - list of entity ids, which relations should be found
   * @param type - the type of relation to be found
   * @return list of EntityRelations of the given type found for the given entities
   */
  public List<EntityRelation> findRelationsForEntitiesAndType(List<String> entityIds, String type) {
    TypedQuery<EntityRelation> relationTypeQuery =
        em.createNamedQuery("EntityRelation.findRelationsForEntityAndType", EntityRelation.class);
    relationTypeQuery.setParameter("entityIds", entityIds);
    relationTypeQuery.setParameter("type", type);
    return relationTypeQuery.getResultList();
  }

}
