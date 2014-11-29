package de.unistuttgart.vis.vita.model.search;

/**
 * For every first word of a searchString array a Phrase will be build, which contains the relative
 * end offset and the built string phrase
 * 
 *
 */
public class Phrase {

  private int endOffset;
  private String phrase;

  public Phrase(int newEndOffSet, String newPhrase) {
    this.endOffset = newEndOffSet;
    this.phrase = newPhrase;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }

  public String getPhrase() {
    return phrase;
  }

  public void setPhrase(String sentence) {
    this.phrase = sentence;
  }

}
