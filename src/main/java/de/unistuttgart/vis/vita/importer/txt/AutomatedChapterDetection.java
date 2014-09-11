package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class AutomatedChapterDetection {
  private final float MINIMUMSMALLHEADINGSPERCENTAGE = 0.4f;
  private final float HUGECHAPTERPERCENTAGE = 0.8f;

  private float smallHeadingsPercentage = 0.0f;
  private ArrayList<Line> lines = new ArrayList<Line>();
  private ExecutorService executor = Executors.newCachedThreadPool();
  private ArrayList<Future<ChapterPosition>> activeChapterDetections =
      new ArrayList<Future<ChapterPosition>>();
  private Future<ChapterPosition> markedHeadingChapters;
  private Future<ChapterPosition> bigHeadingChapters;
  private Future<ChapterPosition> advancedBigHeadingChapters;
  private Future<ChapterPosition> smallHeadingChapters;
  private Future<ChapterPosition> twoWhitelinesChapters;
  private Future<ChapterPosition> noChapters;
  private ChapterPosition result;

  public AutomatedChapterDetection(ArrayList<Line> lines) {
    this.lines = lines;
    startBasicChapterDetection();
    result = chooseChapterPositions();
  }

  public ChapterPosition getChapterPosition() {
    return result;
  }

  private void startBasicChapterDetection() {
    Callable<ChapterPosition> markedHeadingChapterPositions =
        new MarkedHeadingChapterAnalyzer(lines);
    markedHeadingChapters = executor.submit(markedHeadingChapterPositions);
    activeChapterDetections.add(markedHeadingChapters);

    Callable<ChapterPosition> bigHeadingChapterPositions = new BigHeadingChapterAnalyzer(lines);
    bigHeadingChapters = executor.submit(bigHeadingChapterPositions);
    activeChapterDetections.add(bigHeadingChapters);

    Callable<ChapterPosition> smallHeadingChapterPositions = new SmallHeadingChapterAnalyzer(lines);
    smallHeadingChapters = executor.submit(smallHeadingChapterPositions);
    activeChapterDetections.add(smallHeadingChapters);

    Callable<ChapterPosition> twoWhitelinesChapterPositions =
        new SimpleWhitelinesChapterAnalyzer(lines);
    twoWhitelinesChapters = executor.submit(twoWhitelinesChapterPositions);
    activeChapterDetections.add(twoWhitelinesChapters);

    Callable<ChapterPosition> noChapterPositions = new FullTextChapterAnalyzer(lines);
    noChapters = executor.submit(noChapterPositions);
    activeChapterDetections.add(noChapters);

  }


  private void stopChapterDetection() {
    for (Future<ChapterPosition> chapterDetection : activeChapterDetections) {
      chapterDetection.cancel(true);
    }
  }

  private ChapterPosition chooseChapterPositions() {
    ChapterPosition result = null;
    try {

      result = markedHeadingChapters.get();
      if (fulfillsMarkedHeadingConditions(result)) {
        stopChapterDetection();
      } else {

        AdvancedBigHeadingChapterAnalyzer advancedBigHeadingChapterPositions =
            new AdvancedBigHeadingChapterAnalyzer(lines, bigHeadingChapters.get());
        advancedBigHeadingChapters = executor.submit(advancedBigHeadingChapterPositions);
        activeChapterDetections.add(advancedBigHeadingChapters);
        result = advancedBigHeadingChapters.get();
        smallHeadingsPercentage = advancedBigHeadingChapterPositions.getSmallHeadingsPercentage();


        if (fulfillsAdvancedBigHeadingConditions(result)) {
          stopChapterDetection();
        } else {

          result = bigHeadingChapters.get();
          if (fulfillsBigHeadingConditions(result)) {
            stopChapterDetection();
          } else {

            result = smallHeadingChapters.get();
            if (fulfillsSmallHeadingConditions(result)) {
              stopChapterDetection();
            } else {

              result = twoWhitelinesChapters.get();
              if (fulfillsTwoWhitelinesConditions(result)) {
                stopChapterDetection();
              } else {
                result = noChapters.get();
              }
            }
          }
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return result;
  }

  private boolean fulfillsMarkedHeadingConditions(ChapterPosition markedHeadingPositions) {
    return markedHeadingPositions.size() > 0;
  }

  private boolean fulfillsBigHeadingConditions(ChapterPosition bigHeadingPositions) {
    return bigHeadingPositions.size() >= 1 && !hasHugeChapter(bigHeadingPositions);
  }

  private boolean fulfillsAdvancedBigHeadingConditions(ChapterPosition advancedBigHeadingPositions) {
    boolean enoughSmallHeadings = smallHeadingsPercentage > MINIMUMSMALLHEADINGSPERCENTAGE;
    return fulfillsBigHeadingConditions(advancedBigHeadingPositions) && enoughSmallHeadings;
  }

  private boolean fulfillsSmallHeadingConditions(ChapterPosition smallHeadingPositions) {
    return smallHeadingPositions.size() >= 1 && !hasHugeChapter(smallHeadingPositions);
  }

  private boolean fulfillsTwoWhitelinesConditions(ChapterPosition twoWhitelinesPositions) {
    return twoWhitelinesPositions.size() >= 1 && !hasHugeChapter(twoWhitelinesPositions);
  }

  private boolean hasHugeChapter(ChapterPosition positions) {
    boolean hasHugeChapter = false;
    for (int chapterNumber = positions.size(); chapterNumber >= 1; chapterNumber--) {
      int chapterTextStart = positions.getStartOfText(chapterNumber);
      int chapterTextEnd = positions.getEndOfText(chapterNumber);
      int chapterLength = chapterTextEnd - chapterTextStart;
      if (chapterLength >= HUGECHAPTERPERCENTAGE * lines.size()) {
        hasHugeChapter = true;
      }
    }
    return hasHugeChapter;
  }
}
