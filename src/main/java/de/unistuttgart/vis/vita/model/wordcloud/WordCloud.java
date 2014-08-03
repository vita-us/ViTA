package de.unistuttgart.vis.vita.model.wordcloud;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the model for a WordCloud. A WordCloud shows a set of words in different font sizes,
 * depending on how often they are used in a document.
 */
public class WordCloud {

  private Set<WordCloudItem> items;

  /**
   * Creates a new empty WordCloud.
   */
  public WordCloud() {
    this.items = new HashSet<>();
  }

  /**
   * Creates a new WordCloud with the given items.
   * 
   * @param pItems - the items for the new WordCloud
   */
  public WordCloud(Set<WordCloudItem> pItems) {
    this.setItems(pItems);
  }

  /**
   * @return a Set of all items of this WordCloud
   */
  public Set<WordCloudItem> getItems() {
    return items;
  }

  /**
   * Sets the items for this WordCloud
   * 
   * @param newItems
   */
  public void setItems(Set<WordCloudItem> newItems) {
    this.items = newItems;
  }

}
