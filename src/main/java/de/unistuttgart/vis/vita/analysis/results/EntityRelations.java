package de.unistuttgart.vis.vita.analysis.results;

import java.util.Map;

import de.unistuttgart.vis.vita.model.entity.BasicEntity;

/**
 * The result of finding relations between entities
 */
public interface EntityRelations {

  /**
   * Gets the weight of the relation between the given entity and other entities
   * 
   * If there is no entry for an entity, there is no relation between the entities.
   *
   * @param entity the source entity
   * @return map of weighting factors
   */
  public Map<BasicEntity, Double> getRelatedEntities(BasicEntity entity);
}
