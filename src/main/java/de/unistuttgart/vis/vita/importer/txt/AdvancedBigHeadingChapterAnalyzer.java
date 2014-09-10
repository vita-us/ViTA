package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;


public class AdvancedBigHeadingChapterAnalyzer extends AbstractChapterAnalyzer {
  private ChapterPosition bigHeaderPositions;
  private float smallHeadingsPercentage;

  public AdvancedBigHeadingChapterAnalyzer(ArrayList<Line> chapterArea,
      ChapterPosition bigHeaderPositions) throws IllegalArgumentException {
    super(chapterArea);
    this.bigHeaderPositions = bigHeaderPositions;
    // TODO Auto-generated constructor stub
  }

  public float getSmallHeadingsPercentage() {
    return smallHeadingsPercentage;
  }

  protected ChapterPosition useRule() {
    ChapterPosition newPositions = bigHeaderPositions.clone();
    int numberOfAddedSmallHeadings = addOneSmallHeadingRule(newPositions);
    int oldNumberOfChapters = newPositions.size();
    useEmptyChapterRule(newPositions, false);
    int newNumberOfChapters = newPositions.size();
    numberOfAddedSmallHeadings =
        numberOfAddedSmallHeadings - (oldNumberOfChapters - newNumberOfChapters);
    smallHeadingsPercentage = numberOfAddedSmallHeadings * (1.0f / newNumberOfChapters);
    return newPositions;
  }

  protected int addOneSmallHeadingRule(ChapterPosition positions) {
    int numberOfAddedSmallHeadings = 0;
    for (int chapterNumber = 1; chapterNumber <= positions.size(); chapterNumber++) {
      int startOfHeading = positions.getStartOfHeading(chapterNumber);
      int startOfText = positions.getStartOfText(chapterNumber);
      int endOfText = positions.getEndOfText(chapterNumber);
      int nextSmallHeading = getNextPosition(true, LineType.SMALLHEADING, startOfText);
      if (nextSmallHeading < endOfText && nextSmallHeading >= startOfText) {
        boolean someWhitelinesAfter =
            (getNextPosition(false, LineType.WHITELINE, nextSmallHeading + 1) - nextSmallHeading) > 1;
        if (onlyOneTypeBetween(startOfText - 1, nextSmallHeading, LineType.WHITELINE)
            && !onlyOneTypeBetween(nextSmallHeading, endOfText + 1, LineType.WHITELINE)
            && someWhitelinesAfter) {
          positions.changeChapterData(chapterNumber, startOfHeading, nextSmallHeading + 1,
              endOfText);
          numberOfAddedSmallHeadings++;
        }
      }
    }
    return numberOfAddedSmallHeadings;
  }

}
