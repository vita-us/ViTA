package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.HashSet;

public class ChapterAnalyzer {
  class ChapterPosition {
    private ArrayList<Integer> startList = new ArrayList<Integer>();
    private ArrayList<Integer> endList = new ArrayList<Integer>();
    private ArrayList<Integer> headingStartList = new ArrayList<Integer>();

    public void addChapter(int headingStart, int start, int end) {
      if (!((headingStart <= start) && (start <= end))) {
        System.out.println(headingStart + " " + start + " " + end);
        throw new IllegalStateException(); // TODO
      }
      this.headingStartList.add(headingStart);
      this.startList.add(start);
      this.endList.add(end);
    }

    public void deleteChapter(int chapterNumber) {
      if (chapterNumber <= 0) {
        throw new IllegalArgumentException("chapterNumber should not be <= 0");
      }
      if (chapterNumber > startList.size()) {
        throw new IndexOutOfBoundsException("Index is out of range");
      }
      headingStartList.remove(chapterNumber - 1);
      startList.remove(chapterNumber - 1);
      endList.remove(chapterNumber - 1);
    }

    public void changeChapterData(int chapterNumber, int startOfHeading, int startOfText,
        int endOfText) {
      if (chapterNumber <= 0) {
        throw new IllegalArgumentException("chapterNumber should not be <= 0");
      }
      if (chapterNumber > startList.size()) {
        throw new IndexOutOfBoundsException("Index is out of range");
      }
      if (!((startOfHeading <= startOfText) && (startOfText <= endOfText))) {
        throw new IllegalStateException(); // TODO
      }
      headingStartList.set(chapterNumber - 1, startOfHeading);
      startList.set(chapterNumber - 1, startOfText);
      endList.set(chapterNumber - 1, endOfText);
    }

    public int getStartOfHeading(int chapterNumber) {
      if (chapterNumber <= 0) {
        throw new IllegalArgumentException("chapterNumber should not be <= 0");
      }
      if (chapterNumber > startList.size()) {
        throw new IndexOutOfBoundsException("Index is out of range");
      }
      return headingStartList.get(chapterNumber - 1);
    }

    public int getStartOfText(int chapterNumber) {
      if (chapterNumber <= 0) {
        throw new IllegalArgumentException("chapterNumber should not be <= 0");
      }
      if (chapterNumber > startList.size()) {
        throw new IndexOutOfBoundsException("Index is out of range");
      }
      return startList.get(chapterNumber - 1);
    }

    public int getEndOfText(int chapterNumber) {
      if (chapterNumber <= 0) {
        throw new IllegalArgumentException("chapterNumber should not be <= 0");
      }
      if (chapterNumber > startList.size()) {
        throw new IndexOutOfBoundsException("Index is out of range");
      }
      return endList.get(chapterNumber - 1);
    }

    public void print(int i) {
      System.out.println(headingStartList.get(i) + " " + startList.get(i) + " " + endList.get(i));
    }

    // TODO: End / Start Heading

    public int size() {
      return startList.size();
    }
  }

  private final ArrayList<Line> chapterArea;
  private HashSet<LineType> skipTags = new HashSet<LineType>();

  ChapterPosition pos1;
  ChapterPosition pos2;
  ChapterPosition pos3;

  protected ChapterAnalyzer(ArrayList<Line> chapterArea) throws IllegalArgumentException {
    if (chapterArea == null) {
      throw new IllegalArgumentException("chapterArea must not be null");
    }
    this.chapterArea = chapterArea;
    createSkipTags();
    System.out.println(getStartPosition());


    pos1 = detectSimpleChapters(LineType.MARKEDHEADING);
    pos2 = useBigHeaderRules();
    pos3 = useSmallHeaderRules();
  }

  public ArrayList<String> testOut() {
    System.out.println(" Writing to file ");
    ArrayList<String> list = new ArrayList<String>();
    String result = "";
    int lineNumber = 0;
    for (Line line : chapterArea) {
      if (lineNumber % 1000 == 0) {
        System.out.println(" line:  " + lineNumber + " / " + chapterArea.size());
        list.add(result);
        result = "";
      }
      for (int i = 1; pos1.size() >= i; i++) {
        if (pos1.getStartOfHeading(i) <= lineNumber && pos1.getStartOfText(i) > lineNumber) {
          result = result + "M";
        }
      }
      for (int i = 1; pos2.size() >= i; i++) {
        if (pos2.getStartOfHeading(i) <= lineNumber && pos2.getStartOfText(i) > lineNumber) {
          result = result + "B";
        }
      }
      for (int i = 1; pos3.size() >= i; i++) {
        if (pos3.getStartOfHeading(i) <= lineNumber && pos3.getStartOfText(i) > lineNumber) {
          result = result + "S";
        }
      }
      result = result + "  ";
      result = result + line.getType() + "      " + line.getText() + "\r\n";
      lineNumber++;
    }
    list.add(result);
    return list;
  }

  private void createSkipTags() {
    skipTags.add(LineType.PREFACE);
    skipTags.add(LineType.TABLEOFCONTENTS);
  }

  protected int getStartPosition() {
    int lastFoundPosition = 0;
    int lineIndex = 0;
    boolean searchingAreaEnd = false;
    boolean lastLineWasWhite = false;
    boolean behindStartTag = false;
    for (Line line : chapterArea) {
      boolean thisLineIsWhite = line.getType().equals(LineType.WHITELINE);
      behindStartTag = behindStartTag && thisLineIsWhite;
      // Recognize Skip Area Start
      if (skipTags.contains(line.getType())) {
        searchingAreaEnd = true;
        behindStartTag = true;
        lastFoundPosition = lineIndex;
      }
      // Recognize End of Area with 2 Whitelines, except after skip tag
      if (searchingAreaEnd) {
        if (lastLineWasWhite && thisLineIsWhite && !behindStartTag) {
          searchingAreaEnd = false;
        } else if (!thisLineIsWhite) {
          lastFoundPosition = lineIndex + 1;
        }
      }
      // Prepearing next line data
      lastLineWasWhite = line.getType().equals(LineType.WHITELINE);
      lineIndex++;
    }
    return lastFoundPosition;
  }

  private ChapterPosition detectSimpleChapters(LineType type) {
    ChapterPosition positions = new ChapterPosition();
    int startOfHeading = -1;
    int endOfText = -1;
    int nextPosition = -1;

    startOfHeading = getNextPosition(true, type, getStartPosition());
    nextPosition = getNextPosition(true, type, startOfHeading + 1);
    while (startOfHeading < nextPosition) {
      endOfText = nextPosition - 1;
      if (endOfText >= chapterArea.size()) {
        break;
      }
      positions.addChapter(startOfHeading, getSimpleStartOfText(startOfHeading, endOfText),
          endOfText);
      startOfHeading = nextPosition;
      nextPosition = getNextPosition(true, type, nextPosition + 1);
    }
    addLastChapter(startOfHeading, getSimpleStartOfText(startOfHeading, endOfText), positions);
    return positions;
  }

  protected int getSimpleStartOfText(int startOfHeading, int endOfText) {
    int startOfText;
    if ((endOfText - startOfHeading) >= 1) {
      startOfText = startOfHeading + 1;
    } else {
      startOfText = startOfHeading;
    }
    return startOfText;
  }

  protected void addLastChapter(int startOfHeading, int startOfText, ChapterPosition positions) {
    if ((startOfHeading >= 0) && (startOfHeading < chapterArea.size() - 1)) {
      if (startOfText <= startOfHeading) {
        startOfText = startOfHeading + 1;
      }
      if (!(startOfText >= chapterArea.size())) {
        positions.addChapter(startOfHeading, startOfText, chapterArea.size() - 1);
      }
    }
  }

  protected ChapterPosition useMarkedHeaderRules() {
    ChapterPosition positions = detectSimpleChapters(LineType.MARKEDHEADING);
    for (int chapterNumber = positions.size(); chapterNumber > 1; chapterNumber--) {
      int beforeChapterBeginning = positions.getStartOfHeading(chapterNumber - 1);
      int thisChapterBeginning = positions.getStartOfHeading(chapterNumber);
      int thisChapterTextStart = positions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = positions.getEndOfText(chapterNumber);
      if (onlyOneTypeBetween(beforeChapterBeginning, thisChapterBeginning, LineType.WHITELINE)) {
        positions.changeChapterData(chapterNumber - 1, beforeChapterBeginning,
            thisChapterTextStart, thisChapterTextEnd);
        positions.deleteChapter(chapterNumber);
      }
    }
    return positions;
  }

  protected ChapterPosition useBigHeaderRules() {
    ChapterPosition positions = detectSimpleChapters(LineType.BIGHEADING);
    for (int i = 1; i <= positions.size(); i++) {
      System.out.println("Chapterstart: " + positions.getStartOfHeading(i) + " bis "
          + (positions.getStartOfText(i) - 1) + " ends " + (positions.getEndOfText(i)));
    }
    useTwoWhitelinesBeforeRule(positions);
    System.out.println("TWO LINES:");
    for (int i = 1; i <= positions.size(); i++) {
      System.out.println("Chapterstart: " + positions.getStartOfHeading(i) + " bis "
          + (positions.getStartOfText(i) - 1));
    }
    addOneSmallHeadingRule(positions);
    System.out.println("Small::");
    for (int i = 1; i <= positions.size(); i++) {
      System.out.println("Chapterstart: " + positions.getStartOfHeading(i) + " bis "
          + (positions.getStartOfText(i) - 1));
    }
    return positions;
  }

  protected ChapterPosition useSmallHeaderRules() {
    ChapterPosition positions = detectSimpleChapters(LineType.SMALLHEADING);
    useWhitelinesBeforeAndAfterRule(positions);
    return positions;
  }

  protected void useWhitelinesBeforeAndAfterRule(ChapterPosition positions) {
    for (int chapterNumber = positions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = positions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = positions.getStartOfText(chapterNumber - 1);
      int beforeChapterTextEnd = positions.getEndOfText(chapterNumber - 1);
      int thisChapterBeginning = positions.getStartOfHeading(chapterNumber);
      int thisChapterTextStart = positions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = positions.getEndOfText(chapterNumber);
      boolean someWhitelinesAfter =
          (getNextPosition(false, LineType.WHITELINE, thisChapterBeginning + 1) - thisChapterBeginning) > 1;
      if (!someWhitelinesAfter || !fitsTwoWhitelinesBeforeRule(thisChapterBeginning)) {
        positions.changeChapterData(chapterNumber - 1, beforeChapterBeginning,
            beforeChapterBeginning + 1, thisChapterTextEnd);
        positions.deleteChapter(chapterNumber);
      }
    }
    if (positions.size() >= 1) {
      int firstChapterBeginning = positions.getStartOfHeading(1);
      boolean someWhitelinesAfter =
          (getNextPosition(false, LineType.WHITELINE, firstChapterBeginning + 1) - firstChapterBeginning) > 1;
      if (!someWhitelinesAfter || !fitsTwoWhitelinesBeforeRule(firstChapterBeginning)) {
        positions.deleteChapter(1);
      }
    }

  }


  protected boolean fitsTwoWhitelinesBeforeRule(int startHeading) {
    boolean chapterFits = false;
    if (startHeading - getStartPosition() >= 2) {
      chapterFits =
          chapterArea.get(startHeading - 1).getType().equals(LineType.WHITELINE)
              && chapterArea.get(startHeading - 2).getType().equals(LineType.WHITELINE);
    } else if (startHeading - getStartPosition() == 1) {
      chapterFits = chapterArea.get(startHeading - 1).getType().equals(LineType.WHITELINE);
    } else {
      chapterFits = true;
    }
    return chapterFits;
  }

  protected void useTwoWhitelinesBeforeRule(ChapterPosition positions) {
    for (int chapterNumber = positions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = positions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = positions.getStartOfText(chapterNumber - 1);
      int beforeChapterTextEnd = positions.getEndOfText(chapterNumber - 1);
      int thisChapterBeginning = positions.getStartOfHeading(chapterNumber);
      int thisChapterTextStart = positions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = positions.getEndOfText(chapterNumber);
      int textBorder;
      boolean emptyChapter =
          (onlyOneTypeBetween(beforeChapterTextStart - 1, thisChapterBeginning, LineType.WHITELINE))
              || ((beforeChapterTextEnd - beforeChapterTextStart) == 0);
      System.out.println("THIS IS EMPTY CHAPTER? " + emptyChapter);
      if (!fitsTwoWhitelinesBeforeRule(positions.getStartOfHeading(chapterNumber)) || emptyChapter) {
        System.out.println(" EMPTY CHAPTER: at " + positions.getStartOfHeading(chapterNumber));
        if (emptyChapter) {
          System.out.println("THIS CHAPTER TEXT: " + thisChapterTextStart);
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        System.out.println("BUILD: " + beforeChapterBeginning + " " + textBorder + " "
            + thisChapterTextEnd);
        positions.changeChapterData(chapterNumber - 1, beforeChapterBeginning, textBorder,
            thisChapterTextEnd);
        positions.deleteChapter(chapterNumber);
      }
    }
    if (positions.size() >= 1) {
      int firstChapterTextStart = positions.getStartOfText(1);
      int firstChapterTextEnd = positions.getEndOfText(1);
      boolean emptyChapter =
          onlyOneTypeBetween(firstChapterTextStart - 1, firstChapterTextEnd + 1, LineType.WHITELINE);
      if (!fitsTwoWhitelinesBeforeRule(positions.getStartOfHeading(1)) || emptyChapter) {
        positions.deleteChapter(1);
      }
    }
  }

  protected void addOneSmallHeadingRule(ChapterPosition positions) {
    for (int chapterNumber = 1; chapterNumber <= positions.size(); chapterNumber++) {
      int startOfHeading = positions.getStartOfHeading(chapterNumber);
      int startOfText = positions.getStartOfText(chapterNumber);
      int endOfText = positions.getEndOfText(chapterNumber);
      int nextSmallHeading = getNextPosition(true, LineType.SMALLHEADING, startOfText);
      if (nextSmallHeading < endOfText && nextSmallHeading > startOfText) {
        boolean someWhitelinesAfter =
            (getNextPosition(false, LineType.WHITELINE, nextSmallHeading + 1) - nextSmallHeading) > 1;
        if (onlyOneTypeBetween(startOfText + 1, nextSmallHeading, LineType.WHITELINE)
            && !onlyOneTypeBetween(nextSmallHeading, endOfText + 1, LineType.WHITELINE)
            && someWhitelinesAfter) {
          positions.changeChapterData(chapterNumber, startOfHeading, nextSmallHeading + 1,
              endOfText);
        }
      }
    }
  }

  protected int getNextPosition(Boolean thisType, LineType type, int start) {
    if (!((start < 0) || (start >= chapterArea.size()))) {
      for (int index = start; index < chapterArea.size(); index++) {
        Line line = chapterArea.get(index);
        LineType lineType = line.getType();
        if (thisType) {
          if (type.equals(lineType)) {
            return index;
          }
        } else {
          if (!type.equals(lineType)) {
            return index;
          }
        }
      }
    }
    return -1;

  }

  protected boolean onlyOneTypeBetween(int start, int end, LineType type) {
    boolean sameType = true;
    for (int index = start + 1; index < end; index++) {
      Line line = chapterArea.get(index);
      LineType lineType = line.getType();
      if (!lineType.equals(type)) {
        sameType = false;
        break;
      }
    }
    return sameType;
  }

  protected void getResult() {

  }

}
