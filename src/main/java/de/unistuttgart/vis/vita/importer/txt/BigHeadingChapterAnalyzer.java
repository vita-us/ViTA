package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;


public class BigHeadingChapterAnalyzer extends AbstractChapterAnalyzer {

  public BigHeadingChapterAnalyzer(ArrayList<Line> chapterArea) throws IllegalArgumentException {
    super(chapterArea);
    // TODO Auto-generated constructor stub
  }

  protected ChapterPosition useRule() {
    ChapterPosition positions = detectSimpleChapters(true, LineType.BIGHEADING, getStartPosition());
    useTwoWhitelinesBeforeRule(positions, true);
    useEmptyChapterRule(positions, true);
    useLittleChapterRule(positions, minimumChapterSize);
    return positions;
  }

}
