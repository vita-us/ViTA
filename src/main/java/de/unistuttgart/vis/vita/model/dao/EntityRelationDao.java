package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.EntityRelation;

/**
 * Represents a data access object for accessing EntityRelations.
 */
@ManagedBean
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "EntityRelation.findAllEntityRelations", 
              query = "SELECT er "
                    + "FROM EntityRelation er"),
                    
  @NamedQuery(name = "EntityRelation.findRelationsForEntities",
              query = "SELECT er "
                    + "FROM Entity e JOIN e.entityRelations er "
                    + "WHERE e.id IN :entityIds "),
      
  @NamedQuery(name = "EntityRelation.findRelationsForEntitiesAndType",
              query = "SELECT er "
                    + "FROM Entity e JOIN e.entityRelations er "
                    + "WHERE e.id IN :entityIds "
                    + "AND er.relatedEntity.id IN :entityIds "
                    + "AND er.relatedEntity.class = :type"),

  @NamedQuery(name = "EntityRelation.findEntityRelationById", 
              query = "SELECT er "
                    + "FROM EntityRelation er " 
                    + "WHERE er.id = :entityRelationId")}
)
public class EntityRelationDao extends JpaDao<EntityRelation, String> {

  private static final String ENTITY_IDS_PARAMETER = "entityIds";
  private static final String TYPE_PARAMETER = "type";

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

}
