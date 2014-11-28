package de.unistuttgart.vis.vita.services.occurrence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.RangeService;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;

/**
 * Abstract base class of every service dealing with Occurrences. Offers methods to convert
 * Lists of TextSpans into Lists of Occurrences and get the Document length.
 */
@ManagedBean
public abstract class OccurrencesService extends RangeService {

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
   * Returns the chapter which surrounds the position with the given offset.
   *
   * @param offset - the global character offset of the position for which the surrounding chapter
   *        should be found
   * @return the surrounding chapter
   */
  protected Chapter getSurroundingChapter(int offset) {
    TypedQuery<Chapter> chapterQ = em.createNamedQuery("Chapter.findChapterByOffset",
                                                        Chapter.class);
    chapterQ.setParameter("documentId", documentId);
    chapterQ.setParameter("offset", offset);

    List<Chapter> chapters = chapterQ.getResultList();
    if (chapters.isEmpty()) {
      throw new IllegalArgumentException("There is no chapter for the offset " +
          offset);
    }
    if (chapters.size() > 1) {
      throw new IllegalArgumentException("There is more than one (" +
          chapters.size() + ") chapter for the offset " + offset);
    }

    return chapters.get(0);
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

  protected abstract long getNumberOfSpansInStep(int stepStart, int stepEnd);

}
