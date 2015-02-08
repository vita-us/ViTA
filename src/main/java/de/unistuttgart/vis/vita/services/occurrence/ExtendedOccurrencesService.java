package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Extends the {@link OccurrencesService} with some check functions, which will throw
 * {@link WebApplicationException} containing an error-message, if the values are not valid. Also
 * contains the standard implementation
 * {@link ExtendedOccurrencesService#getOccurrencesImpl(int, double, double)}.
 */
public abstract class ExtendedOccurrencesService extends OccurrencesService {

  private static final int MINIMUM_STEPS = 0;
  private static final int MAXIMUM_STEPS = 1000;

  /**
   * Checks if the amount of steps is valid.
   * 
   * @param steps - The steps.
   */
  protected void checkSteps(int steps) {
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
  protected void checkRange(double rangeStart, double rangeEnd) {
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
  protected int checkStartOffset(double rangeStart) {
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
  protected int checkEndOffset(double rangeEnd) {
    int endOffset;
    try {
      endOffset = getEndOffset(rangeEnd);
    } catch (IllegalRangeException ire) {
      throw new WebApplicationException(ire);
    }
    return endOffset;
  }


  /**
   * Standard Implementation: Reads occurrences of the specific attribute and entity from database
   * and returns them in JSON.
   * 
   * @param steps - amount of steps, the range should be divided into (default value 0 means exact)
   * @param rangeStart - start of range to be searched in
   * @param rangeEnd - end of range to be searched in
   * @return an OccurenceResponse holding all found Occurrences
   */
  protected OccurrencesResponse getOccurrencesImpl(int steps, double rangeStart, double rangeEnd) {
    checkSteps(steps);
    checkRange(rangeStart, rangeEnd);
    int startOffset = checkStartOffset(rangeStart);
    int endOffset = checkEndOffset(rangeEnd);

    List<Range> occs = null;
    if (steps == 0) {
      occs = getExactEntityOccurrences(startOffset, endOffset);
    } else {
      occs = getGranularEntityOccurrences(steps, startOffset, endOffset);
    }

    return new OccurrencesResponse(occs);
  }

  /**
   * For a given Range all Occurrences are returned. This defines the behavior of the standard
   * implementation of {@link ExtendedOccurrencesService#getOccurrencesImpl(int, double, double)}.
   * If the standard implementation is overridden, this method can throw
   * UnsupportedOperationException.
   * 
   * @param startOffset - start of range to be searched in
   * @param endOffset - end of range to be searched in
   * @return The Ranges of the Occurrences
   */
  protected abstract List<Range> getExactEntityOccurrences(int startOffset, int endOffset);

}
