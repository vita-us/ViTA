package de.unistuttgart.vis.vita.analysis.modules.gate;

/**
 * A runtime exception with the semantics of an {@link InterruptedException}
 */
public class SoftInterruptedException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = 1338804617804505773L;

  public SoftInterruptedException() {}

  public SoftInterruptedException(String message) {
    super(message);
  }

  public SoftInterruptedException(String message, Throwable cause) {
    super(message, cause);
  }

  public SoftInterruptedException(Throwable cause) {
    super(cause);
  }
}
