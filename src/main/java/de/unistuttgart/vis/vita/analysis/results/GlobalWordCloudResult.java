package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

/**
 * The result of calculating the global word cloud
 */
public interface GlobalWordCloudResult {

  /**
   * Gets the global word cloud
   */
  public WordCloud getGlobalWordCloud();
}
