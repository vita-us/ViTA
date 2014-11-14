package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.List;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.importer.txt.util.LineType;

/**
 * The advanced form of the Big Heading Chapter Analyzer tries to extend the heading with one
 * SMALLHEADING-Line, if there is one and this will not lead to empty chapters. After that the
 * percentage of extended headings will be provided.
 */
public class AdvancedBigHeadingChapterAnalyzer extends AbstractChapterAnalyzer {

  private float smallHeadingsPercentage;
  private int startOfAnalysis;

  /**
   * Instantiates a new AdvancedBigHeadingChapterAnalyzer which reuses the results of a
   * BigHeadingChapterAnalyzer. Therefore it needs the ChapterPosition and the List of Lines from
   * the previous Analysis, but will not change any data.
   *
   * @param chapterArea         ArrayList of Line - The lines containing the chapters. Should not be
   *                            null.
   * @param bigHeadingPositions ChapterPosition - The ChapterPosition from the BigHeadingAnalysis.
   * @param startOfAnalysis     int - the line at which the analysis should begin.
   * @throws IllegalArgumentException If chapterArea is null.
   */
  public AdvancedBigHeadingChapterAnalyzer(List<Line> chapterArea,
      ChapterPosition bigHeadingPositions, int startOfAnalysis) {
    super(chapterArea);
    this.chapterPositions = bigHeadingPositions.clone();
    this.startOfAnalysis = startOfAnalysis;
  }

  @Override
  public int getStartOfAnalysis() {
    return this.startOfAnalysis;
  }

  /**
   * Gets the percentage of headings which were successfully extended with a following small
   * heading. The value is available when the ChapterPosition is available too.
   *
   * @return float - the percentage, value between 0 and 1
   */
  public float getSmallHeadingsPercentage() {
    return smallHeadingsPercentage;
  }

  @Override
  protected ChapterPosition useRule() {
    int numberOfAddedSmallHeadings = addOneSmallHeadingRule();
    int oldNumberOfChapters = this.chapterPositions.size();
    useEmptyChapterRule(false);
    int newNumberOfChapters = this.chapterPositions.size();
    numberOfAddedSmallHeadings =
        numberOfAddedSmallHeadings - (oldNumberOfChapters - newNumberOfChapters);
    smallHeadingsPercentage = numberOfAddedSmallHeadings * (1.0f / newNumberOfChapters);
    return this.chapterPositions;
  }

  /**
   * Tries to add a Smallheading to the Heading, if there is one right behind the heading.
   *
   * @return int - The count of successfully added headings.
   */
  private int addOneSmallHeadingRule() {
    int numberOfAddedSmallHeadings = 0;
    for (int chapterNumber = 1; chapterNumber <= this.chapterPositions.size(); chapterNumber++) {
      int startOfHeading = this.chapterPositions.getStartOfHeading(chapterNumber);
      int startOfText = this.chapterPositions.getStartOfText(chapterNumber);
      int endOfText = this.chapterPositions.getEndOfText(chapterNumber);

      int nextSmallHeading = getNextPosition(true, LineType.SMALLHEADING, startOfText);
      if (nextSmallHeading < endOfText && nextSmallHeading >= startOfText) {
        boolean someWhitelinesAfter =
            (getNextPosition(false, LineType.WHITELINE, nextSmallHeading + 1) - nextSmallHeading)
            > 1;
        if (onlyOneTypeBetween(startOfText - 1, nextSmallHeading, LineType.WHITELINE)
            && !onlyOneTypeBetween(nextSmallHeading, endOfText + 1, LineType.WHITELINE)
            && someWhitelinesAfter) {
          this.chapterPositions.changeChapterData(chapterNumber, startOfHeading,
                                                  nextSmallHeading + 1, endOfText);
          numberOfAddedSmallHeadings++;
        }
      }
    }
    return numberOfAddedSmallHeadings;
  }
}
