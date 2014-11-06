package de.unistuttgart.vis.vita.services.occurrence;

/**
 * Exception which indicates that a given range was not between 0 and 1.
 */
public class IllegalRangeException extends Exception {

  private static final long serialVersionUID = -6207202165502785612L;

  /**
   * Creates a new instance of IllegalRangeException with the given message.
   * 
   * @param message - description why this exception occurred
   */
  public IllegalRangeException(String message) {
    super(message);
  }

}
