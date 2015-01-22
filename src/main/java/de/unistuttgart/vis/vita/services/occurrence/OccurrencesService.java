package de.unistuttgart.vis.vita.services.occurrence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;

import de.unistuttgart.vis.vita.model.dao.ChapterDao;
import de.unistuttgart.vis.vita.model.dao.OccurrenceDao;
import de.unistuttgart.vis.vita.model.document.*;

import de.unistuttgart.vis.vita.services.RangeService;


/**
 * Abstract base class of every service dealing with Occurrences. Offers methods
 * to convert Lists of Occurrences into Lists of Ranges and get the Document
 * length.
 */
@ManagedBean
public abstract class OccurrencesService extends RangeService {

  private ChapterDao chapterDao;
  protected OccurrenceDao occurrenceDao;

  @Override public void postConstruct() {
    super.postConstruct();
    chapterDao = getDaoFactory().getChapterDao();
    occurrenceDao = getDaoFactory().getOccurrenceDao();
  }

  /**
   * Converts a given List of Occurrences into Ranges.
   *
   * @param occurrences
   *          - the Occurrences to be converted
   * @return list of Occurrences
   */
  public List<Range> convertOccurrencesToRanges(List<Occurrence> occurrences) {
    List<Range> ranges = new ArrayList<>();

    for (Occurrence occ : occurrences) {
      ranges.add(occ.getRange());

    }
    return ranges;
  }

  /**
   * Converts the occurrences to ranges of their sentences, merging adjacent sentences
   * @param occurrences the occurrences whose sentences are considered
   * @return the range of the sentences
   */
  protected List<Range> mergeAdjacentSentences(List<Sentence> sentences) {
    List<Range> newList = new ArrayList<>();
    int lastSentenceIndex = Integer.MIN_VALUE;
    Range currentRange = null;
    for (Sentence sentence : sentences) {
      if (currentRange != null && sentence.getIndex() == lastSentenceIndex + 1) {
        currentRange = new Range(currentRange.getStart(), sentence.getRange().getEnd());
      } else {
        if (currentRange != null) {
          newList.add(currentRange);
        }
        currentRange = sentence.getRange();
      }
      lastSentenceIndex = sentence.getIndex();
    }
    if (currentRange != null) {
      newList.add(currentRange);
    }
    return newList;
  }

  /**
   * Returns the chapter which surrounds the position with the given offset.
   *
   * @param offset
   *          - the global character offset of the position for which the
   *          surrounding chapter should be found
   * @return the surrounding chapter
   */
  protected Chapter getSurroundingChapter(int offset) {
    List<Chapter> chapters = chapterDao.findChaptersByOffset(documentId, offset);
    if (chapters.isEmpty()) {
      throw new IllegalArgumentException("There is no chapter for the offset " + offset);
    }
    if (chapters.size() > 1) {
      throw new IllegalArgumentException("There is more than one (" + chapters.size()
          + ") chapter for the offset " + offset);
    }

    return chapters.get(0);
  }

  protected List<Range> getGranularEntityOccurrences(int steps, int startOffset,
      int endOffset) {
    // compute sizes of range and steps
    int rangeSize = endOffset - startOffset;
    int stepSize = rangeSize / steps;

    List<Range> stepSpans = new ArrayList<>();

    TextPosition currentSpanStart = null;
    TextPosition currentSpanEnd = null;
    boolean includesLastStep = false;
    for (int step = 0; step < steps; step++) {
      int stepStart = startOffset + (stepSize * step);
      int stepEnd = startOffset + (stepSize * (step + 1));

      if (getNumberOfOccurrencesInStep(stepStart, stepEnd) > 0) {
        if (!includesLastStep) {
          // Start a new step
          includesLastStep = true;
          currentSpanStart = TextPosition.fromGlobalOffset(
              stepStart, getDocumentLength());
        }
        currentSpanEnd = TextPosition.fromGlobalOffset(stepEnd,
            getDocumentLength());

      } else if (includesLastStep) {
        // The step is over, add it to the list
        stepSpans.add(new Range(currentSpanStart, currentSpanEnd));
        includesLastStep = false;
      }
    }

    if (includesLastStep) {
      // There is a step at the very end
      stepSpans.add(new Range(currentSpanStart, currentSpanEnd));
    }

    return stepSpans;
  }

  protected abstract long getNumberOfOccurrencesInStep(int firstSentenceIndex, int lastSentenceIndex);
}
