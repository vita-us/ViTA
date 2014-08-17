package de.unistuttgart.vis.vita.analysis;

public class UnresolvedModuleDependencyException extends RuntimeException {
  private static final long serialVersionUID = -6693108655419686293L;

  public UnresolvedModuleDependencyException() {

  }

  public UnresolvedModuleDependencyException(String message) {
    super(message);

  }

  public UnresolvedModuleDependencyException(Throwable cause) {
    super(cause);

  }

  public UnresolvedModuleDependencyException(String message, Throwable cause) {
    super(message, cause);

  }

  public UnresolvedModuleDependencyException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);

  }

}
