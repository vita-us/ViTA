package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.List;

import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineType;

/**
 * Chapters will be built every time a Bigheading is found and there are at least two Whitelines in
 * front of it. The following BigHeadings will be added to the chapters heading and very small
 * 'chapters' will be attached to the Chapters before. Preface and Contents will be eliminated from
 * the analysis.
 */
public class BigHeadingChapterAnalyzer extends AbstractSubtypeExtendedChapterAnalyzer {

  private int startOfAnalysis;

  /**
   * Instantiates a new BigHeadingChapterAnalyzer and sets the lines to analyze.
   *
   * @param chapterArea ArrayList of Line - The lines containing the chapters. Should not be null.
   * @throws IllegalArgumentException If input is null.
   */
  public BigHeadingChapterAnalyzer(List<Line> chapterArea) {
    super(chapterArea);
    this.startOfAnalysis = getStartPosition();
  }

  @Override
  public int getStartOfAnalysis() {
    return this.startOfAnalysis;
  }

  @Override
  protected ChapterPosition useRule() {
    this.chapterPositions = detectSimpleChapters(true, LineType.BIGHEADING, this.startOfAnalysis);
    useTwoWhitelinesBeforeRule(true);
    useEmptyChapterRule(true);
    useLittleChapterRule(minimumChapterSize, false);
    useSubtypeReductionRule();
    return this.chapterPositions;
  }
}
