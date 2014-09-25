package de.unistuttgart.vis.vita.analysis;

public class InvalidModuleException extends RuntimeException {
  private static final long serialVersionUID = -7809014467592935284L;

  public InvalidModuleException() {
    super();
  }

  public InvalidModuleException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidModuleException(String message) {
    super(message);
  }

  public InvalidModuleException(Throwable cause) {
    super(cause);
  }

}
