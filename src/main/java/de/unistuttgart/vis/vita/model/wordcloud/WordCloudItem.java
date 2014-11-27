package de.unistuttgart.vis.vita.model.wordcloud;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * Represents one item of a WordCloud. This includes a word and its frequency of occurrence in a
 * document.
 */
@Entity
public class WordCloudItem extends AbstractEntityBase implements Comparable<WordCloudItem> {

  // constants
  private static final int MIN_FREQUENCY = 1;

  // attributes
  @XmlElement
  private String word;

  @XmlElement
  private int frequency;

  /**
   * Creates a new item of a WordCloud without a word and frequency 0.
   */
  public WordCloudItem() {

  }

  /**
   * Creates a new item of a WordCloud with given word and frequency of usage.
   *
   * @param pWord      - the word which should be shown in the WordCloud
   * @param pFrequency - how often this word occurs in the document
   */
  public WordCloudItem(String pWord, int pFrequency) {
    this.setWord(pWord);
    this.setFrequency(pFrequency);
  }

  /**
   * @return the word to be shown in the WordCloud
   */
  public String getWord() {
    return word;
  }

  /**
   * Sets the name for this item of the WordCloud.
   *
   * @param word - the word which should be shown in the WordCloud
   */
  public void setWord(String word) {
    this.word = word;
  }

  /**
   * @return the number of occurrences of the word in a document
   */
  public int getFrequency() {
    return frequency;
  }

  /**
   * Sets the frequency of occurrences for this word.
   *
   * @param frequency - number of occurrences in a document, must be greater or equals 1
   */
  public void setFrequency(int frequency) {
    if (frequency < MIN_FREQUENCY) {
      throw new IllegalArgumentException("word must occur at least one time!");
    }
    this.frequency = frequency;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof WordCloudItem)) {
      return false;
    }

    WordCloudItem other = (WordCloudItem)obj;
    return other.word.equals(word) && other.frequency == frequency;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(word).append(frequency).toHashCode();
  }

  @Override
  public String toString() {
    return String.format("%s x %d", word, frequency);
  }

  @Override
  public int compareTo(WordCloudItem o) {
    // compareTo must not return zero if the items are not identical, so set up some artificial
    // ordering in that case
    if (frequency == o.frequency)
      return word.compareTo(o.word);

    return Integer.compare(frequency, o.frequency);
  }
}
