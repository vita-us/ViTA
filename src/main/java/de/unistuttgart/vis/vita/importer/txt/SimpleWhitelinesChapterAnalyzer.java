package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;


public class SimpleWhitelinesChapterAnalyzer extends AbstractChapterAnalyzer {

  public SimpleWhitelinesChapterAnalyzer(ArrayList<Line> chapterArea)
      throws IllegalArgumentException {
    super(chapterArea);
    // TODO Auto-generated constructor stub
  }

  protected ChapterPosition useRule() {
    ChapterPosition positions = detectSimpleChapters(false, LineType.WHITELINE, getStartPosition());
    useTwoWhitelinesBeforeRule(positions, false);
    useEmptyChapterRule(positions, false);
    useLittleChapterRule(positions, minimumChapterSize);
    for (int chapterNumber = 1; chapterNumber <= positions.size(); chapterNumber++) {
      int startOfHeading = positions.getStartOfHeading(chapterNumber);
      int endOfText = positions.getEndOfText(chapterNumber);
      positions.changeChapterData(chapterNumber, startOfHeading, startOfHeading, endOfText);
    }
    return positions;
  }

}
