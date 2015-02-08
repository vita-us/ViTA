package de.unistuttgart.vis.vita.analysis;

/**
 * The execution of an analysis module failed
 */
public class ModuleExecutionException extends RuntimeException {
  private static final long serialVersionUID = 7707117006756195804L;

  public ModuleExecutionException() {
    super();
  }

  public ModuleExecutionException(String message) {
    super(message);
  }

  public ModuleExecutionException(Throwable cause) {
    super(cause);
  }

  public ModuleExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
