package de.unistuttgart.vis.vita.analysis.results;

import java.util.List;

import de.unistuttgart.vis.vita.model.entity.BasicEntity;

/**
 * The result of ranking entities by their importance
 */
public interface EntityRanking {
  /**
   * Gets the entities in their correct order
   * 
   * @return the entities, ranked
   */
  public List<BasicEntity> getRankedEntities();
}
