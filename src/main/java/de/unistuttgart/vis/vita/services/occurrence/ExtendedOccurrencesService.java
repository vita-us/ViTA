package de.unistuttgart.vis.vita.services.occurrence;

import javax.ws.rs.WebApplicationException;

/**
 * Extends the {@link OccurrencesService} with some check functions, which will throw
 * {@link WebApplicationException} containing an error-message, if the values are not valid.
 */
public abstract class ExtendedOccurrencesService extends OccurrencesService {

  private final static int MINIMUM_STEPS = 0;
  private final static int MAXIMUM_STEPS = 1000;

  /**
   * Checks if the amount of steps is valid.
   * 
   * @param steps - The steps.
   */
  protected void checkSteps(int steps) throws WebApplicationException {
    if (steps < MINIMUM_STEPS || steps > MAXIMUM_STEPS) {
      throw new WebApplicationException(new IllegalArgumentException("Illegal amount of steps!"),
          500);
    }
  }

  /**
   * Checks if the Range start and end is valid.
   * 
   * @param rangeStart - The start of the Range, number between 0 and 1.
   * @param rangeEnd - The end of the Range, number between 0 and 1.
   */
  protected void checkRange(double rangeStart, double rangeEnd) throws WebApplicationException {
    if (rangeEnd < rangeStart) {
      throw new WebApplicationException("Illegal range!");
    }
  }

  /**
   * Checks if the Range start is valid and transforms the rangeStart into a global offset.
   * 
   * @param rangeStart - The start of the Range, number between 0 and 1.
   * @return The global offset, number between 0 and document's length -1
   */
  protected int checkStartOffset(double rangeStart) throws WebApplicationException {
    int startOffset;
    try {
      startOffset = getStartOffset(rangeStart);
    } catch (IllegalRangeException ire) {
      throw new WebApplicationException(ire);
    }
    return startOffset;
  }

  /**
   * Checks if the Range end is valid and transforms the rangeEnd into a global offset.
   * 
   * @param rangeEnd - The end of the Range, number between 0 and 1.
   * @return The global offset, number between 0 and document's length -1
   */
  protected int checkEndOffset(double rangeEnd) throws WebApplicationException {
    int endOffset;
    try {
      endOffset = getEndOffset(rangeEnd);
    } catch (IllegalRangeException ire) {
      throw new WebApplicationException(ire);
    }
    return endOffset;
  }

}
