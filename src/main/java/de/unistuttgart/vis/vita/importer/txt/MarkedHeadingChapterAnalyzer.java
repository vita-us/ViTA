package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;


public class MarkedHeadingChapterAnalyzer extends AbstractChapterAnalyzer {

  protected MarkedHeadingChapterAnalyzer(ArrayList<Line> chapterArea)
      throws IllegalArgumentException {
    super(chapterArea);
    // TODO Auto-generated constructor stub
  }

  protected ChapterPosition useRule() {
    ChapterPosition positions = detectSimpleChapters(true, LineType.MARKEDHEADING, 0);
    useEmptyChapterRule(positions, true);
    return positions;
  }

}

