package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;


public class SmallHeadingChapterAnalyzer extends AbstractChapterAnalyzer {

  public SmallHeadingChapterAnalyzer(ArrayList<Line> chapterArea) throws IllegalArgumentException {
    super(chapterArea);
    // TODO Auto-generated constructor stub
  }

  protected ChapterPosition useRule() {
    ChapterPosition positions =
        detectSimpleChapters(true, LineType.SMALLHEADING, getStartPosition());
    useTwoWhitelinesBeforeRule(positions, false);
    useWhitelineAfterRule(positions, false);
    useEmptyChapterRule(positions, false);
    useLittleChapterRule(positions, minimumChapterSize);
    return positions;
  }

}
