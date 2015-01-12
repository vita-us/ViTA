package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.List;

import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineType;

/**
 * Chapters will be built every time a Smallheading is found and there are at least two Whitelines
 * in front of it, and one Whiteline behind. The heading contains only one line and very small
 * 'chapters' will be attached to the Chapters before. Preface and Contents will be eliminated from
 * the analysis. Tries to ignore false positive results depending on subtypes of heading lines.
 */
public class SmallHeadingChapterAnalyzer extends AbstractSubtypeExtendedChapterAnalyzer {

  private int startOfAnalysis;

  /**
   * Instantiates a new SmallHeadingChapterAnalyzer and sets the lines to analyze.
   *
   * @param chapterArea ArrayList of Line - The lines containing the chapters. Should not be null.
   * @throws IllegalArgumentException If input is null.
   */
  public SmallHeadingChapterAnalyzer(List<Line> chapterArea) {
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
    useSubtypeReductionRule();
    return this.chapterPositions;
  }
}
