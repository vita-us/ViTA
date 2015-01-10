package de.unistuttgart.vis.vita.services.document;

import javax.validation.ConstraintViolation;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Set;

public class ValidationViolationException extends WebApplicationException {
  public <T> ValidationViolationException(Set<ConstraintViolation<T>> violations) {
    super(Response
      .status(400)
      .entity(violations.iterator().next().getMessage())
      .build());
  }
}
