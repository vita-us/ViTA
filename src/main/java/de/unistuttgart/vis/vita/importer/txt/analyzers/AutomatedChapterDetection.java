package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;

/**
 * Uses different Chapter Analyzer and decides which result is most likely the best result for a
 * typical English novel.
 */
public class AutomatedChapterDetection {

  private static final float HUGE_CHAPTER_PERCENTAGE = 0.5f;
  private static final float MINIMUM_SMALLHEADINGS_PERCENTAGE = 0.4f;

  private float smallHeadingsPercentage = 0.0f;
  private int bigHeadingAnalysisStart;
  private ExecutorService executor = Executors.newCachedThreadPool();
  private List<Line> lines = new ArrayList<>();
  private List<Future<ChapterPosition>> activeChapterAnalyzers = new ArrayList<>();
  private Future<ChapterPosition> markedHeadingChapters;
  private Future<ChapterPosition> bigHeadingChapters;
  private Future<ChapterPosition> advancedBigHeadingChapters;
  private Future<ChapterPosition> smallHeadingChapters;
  private Future<ChapterPosition> simpleWhitelinesChapters;
  private Future<ChapterPosition> noChapters;
  private ChapterPosition result;

  /**
   * Instantiates a new AutomatedChapterDetection for exactly one list of lines. The result is
   * available at 'getChapterPosition()'.
   *
   * @param lines ArrayList of Line - The lines to analyze, usually the text of the book.
   */
  public AutomatedChapterDetection(List<Line> lines) {
    this.lines = lines;
    startBasicChapterDetection();
    this.result = chooseChapterPositions();
  }

  /**
   * Returns the computed Chapter Position.
   *
   * @return ChapterPosition - The exact result depends on the chosen Rule for the Chapter
   * Detection. The result usually is not empty and contains at least one Chapter, so there is
   * always a result to work with.
   */
  public ChapterPosition getChapterPosition() {
    return this.result;
  }

  /**
   * Initializes the ChapterAnalyzers which do not need data from other ChapterAnalyzers. Adds them
   * to the list of active Chapter Analyzers.
   */
  private void startBasicChapterDetection() {
    Callable<ChapterPosition> markedHeadingChapterPositions =
        new MarkedHeadingChapterAnalyzer(lines);
    markedHeadingChapters = executor.submit(markedHeadingChapterPositions);
    activeChapterAnalyzers.add(markedHeadingChapters);

    BigHeadingChapterAnalyzer bigHeadingChapterPositions = new BigHeadingChapterAnalyzer(lines);
    this.bigHeadingAnalysisStart = bigHeadingChapterPositions.getStartOfAnalysis();
    bigHeadingChapters = executor.submit(bigHeadingChapterPositions);
    activeChapterAnalyzers.add(bigHeadingChapters);

    Callable<ChapterPosition> smallHeadingChapterPositions = new SmallHeadingChapterAnalyzer(lines);
    smallHeadingChapters = executor.submit(smallHeadingChapterPositions);
    activeChapterAnalyzers.add(smallHeadingChapters);

    Callable<ChapterPosition> simpleWhitelinesChapterPositions =
        new SimpleWhitelinesChapterAnalyzer(lines);
    simpleWhitelinesChapters = executor.submit(simpleWhitelinesChapterPositions);
    activeChapterAnalyzers.add(simpleWhitelinesChapters);

    Callable<ChapterPosition> noChapterPositions = new FullTextChapterAnalyzer(lines);
    noChapters = executor.submit(noChapterPositions);
    activeChapterAnalyzers.add(noChapters);
  }

  /**
   * Stops all active ChapterDetections, without removing them from the list!
   */
  private void stopChapterDetection() {
    for (Future<ChapterPosition> chapterDetection : activeChapterAnalyzers) {
      chapterDetection.cancel(true);
    }
  }

  /**
   * Get the results of the Chapter Analyzers and checks if the result fulfills the conditions. When
   * a result is found, all other ChapterAnalyzers are cancelled.
   *
   * @return ChapterPosition - A result of a Chapter Analyzer which fulfills the conditions for the
   * analyzer.
   */
  private ChapterPosition chooseChapterPositions() {
    ChapterPosition analyzerResultToCheck = null;
    try {

      // check results of the analyzers and take first which fulfills conditions
      analyzerResultToCheck = markedHeadingChapters.get();
      if (fulfillsMarkedHeadingConditions(analyzerResultToCheck)) {
        stopChapterDetection();
      } else {

        AdvancedBigHeadingChapterAnalyzer advancedBigHeadingChapterPositions =
            new AdvancedBigHeadingChapterAnalyzer(lines, bigHeadingChapters.get(),
                                                  this.bigHeadingAnalysisStart);
        advancedBigHeadingChapters = executor.submit(advancedBigHeadingChapterPositions);
        activeChapterAnalyzers.add(advancedBigHeadingChapters);
        analyzerResultToCheck = advancedBigHeadingChapters.get();
        smallHeadingsPercentage = advancedBigHeadingChapterPositions.getSmallHeadingsPercentage();

        if (fulfillsAdvancedBigHeadingConditions(analyzerResultToCheck)) {
          stopChapterDetection();
        } else {

          analyzerResultToCheck = bigHeadingChapters.get();
          if (fulfillsBigHeadingConditions(analyzerResultToCheck)) {
            stopChapterDetection();
          } else {

            analyzerResultToCheck = smallHeadingChapters.get();
            if (fulfillsSmallHeadingConditions(analyzerResultToCheck)) {
              stopChapterDetection();
            } else {

              analyzerResultToCheck = simpleWhitelinesChapters.get();
              if (fulfillsSimpleWhitelinesConditions(analyzerResultToCheck)) {
                stopChapterDetection();
              } else {
                analyzerResultToCheck = noChapters.get();
              }
            }
          }
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      // TODO propagate InterruptedException to allow for manual termination, and
      // do not suppress ExecutionExceptions
    }
    return analyzerResultToCheck;
  }

  /**
   * Checks if a Chapter Position fulfills the conditions to take marked heading chapters as result.
   * In this case it is enough if there is at least one chapter.
   *
   * @param markedHeadingPositions ChapterPosition - The information about the chapters, should be
   *                               the result of a Marked Heading Chapter Analyzer.
   * @return boolean - true if the inserted ChapterPosition fulfills all conditions for valid
   * results of the Marked Heading Analyzer.
   */
  private boolean fulfillsMarkedHeadingConditions(ChapterPosition markedHeadingPositions) {
    return !markedHeadingPositions.isEmpty();
  }

  /**
   * Checks if a Chapter Position fulfills the conditions to take big heading chapters as result. In
   * this case it is enough if there is at least one chapter and there are no huge chapters.
   *
   * @param bigHeadingPositions ChapterPosition - The information about the chapters, should be the
   *                            result of a Big Heading Chapter Analyzer.
   * @return boolean - true if the inserted ChapterPosition fulfills all conditions for valid
   * results of the Big Heading Chapter Analyzer.
   */
  private boolean fulfillsBigHeadingConditions(ChapterPosition bigHeadingPositions) {
    return !bigHeadingPositions.isEmpty() && !hasHugeChapter(bigHeadingPositions);
  }

  /**
   * Checks if a Chapter Position fulfills the conditions to take advanced big heading chapters as
   * result. Of course they must fulfill the big heading chapter conditions and there must be an
   * amount of headings which are extended by the advanced analyzer.
   *
   * @param advancedBigHeadingPositions ChapterPosition - The information about the chapters, should
   *                                    be the result of a Advanced Big Heading Chapter Analyzer.
   * @return boolean - true if the inserted ChapterPosition fulfills all conditions for valid
   * results of the Advanced Big Heading Chapter Analyzer.
   */
  private boolean fulfillsAdvancedBigHeadingConditions(
      ChapterPosition advancedBigHeadingPositions) {
    boolean enoughSmallHeadings = smallHeadingsPercentage >= MINIMUM_SMALLHEADINGS_PERCENTAGE;
    return fulfillsBigHeadingConditions(advancedBigHeadingPositions) && enoughSmallHeadings;
  }

  /**
   * Checks if a Chapter Position fulfills the conditions to take small heading chapters as result.
   * this case it is enough if there is at least one chapter and there are no huge chapters.
   *
   * @param smallHeadingPositions ChapterPosition - The information about the chapters, should be
   *                              the result of a Small Heading Chapter Analyzer.
   * @return boolean - true if the inserted ChapterPosition fulfills all conditions for valid
   * results of the Small Heading Chapter Analyzer.
   */
  private boolean fulfillsSmallHeadingConditions(ChapterPosition smallHeadingPositions) {
    return !smallHeadingPositions.isEmpty() && !hasHugeChapter(smallHeadingPositions);
  }

  /**
   * Checks if a Chapter Position fulfills the conditions to take simple whitelines chapters as
   * result. this case it is enough if there is at least one chapter and there are no huge
   * chapters.
   *
   * @param simpleWhitelinesPositions ChapterPosition - The information about the chapters, should
   *                                  be the result of a Simple Whitelines Chapter Analyzer.
   * @return - true if the inserted ChapterPosition fulfills all conditions for valid results of the
   * Simple Whitelines Chapter Analyzer.
   */
  private boolean fulfillsSimpleWhitelinesConditions(ChapterPosition simpleWhitelinesPositions) {
    return !simpleWhitelinesPositions.isEmpty() && !hasHugeChapter(simpleWhitelinesPositions);
  }

  /**
   * Checks if the ChapterPositions have huge Chapters. This is atypical for a correctly analyzed
   * book.
   *
   * @param positions ChapterPosition - The information about the chapters.
   * @return boolean - true: there is at least one huge chapter. false: there is no huge chapter.
   */
  private boolean hasHugeChapter(ChapterPosition positions) {
    boolean hasHugeChapter = false;
    for (int chapterNumber = positions.size(); chapterNumber >= 1; chapterNumber--) {
      int chapterTextStart = positions.getStartOfText(chapterNumber);
      int chapterTextEnd = positions.getEndOfText(chapterNumber);
      int chapterLength = chapterTextEnd - chapterTextStart;
      if (chapterLength >= HUGE_CHAPTER_PERCENTAGE * lines.size()) {
        hasHugeChapter = true;
        break;
      }
    }
    return hasHugeChapter;
  }
}
