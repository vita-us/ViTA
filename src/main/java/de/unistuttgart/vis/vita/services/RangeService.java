package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.occurrence.IllegalRangeException;

/**
 * Represents a service offering data from specific ranges of a Document. Provides methods to 
 * check ranges and calculate offsets.
 */
public abstract class RangeService extends BaseService {

  protected DocumentDao documentDao;
  
  protected String documentId;
  private int documentLength;

  @Override public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
  }

  /**
   * Returns the length of the current document.
   *
   * @return document length
   */
  public int getDocumentLength() {
    if (documentLength <= 0) {
      Document doc = documentDao.findById(documentId);
      documentLength = doc.getMetrics().getCharacterCount();
    }
    return documentLength;
  }

  /**
   * Returns the absolute offset for a given range start.
   *
   * @param rangeStart - the start of a range as a value between 0 and 1
   * @return the absolute offset (rounded down)
   * @throws IllegalRangeException if given range value is not valid
   */
  public int getStartOffset(double rangeStart) throws IllegalRangeException {
    if (!isValidRangeValue(rangeStart)) {
      throw new IllegalRangeException("Illegal range start!");
    }
    return (int) Math.floor(getDocumentLength() * rangeStart);
  }

  /**
   * Returns the absolute offset for a given range End.
   *
   * @param rangeEnd - the end of the range as a value between 0 and 1
   * @return the absolute offset (rounded up)
   * @throws IllegalRangeException if given range value is not valid
   */
  public int getEndOffset(double rangeEnd) throws IllegalRangeException {
    if (!isValidRangeValue(rangeEnd)) {
      throw new IllegalRangeException("Illegal range end!");
    }
    return (int) Math.ceil(getDocumentLength() * rangeEnd);
  }

  /**
   * Checks whether a given range value is between 0 and 1.
   *
   * @param rangeValue - the range value to be checked
   * @return true if given range value is valid, false otherwise
   */
  public boolean isValidRangeValue(double rangeValue) {
    return rangeValue >= 0 && rangeValue <= 1;
  }

}
