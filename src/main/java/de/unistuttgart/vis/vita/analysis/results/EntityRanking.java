package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.entity.BasicEntity;

import java.util.List;

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
