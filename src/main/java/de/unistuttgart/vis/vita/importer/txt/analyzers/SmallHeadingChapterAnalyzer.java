package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.ArrayList;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.importer.txt.util.LineType;

/**
 * Chapters will be built every time a Smallheading is found and there are at least two Whitelines
 * in front of it, and one Whiteline behind. The heading contains only one line and very small
 * 'chapters' will be attached to the Chapters before. Preface and Contents will be eliminated from
 * the analysis.
 */
public class SmallHeadingChapterAnalyzer extends AbstractChapterAnalyzer {

  private int startOfAnalysis;

  /**
   * Instantiates a new SmallHeadingChapterAnalyzer and sets the lines to analyze.
   * 
   * @param chapterArea ArrayList of Line - The lines containing the chapters. Should not be null.
   * @throws IllegalArgumentException If input is null.
   */
  public SmallHeadingChapterAnalyzer(ArrayList<Line> chapterArea) throws IllegalArgumentException {
    super(chapterArea);
    this.startOfAnalysis = getStartPosition();
  }

  @Override
  public int getStartOfAnalysis() {
    return startOfAnalysis;
  }

  @Override
  protected ChapterPosition useRule() {
    this.chapterPositions = detectSimpleChapters(true, LineType.SMALLHEADING, this.startOfAnalysis);
    useTwoWhitelinesBeforeRule(false);
    useWhitelineAfterRule(false);
    useEmptyChapterRule(false);
    useLittleChapterRule(minimumChapterSize, false);
    return this.chapterPositions;
  }
}
