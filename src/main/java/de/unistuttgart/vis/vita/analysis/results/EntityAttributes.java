package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

import java.util.Set;

/**
 * The result of finding attributes of entities
 */
public interface EntityAttributes {

  /**
   * Gets the attributes for the given entity
   *
   * @param entity the entity
   * @return the attributes of the given entity
   */
  public Set<Attribute> getAttributesForEntity(BasicEntity entity);
}
