package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;

/**
 * Chapters will be built every time a MarkedHeading is found. The following MarkedHeadings will be
 * added to the chapters heading. This Analyzer only assures that there are no chapters with no
 * text, because the markers for this rule are set by the user and so no further analysis is needed.
 */
public class MarkedHeadingChapterAnalyzer extends AbstractChapterAnalyzer {

  /**
   * Instantiates a new MarkedHeadingChapterAnalyzer and sets the lines to analyze.
   * 
   * @param chapterArea ArrayList of Line - The lines containing the chapters. Should not be null.
   * @throws IllegalArgumentException If input is null.
   */
  protected MarkedHeadingChapterAnalyzer(ArrayList<Line> chapterArea)
      throws IllegalArgumentException {
    super(chapterArea);
  }

  @Override
  public int getStartOfAnalysis() {
    return 0;
  }

  @Override
  protected ChapterPosition useRule() {
    this.chapterPositions = detectSimpleChapters(true, LineType.MARKEDHEADING, 0);
    useEmptyChapterRule(true);
    return this.chapterPositions;
  }
}
