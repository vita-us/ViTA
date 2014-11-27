package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

/**
 * The result of calculating the word clouds for entities
 */
public interface EntityWordCloudResult {
  /**
   * Gets the word cloud for the given entity
   *
   * @param entity the entity
   * @return the word cloud
   */
  public WordCloud getWordCloudForEntity(BasicEntity entity);
}
