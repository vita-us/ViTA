package de.unistuttgart.vis.vita.services.occurrence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import de.unistuttgart.vis.vita.model.dao.ChapterDao;
import de.unistuttgart.vis.vita.model.dao.TextSpanDao;
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

  private ChapterDao chapterDao;
  protected TextSpanDao textSpanDao;

  @Override public void postConstruct() {
    super.postConstruct();
    chapterDao = getDaoFactory().getChapterDao();
    textSpanDao = getDaoFactory().getTextSpanDao();
  }

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
    List<Chapter> chapters = chapterDao.findChaptersByOffset(documentId, offset);
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

    TextPosition currentSpanStart = null;
    TextPosition currentSpanEnd = null;
    boolean includesLastStep = false;
    for (int step = 0; step < steps; step++) {
      int stepStart = startOffset + (stepSize * step);
      int stepEnd = startOffset + (stepSize * (step + 1));

      if (getNumberOfSpansInStep(stepStart, stepEnd) > 0) {
        if (!includesLastStep) {
          // Start a new step
          includesLastStep = true;
          currentSpanStart = TextPosition.fromGlobalOffset(getSurroundingChapter(stepStart), stepStart);
        }
        currentSpanEnd = TextPosition.fromGlobalOffset(getSurroundingChapter(stepEnd), stepEnd);

      } else if (includesLastStep) {
        // The step is over, add it to the list
        stepSpans.add(new TextSpan(currentSpanStart, currentSpanEnd));
        includesLastStep = false;
      }
    }
    
    if (includesLastStep) {
      // There is a step at the very end
      stepSpans.add(new TextSpan(currentSpanStart, currentSpanEnd));
    }

    return convertSpansToOccurrences(stepSpans);
  }

  protected abstract long getNumberOfSpansInStep(int stepStart, int stepEnd);

}
