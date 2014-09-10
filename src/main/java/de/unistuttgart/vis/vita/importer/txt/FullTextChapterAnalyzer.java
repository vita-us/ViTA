package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;


public class FullTextChapterAnalyzer extends AbstractChapterAnalyzer {

  public FullTextChapterAnalyzer(ArrayList<Line> chapterArea) throws IllegalArgumentException {
    super(chapterArea);
    // TODO Auto-generated constructor stub
  }

  protected ChapterPosition useRule() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 0, chapterArea.size());
    return positions;
  }

}
