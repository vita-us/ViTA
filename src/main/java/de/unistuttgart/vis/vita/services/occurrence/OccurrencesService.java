package de.unistuttgart.vis.vita.services.occurrence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;

/**
 * Abstract base class of every service dealing with Occurrences. Offers methods to convert
 * Lists of TextSpans into Lists of Occurrences and get the Document length.
 */
@ManagedBean
public abstract class OccurrencesService {

  @Inject
  protected EntityManager em;

  protected String documentId;
  private int documentLength;

  /**
   * Converts a given List of TextSpans into Occurrences.
   *
   * @param textSpans - the TextSpans to be converted
   * @return list of Occurrences
   */
  public List<Occurrence> convertSpansToOccurrences(List<TextSpan> textSpans) {
    List<Occurrence> occurrences = new ArrayList<>();
    int docLength = getDocumentLength();

    for (TextSpan span : textSpans) {
      occurrences.add(span.toOccurrence(docLength));
    }

    return occurrences;
  }

  /**
   * Returns the length of the current document.
   *
   * @return document length
   */
  public int getDocumentLength() {
    if (documentLength <= 0) {
      Document doc = readDocumentFromDatabase();
      documentLength = doc.getMetrics().getCharacterCount();
    }
    return documentLength;
  }

  /**
   * Reads the current document from the database and returns it.
   *
   * @return current document
   */
  private Document readDocumentFromDatabase() {
    TypedQuery<Document> docQuery = em.createNamedQuery("Document.findDocumentById",
                                                        Document.class);
    docQuery.setParameter("documentId", documentId);
    return docQuery.getSingleResult();
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

  /**
   * Returns the chapter which surrounds the position with the given offset.
   *
   * @param offset - the global character offset of the position for which the surrounding chapter
   *        should be found
   * @return the surrounding chapter
   */
  protected Chapter getSurroundingChapter(int offset) {
    TypedQuery<Chapter> chapterQ = em.createNamedQuery("Chapter.findChapterByOffset",
                                                        Chapter.class);
    chapterQ.setParameter("offset", offset);

    List<Chapter> chapters = chapterQ.getResultList();
    if (chapters.size() == 1) {
      return chapters.get(0);
    }

    return null;
  }

  protected List<Occurrence> getGranularEntityOccurrences(int steps, int startOffset, int endOffset) {
    // compute sizes of range and steps
    int rangeSize = endOffset - startOffset;
    int stepSize = rangeSize / steps;

    List<TextSpan> stepSpans = new ArrayList<>();

    for (int step = 0; step < steps; step++) {
      int stepStart = startOffset + (stepSize * step);
      int stepEnd = startOffset + (stepSize * (step + 1));

      if (getNumberOfSpansInStep(stepStart, stepEnd) > 0) {
        // create new Positions with offset and surrounding Chapter
        TextPosition startPos = TextPosition.fromGlobalOffset(getSurroundingChapter(stepStart), stepStart);
        TextPosition endPos = TextPosition.fromGlobalOffset(getSurroundingChapter(stepEnd), stepEnd);

        // create and add new Span
        stepSpans.add(new TextSpan(startPos, endPos));
      }
    }

    return convertSpansToOccurrences(stepSpans);
  }

  protected abstract int getNumberOfSpansInStep(int stepStart, int stepEnd);

}
