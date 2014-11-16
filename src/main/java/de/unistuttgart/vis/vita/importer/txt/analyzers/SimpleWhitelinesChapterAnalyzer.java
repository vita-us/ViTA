package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.List;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.importer.txt.util.LineType;

/**
 * Chapters will be built every time two or more Whitelines in a row are found. There will be no
 * heading! Very small 'chapters' will be attached to the Chapters before. Preface and Contents will
 * be eliminated from the analysis.
 */
public class SimpleWhitelinesChapterAnalyzer extends AbstractChapterAnalyzer {

  private int startOfAnalysis;

  /**
   * Instantiates a new SimpleWhitelinesChapterAnalyzer and sets the lines to analyze.
   *
   * @param chapterArea ArrayList of Line - The lines containing the chapters. Should not be null.
   * @throws IllegalArgumentException If input is null.
   */
  public SimpleWhitelinesChapterAnalyzer(List<Line> chapterArea) {
    super(chapterArea);
    this.startOfAnalysis = getStartPosition();
  }

  @Override
  public int getStartOfAnalysis() {
    return startOfAnalysis;
  }

  @Override
  protected ChapterPosition useRule() {
    this.chapterPositions = detectSimpleChapters(false, LineType.WHITELINE, startOfAnalysis);
    makeAllHeadingsEmpty();
    useTwoWhitelinesBeforeRule(false);
    useEmptyChapterRule(false);
    useLittleChapterRule(minimumChapterSize, false);
    return this.chapterPositions;
  }

  /**
   * Assures all headings are empty.
   */
  private void makeAllHeadingsEmpty() {
    for (int chapterNumber = 1; chapterNumber <= this.chapterPositions.size(); chapterNumber++) {
      int startOfHeading = this.chapterPositions.getStartOfHeading(chapterNumber);
      int endOfText = this.chapterPositions.getEndOfText(chapterNumber);
      this.chapterPositions.changeChapterData(chapterNumber, startOfHeading, startOfHeading,
                                              endOfText);
    }
  }

}
