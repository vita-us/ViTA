package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineSubType;

/**
 * Adds an additional rule to a ChapterAnalyzer which uses subtypes to remove some false positive
 * chapter headings from the already found ones.<br>
 * <br>
 * STRONG RULE:<br>
 * When nearly all chapter headings are of the same type, the remaining headings will be removed. <br>
 * <br>
 * WEAK RULE: <br>
 * There is a majority of headings of the same type, the remaining headings without a subtype will
 * be removed.
 */
public abstract class AbstractSubtypeExtendedChapterAnalyzer extends AbstractChapterAnalyzer {
  // The borders for the use of small or strong rule.
  private static final float MINIMAL_WEAK_OCCURENCE_PERCENTAGE = 0.51f;
  private static final float MINIMAL_STRONG_OCCURENCE_PERCENTAGE = 0.8f;

  private int numberOfChapters;
  private Map<LineSubType, Integer> numberOfOccurenceMap = new HashMap<LineSubType, Integer>();

  /**
   * Initialize the Extended Abstract Chapter Analyzer and set the lines to analyze.
   * 
   * @param chapterArea ArrayList of Line - The lines in which the Chapters are.
   * @throws IllegalArgumentException If input is null.
   * 
   */
  public AbstractSubtypeExtendedChapterAnalyzer(List<Line> chapterArea) {
    super(chapterArea);
    if (chapterArea == null) {
      throw new IllegalArgumentException("chapterArea must not be null");
    }

    // initialize counters
    for (LineSubType subType : LineSubType.values()) {
      numberOfOccurenceMap.put(subType, 0);
    }
    numberOfChapters = 0;
  }

  /**
   * This rule will try to find and remove false positive chapter headings by analyzing the subtypes
   * of the chapter headings. This rule should be used when all other rules are already applied.
   */
  protected void useSubtypeReductionRule() {
    numberOfChapters = this.chapterPositions.size();
    computeSubtypeCount();
    float maxCountPercentage = computeMaxCountPercentage();
    if (useWeakRule(maxCountPercentage)) {
      if (useStrongRule(maxCountPercentage)) {
        LineSubType maxSubType = getSubTypeWithHighestCount();
        reduceChapterPosition(true, maxSubType);
      } else {
        reduceChapterPosition(false, null);
      }
    }
  }

  /**
   * Applies the strong/weak rule. The first chapter will not be changed! All other chapters will be
   * added to the text of the chapter before.
   * 
   * @param useStrongCondition true: use the strong rule. false: use the weak rule.
   * @param subtype For the strong rule this has to be the subtype which should not be removed. For
   *        the weak rule null can be used.
   */
  private void reduceChapterPosition(boolean useStrongCondition, LineSubType subtype) {
    int chapterNumber = 1;
    // iterate over all chapters
    while (chapterNumber <= this.chapterPositions.size()) {
      boolean changedChapterPosition = false;
      int headingStartIncl = this.chapterPositions.getStartOfHeading(chapterNumber);
      int headingEndExcl = this.chapterPositions.getStartOfText(chapterNumber);
      Iterable<Line> headingLines = this.chapterArea.subList(headingStartIncl, headingEndExcl);
      boolean isBadChapter = false;
      if (useStrongCondition) {
        isBadChapter = !containsSubType(headingLines, subtype);
      } else {
        isBadChapter = !linesHaveSubtype(headingLines);
      }
      // Do not delete first chapter
      if (isBadChapter && (chapterNumber != 1)) {
          changedChapterPosition = true;
          // delete chapter and add as text of previous chapter
          int previousChapterNumber = chapterNumber - 1;
          int newEndOfText = this.chapterPositions.getEndOfText(chapterNumber);
          int newStartOfText = this.chapterPositions.getStartOfText(previousChapterNumber);
          int newStartOfHeading = this.chapterPositions.getStartOfHeading(previousChapterNumber);
          this.chapterPositions.deleteChapter(chapterNumber);
          this.chapterPositions.changeChapterData(previousChapterNumber, newStartOfHeading,
              newStartOfText, newEndOfText);
      }
      // when a chapter has been deleted, the next chapter is at the old position.
      if (!changedChapterPosition) {
        chapterNumber++;
      }
    }
  }

  /**
   * Checks if there is at least one line containing a subtype.
   * 
   * @param lines the lines to search in.
   * @return true: at least one line is found. false: no line is found.
   */
  private boolean linesHaveSubtype(Iterable<Line> lines) {
    boolean found = false;
    for (Line line : lines) {
      if (line.hasSubType()) {
        found = true;
        break;
      }
    }
    return found;
  }

  /**
   * Finds the subtype with the highest number of occurence. If there are subtypes with the same
   * number one will be taken randomly.
   * 
   * @return the subtype.
   */
  private LineSubType getSubTypeWithHighestCount() {
    int maxCount = 0;
    LineSubType maxType = null;
    for (LineSubType subType : numberOfOccurenceMap.keySet()) {
      if (numberOfOccurenceMap.get(subType) >= maxCount) {
        maxCount = numberOfOccurenceMap.get(subType);
        maxType = subType;
      }
    }
    return maxType;
  }

  /**
   * Searches the highest number of occurence for a subtype and computes the percentage of headings
   * which are of the subtype with the highest number of occurence.
   * 
   * @return the percentage of headings which are of the subtype with the highest number of
   *         occurence. A number between 0 and 1.
   */
  private float computeMaxCountPercentage() {
    float maxCountPercentage;
    int maxCount = 0;
    for (int subTypeCount : numberOfOccurenceMap.values()) {
      if (subTypeCount > maxCount) {
        maxCount = subTypeCount;
      }
    }
    if (numberOfChapters > 0) {
      maxCountPercentage = (1.0f * maxCount) / (1.0f * numberOfChapters);
    } else {
      maxCountPercentage = 0;
    }
    return maxCountPercentage;
  }

  /**
   * Check if at least weak rule can be applied.
   * 
   * @param maxCountPercentage the (highest) percentage of headings which are of the subtype with
   *        the highest number of occurence. A number between 0 and 1.
   * @return true: At least weak rule should be used. false: Condition for weak rule not fulfilled.
   */
  private boolean useWeakRule(float maxCountPercentage) {
    return MINIMAL_WEAK_OCCURENCE_PERCENTAGE <= maxCountPercentage;
  }

  /**
   * Check if strong rule can be applied.
   * 
   * @param maxCountPercentage the (highest) percentage of headings which are of the subtype with
   *        the highest number of occurence. A number between 0 and 1.
   * @return true: Strong rule should be used. false: Condition for strong rule not fulfilled.
   */
  private boolean useStrongRule(float maxCountPercentage) {
    return MINIMAL_STRONG_OCCURENCE_PERCENTAGE <= maxCountPercentage;
  }

  /**
   * Counts the number of headings for each subtype. A heading maybe contains several subtypes!
   */
  private void computeSubtypeCount() {
    for (int chapterNumber = 1; chapterNumber <= numberOfChapters; chapterNumber++) {
      if (this.chapterPositions.hasHeading(chapterNumber)) {
        int headingStartIncl = this.chapterPositions.getStartOfHeading(chapterNumber);
        int headingEndExcl = this.chapterPositions.getStartOfText(chapterNumber);
        computeChapterSubtypeCount(this.chapterArea.subList(headingStartIncl, headingEndExcl));
      }
    }
  }

  /**
   * For the given lines (of a chapter heading) searches for the occurence of each subtype and adds
   * +1 to the counter if found.
   * 
   * @param lines The lines to search in - should be the heading of a chapter.
   */
  private void computeChapterSubtypeCount(Iterable<Line> lines) {
    for (LineSubType subtype : numberOfOccurenceMap.keySet()) {
      for (Line line : lines) {
        if (line.hasSubType() && line.isSubType(subtype)) {
          numberOfOccurenceMap.put(subtype, numberOfOccurenceMap.get(subtype) + 1);
          // search for next subtype.
          break;
        }
      }
    }
  }

  /**
   * Check if the given lines contain at least one subtype occurence.
   * 
   * @param lines The lines to search in - should be the heading of a chapter.
   * @param subtype The subtype to search for.
   * @return true: at least one line contains the given subtype. false: no line contains the given
   *         subtype.
   */
  private boolean containsSubType(Iterable<Line> lines, LineSubType subtype) {
    boolean found = false;
    for (Line line : lines) {
      if (line.hasSubType() && line.isSubType(subtype)) {
        found = true;
        break;
      }
    }
    return found;
  }

}
