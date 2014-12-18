package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.HashMap;
import java.util.List;

import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineSubType;

// TODO:
public abstract class AbstractSubtypeExtendedChapterAnalyzer extends AbstractChapterAnalyzer {
  // TODO:
  private static final float MINIMAL_WEAK_OCCURENCE_PERCENTAGE = 0.51f;
  private static final float MINIMAL_STRONG_OCCURENCE_PERCENTAGE = 0.8f;

  private int chapterCount;
  private HashMap<LineSubType, Integer> subTypeMap = new HashMap<LineSubType, Integer>();


  public AbstractSubtypeExtendedChapterAnalyzer(List<Line> chapterArea) {
    super(chapterArea);
    for (LineSubType subType : LineSubType.values()) {
      subTypeMap.put(subType, 0);
    }
    chapterCount = 0;
  }

  protected void useSubtypeReductionRule() {
    chapterCount = this.chapterPositions.size();
    computeSubtypeCount();
    float maxCountPercentage = computeMaxCountPercentage();
    System.out.println("CHAPTER COUNT: "+chapterCount);
    System.out.println("PERCENTAGE : " + maxCountPercentage);
    System.out.println(this.subTypeMap);
    if (useWeakReduction(maxCountPercentage)) {
      if(useStrongReduction(maxCountPercentage)){
        LineSubType maxSubType = GetSubTypeWithHighestCount();
        reduceChapterPosition(true, maxSubType);
      }else{
        reduceChapterPosition(false, null);
      }
    }
  }

  private void reduceChapterPosition(boolean useStrongCondition, LineSubType subType) {
    int chapterNumber = 1;
    while (chapterNumber <= this.chapterPositions.size()) {
      boolean changedChapterPosition = false;
      int headingStartIncl = this.chapterPositions.getStartOfHeading(chapterNumber);
      int headingEndExcl = this.chapterPositions.getStartOfText(chapterNumber);
      Iterable<Line> headingLines = this.chapterArea.subList(headingStartIncl, headingEndExcl);
      boolean isBadChapter = false;
      if(useStrongCondition){
        //System.out.println("Strong");
        isBadChapter = !containsSubType(headingLines,subType);
      }else{
        //System.out.println("weak");
        isBadChapter = !linesHaveSubtype(headingLines);
      }
      if (isBadChapter) {
        // Do not delete first chapter
        if(chapterNumber != 1){
          changedChapterPosition = true;
          // delete chapter and add as text of previous chapter
          int previousChapterNumber = chapterNumber -1;
          int newEndOfText = this.chapterPositions.getEndOfText(chapterNumber);
          int newStartOfText = this.chapterPositions.getStartOfText(previousChapterNumber);
          int newStartOfHeading = this.chapterPositions.getStartOfHeading(previousChapterNumber);
          this.chapterPositions.deleteChapter(chapterNumber);
          this.chapterPositions.changeChapterData(previousChapterNumber, newStartOfHeading, newStartOfText, newEndOfText);
        }
      }
      if(!changedChapterPosition){
        chapterNumber++;
      }
    }
  }

  
  private boolean linesHaveSubtype(Iterable<Line> lines){
    boolean found = false;
    for (Line line : lines) {
      if (line.hasSubType()) {
        found = true;
        break;
      }
    }
    return found;
  }
  
  
  private LineSubType GetSubTypeWithHighestCount() {
    int maxCount = 0;
    LineSubType maxType = null;
    for (LineSubType subType : subTypeMap.keySet()) {
      if (subTypeMap.get(subType) >= maxCount) {
        maxCount = subTypeMap.get(subType);
        maxType = subType;
      }
    }
    return maxType;
  }

  private float computeMaxCountPercentage() {
    float maxCountPercentage;
    int maxCount = 0;
    for (int subTypeCount : subTypeMap.values()) {
      if (subTypeCount > maxCount) {
        maxCount = subTypeCount;
      }
    }
    if (chapterCount > 0) {
      System.out.println("MAXCOUNT: " + maxCount);
      maxCountPercentage = (1.0f * maxCount) / (1.0f * chapterCount);
    } else {
      maxCountPercentage = 0;
    }
    return maxCountPercentage;
  }

  private boolean useWeakReduction(float maxCountPercentage) {
    return (MINIMAL_WEAK_OCCURENCE_PERCENTAGE <= maxCountPercentage);
  }
  
  private boolean useStrongReduction(float maxCountPercentage){
    return (MINIMAL_STRONG_OCCURENCE_PERCENTAGE <= maxCountPercentage);
  }

  private void computeSubtypeCount() {
    for (int chapterNumber = 1; chapterNumber <= chapterCount; chapterNumber++) {
      if (this.chapterPositions.hasHeading(chapterNumber)) {
        int headingStartIncl = this.chapterPositions.getStartOfHeading(chapterNumber);
        int headingEndExcl = this.chapterPositions.getStartOfText(chapterNumber);
        computeChapterSubtypeCount(this.chapterArea.subList(headingStartIncl, headingEndExcl));
      }
    }
  }

  private void computeChapterSubtypeCount(Iterable<Line> lines) {
    for (LineSubType subType : subTypeMap.keySet()) {
      for (Line line : lines) {
        if (line.hasSubType() && line.isSubType(subType)) {
          subTypeMap.put(subType, subTypeMap.get(subType) + 1);
          break;
        }
      }
    }
  }

  private boolean containsSubType(Iterable<Line> lines, LineSubType subType) {
    boolean found = false;
    for (Line line : lines) {
      if (line.hasSubType() && line.isSubType(subType)) {
        found = true;
        break;
      }
    }
    return found;
  }

}
