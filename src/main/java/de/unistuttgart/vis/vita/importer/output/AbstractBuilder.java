package de.unistuttgart.vis.vita.importer.output;

/**
 * A Builder should be used to set the attributes of the imported Book in a consistent
 * representation.
 */
public abstract class AbstractBuilder {
  private static final int STRING_LENGTH_LIMIT = 1000;

  /**
   * Makes a String shorter, if it is longer than allowed. This method should be called by every
   * String-setter for chapter- and book-attributes except the setter of the book's text.
   * 
   * @param stringToShorten The text to proof.
   * @return The (if needed) shortened text or original text.
   */
  protected String getShortenedString(String stringToShorten) {
    String shortenedString;
    if (stringToShorten.length() > STRING_LENGTH_LIMIT) {
      shortenedString = stringToShorten.substring(0, 1000);
    } else {
      shortenedString = stringToShorten;
    }
    return shortenedString;
  }

}
