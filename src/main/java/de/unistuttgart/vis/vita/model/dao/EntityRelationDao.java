package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.EntityRelation;

/**
 * Represents a data access object for accessing EntityRelations.
 */
@MappedSuperclass
@NamedQueries({
    @NamedQuery(name = "EntityRelation.findAllEntityRelations",
        query = "SELECT er "
                + "FROM EntityRelation er"),

    @NamedQuery(name = "EntityRelation.findRelationsForEntities",
        query = "SELECT er "
                + "FROM Entity e JOIN e.entityRelations er "
                + "WHERE e.id IN :entityIds "
                // only one relation per pair
                + "AND er.relatedEntity.id < e.id"),

    @NamedQuery(name = "EntityRelation.findRelationsForEntitiesAndType",
        query = "SELECT er "
                + "FROM Entity e JOIN e.entityRelations er "
                + "WHERE e.id IN :entityIds "
                + "AND er.relatedEntity.id IN :entityIds "
                + "AND er.relatedEntity.class = :type "
                // only one relation per pair
                + "AND er.relatedEntity.id < e.id"),

    @NamedQuery(name = "EntityRelation.findEntityRelationById",
        query = "SELECT er "
                + "FROM EntityRelation er "
                + "WHERE er.id = :entityRelationId"),

    @NamedQuery(name = "EntityRelation.deleteEntityRelations",
        query = "DELETE FROM EntityRelation er "
                + "WHERE er.originEntity.id = :entityId "
                + "OR er.relatedEntity.id = :entityId")})
public class EntityRelationDao extends JpaDao<EntityRelation, String> {

  private static final String ENTITY_IDS_PARAMETER = "entityIds";
  private static final String TYPE_PARAMETER = "type";

  /**
   * Creates a new data access object for accessing EntityRelations using the given
   * {@link EntityManager}.
   * 
   * @param em - the EntityManager to be used in the new EntityRelationDao
   */
  public EntityRelationDao(EntityManager em) {
    super(EntityRelation.class, em);
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
    entityQuery.setParameter(ENTITY_IDS_PARAMETER, entityIds);
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
        em.createNamedQuery("EntityRelation.findRelationsForEntitiesAndType", EntityRelation.class);
    relationTypeQuery.setParameter(ENTITY_IDS_PARAMETER, entityIds);
    relationTypeQuery.setParameter(TYPE_PARAMETER, type);
    return relationTypeQuery.getResultList();
  }

  /**
   * Deletes the EntityRelations for the entity with the given id.
   *
   * @param entityId - the Entity which relations should be deleted
   */
  public void deleteEntityRelations(String entityId) {
    Query relationTypeQuery = em.createNamedQuery("EntityRelation.deleteEntityRelations");
    relationTypeQuery.setParameter("entityId", entityId).executeUpdate();
  }

}
