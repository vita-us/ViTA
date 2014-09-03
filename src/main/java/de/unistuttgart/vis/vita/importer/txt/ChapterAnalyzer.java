package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.HashSet;

public class ChapterAnalyzer {
  class ChapterPosition {
    private ArrayList<Integer> startList = new ArrayList<Integer>();
    private ArrayList<Integer> endList = new ArrayList<Integer>();
    private ArrayList<Integer> headingStartList = new ArrayList<Integer>();

    @SuppressWarnings("unchecked")
    @Override
    public ChapterPosition clone() {
      ChapterPosition chapterPosition = new ChapterPosition();
      chapterPosition.startList = (ArrayList<Integer>) this.startList.clone();
      chapterPosition.endList = (ArrayList<Integer>) this.endList.clone();
      chapterPosition.headingStartList = (ArrayList<Integer>) this.headingStartList.clone();
      return chapterPosition;
    }

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

  private final int MINIMUMCHAPTERSIZE = 200;
  private final float MINIMUMSMALLHEADINGSPERCENTAGE = 0.4f;
  private final float HUGECHAPTERPERCENTAGE = 0.8f;
  private float smallHeadingsPercentage;
  private final ArrayList<Line> chapterArea;
  private HashSet<LineType> skipTags = new HashSet<LineType>();

  private ChapterPosition markedHeadingChapters;
  private ChapterPosition bigHeadingChapters;
  private ChapterPosition advancedBigHeadingChapters;
  private ChapterPosition smallHeadingChapters;
  private ChapterPosition twoWhitelinesChapters;
  private ChapterPosition noChapters;
  private ChapterPosition result;

  protected ChapterAnalyzer(ArrayList<Line> chapterArea) throws IllegalArgumentException {
    if (chapterArea == null) {
      throw new IllegalArgumentException("chapterArea must not be null");
    }
    this.chapterArea = chapterArea;
    createSkipTags();

    markedHeadingChapters = useMarkedHeaderRules();
    bigHeadingChapters = useBigHeaderRules();
    advancedBigHeadingChapters = useAdvancedBigHeaderRules(bigHeadingChapters);
    smallHeadingChapters = useSmallHeaderRules();
    twoWhitelinesChapters = useTwoWhitelinesChapterRule();
    noChapters = useNoChapterRules();

    result = chooseChapterPositions();

  }

  private ChapterPosition chooseChapterPositions() {
    ChapterPosition result = noChapters;

    if (fulfillsTwoWhitelinesConditions(twoWhitelinesChapters)) {
      result = twoWhitelinesChapters;
    }
    if (fulfillsSmallHeadingConditions(smallHeadingChapters)) {
      result = smallHeadingChapters;
    }
    if (fulfillsBigHeadingConditions(bigHeadingChapters)) {
      result = bigHeadingChapters;
    }
    if (fulfillsAdvancedBigHeadingConditions(advancedBigHeadingChapters)) {
      result = advancedBigHeadingChapters;
    }
    if (fulfillsMarkedHeadingConditions(markedHeadingChapters)) {
      result = markedHeadingChapters;
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
      if (chapterLength >= HUGECHAPTERPERCENTAGE * chapterArea.size()) {
        hasHugeChapter = true;
      }
    }
    return hasHugeChapter;
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

  private ChapterPosition detectSimpleChapters(Boolean findThisType, LineType type,
      int startPosition) {
    ChapterPosition positions = new ChapterPosition();
    int startOfHeading = -1;
    int endOfText = -1;
    int nextPosition = -1;


    startOfHeading = getNextPosition(findThisType, type, startPosition);
    nextPosition = getNextPosition(findThisType, type, startOfHeading + 1);
    while (startOfHeading < nextPosition) {
      endOfText = nextPosition - 1;
      if (endOfText >= chapterArea.size()) {
        break;
      }
      positions.addChapter(startOfHeading, getSimpleStartOfText(startOfHeading, endOfText),
          endOfText);
      startOfHeading = nextPosition;
      nextPosition = getNextPosition(findThisType, type, nextPosition + 1);
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
    ChapterPosition positions = detectSimpleChapters(true, LineType.MARKEDHEADING, 0);
    useEmptyChapterRule(positions, true);
    return positions;
  }

  protected ChapterPosition useBigHeaderRules() {
    ChapterPosition positions = detectSimpleChapters(true, LineType.BIGHEADING, getStartPosition());
    useTwoWhitelinesBeforeRule(positions, true);
    useEmptyChapterRule(positions, true);
    useLittleChapterRule(positions, MINIMUMCHAPTERSIZE);
    return positions;
  }

  protected ChapterPosition useSmallHeaderRules() {
    ChapterPosition positions =
        detectSimpleChapters(true, LineType.SMALLHEADING, getStartPosition());
    useTwoWhitelinesBeforeRule(positions, false);
    useWhitelineAfterRule(positions, false);
    useEmptyChapterRule(positions, false);
    useLittleChapterRule(positions, MINIMUMCHAPTERSIZE);
    return positions;
  }

  protected ChapterPosition useAdvancedBigHeaderRules(ChapterPosition bigHeaderPositions) {
    ChapterPosition newPositions = bigHeaderPositions.clone();
    int numberOfAddedSmallHeadings = addOneSmallHeadingRule(newPositions);
    int oldNumberOfChapters = newPositions.size();
    useEmptyChapterRule(newPositions, false);
    int newNumberOfChapters = newPositions.size();
    numberOfAddedSmallHeadings =
        numberOfAddedSmallHeadings - (oldNumberOfChapters - newNumberOfChapters);
    smallHeadingsPercentage = numberOfAddedSmallHeadings * (1.0f / newNumberOfChapters);
    return newPositions;
  }

  protected ChapterPosition useTwoWhitelinesChapterRule() {
    ChapterPosition positions = detectSimpleChapters(false, LineType.WHITELINE, getStartPosition());
    useTwoWhitelinesBeforeRule(positions, false);
    useEmptyChapterRule(positions, false);
    for (int chapterNumber = 1; chapterNumber <= positions.size(); chapterNumber++) {
      int startOfHeading = positions.getStartOfHeading(chapterNumber);
      int endOfText = positions.getEndOfText(chapterNumber);
      positions.changeChapterData(chapterNumber, startOfHeading, startOfHeading, endOfText);
    }
    return positions;
  }

  protected ChapterPosition useNoChapterRules() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 0, chapterArea.size());
    return positions;
  }

  protected void useWhitelineAfterRule(ChapterPosition positions, Boolean extendHeading) {
    for (int chapterNumber = positions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = positions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = positions.getStartOfText(chapterNumber - 1);
      int beforeChapterTextEnd = positions.getEndOfText(chapterNumber - 1);
      int thisChapterBeginning = positions.getStartOfHeading(chapterNumber);
      int thisChapterTextStart = positions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = positions.getEndOfText(chapterNumber);
      int textBorder;
      boolean someWhitelinesAfter =
          (getNextPosition(false, LineType.WHITELINE, thisChapterBeginning + 1) - thisChapterBeginning) > 1;
      if (!someWhitelinesAfter) {
        if (extendHeading) {
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        positions.changeChapterData(chapterNumber - 1, beforeChapterBeginning, textBorder,
            thisChapterTextEnd);
        positions.deleteChapter(chapterNumber);
      }
    }
    if (positions.size() >= 1) {
      int firstChapterBeginning = positions.getStartOfHeading(1);
      boolean someWhitelinesAfter =
          (getNextPosition(false, LineType.WHITELINE, firstChapterBeginning + 1) - firstChapterBeginning) > 1;
      if (!someWhitelinesAfter) {
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

  protected void useEmptyChapterRule(ChapterPosition positions, Boolean extendHeading) {
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
      if (emptyChapter) {
        if (extendHeading) {
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        positions.changeChapterData(chapterNumber - 1, beforeChapterBeginning, textBorder,
            thisChapterTextEnd);
        positions.deleteChapter(chapterNumber);
      }
    }
    int lastChapter = positions.size();
    if (positions.size() > 0
        && !fitsTwoWhitelinesBeforeRule(positions.getStartOfHeading(lastChapter))) {
      positions.deleteChapter(lastChapter);
    }
    int firstChapter = 1;
    if (positions.size() >= 1) {
      int firstChapterTextStart = positions.getStartOfText(firstChapter);
      int firstChapterTextEnd = positions.getEndOfText(firstChapter);
      boolean emptyChapter =
          onlyOneTypeBetween(firstChapterTextStart - 1, firstChapterTextEnd + 1, LineType.WHITELINE);
      if (emptyChapter) {
        positions.deleteChapter(firstChapter);
      }
    }
  }

  protected void useTwoWhitelinesBeforeRule(ChapterPosition positions, Boolean extendHeading) {
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
      if (!fitsTwoWhitelinesBeforeRule(positions.getStartOfHeading(chapterNumber))) {
        if (emptyChapter && extendHeading) {
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        positions.changeChapterData(chapterNumber - 1, beforeChapterBeginning, textBorder,
            thisChapterTextEnd);
        positions.deleteChapter(chapterNumber);
      }
    }
    int firstChapter = 1;
    if (positions.size() >= 1) {
      if (!fitsTwoWhitelinesBeforeRule(positions.getStartOfHeading(firstChapter))) {
        positions.deleteChapter(firstChapter);
      }
    }
  }

  private int countCharacters(int chapterNumber, ChapterPosition positions) {
    int characterCount = 0;
    int firstLine = positions.getStartOfText(chapterNumber);
    int lastLine = positions.getEndOfText(chapterNumber);
    for (int lineNumber = firstLine; lineNumber <= lastLine; lineNumber++) {
      Line line = chapterArea.get(lineNumber);
      if (line.getType() != LineType.WHITELINE) {
        characterCount += line.getText().length();
      }
    }
    return characterCount;
  }

  protected void useLittleChapterRule(ChapterPosition positions, int minimumLength) {
    for (int chapterNumber = positions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = positions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = positions.getStartOfText(chapterNumber - 1);
      int beforeChapterTextEnd = positions.getEndOfText(chapterNumber - 1);
      int thisChapterBeginning = positions.getStartOfHeading(chapterNumber);
      int thisChapterTextStart = positions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = positions.getEndOfText(chapterNumber);
      int characterCount = countCharacters(chapterNumber, positions);
      if (characterCount < minimumLength) {
        positions.changeChapterData(chapterNumber - 1, beforeChapterBeginning,
            beforeChapterTextStart, thisChapterTextEnd);
        positions.deleteChapter(chapterNumber);
      }
    }
    int firstChapter = 1;
    if (positions.size() >= 1) {
      int characterCount = countCharacters(firstChapter, positions);
      if (characterCount < minimumLength) {
        positions.deleteChapter(firstChapter);
      }
    }
  }

  protected int addOneSmallHeadingRule(ChapterPosition positions) {
    int numberOfAddedSmallHeadings = 0;
    for (int chapterNumber = 1; chapterNumber <= positions.size(); chapterNumber++) {
      int startOfHeading = positions.getStartOfHeading(chapterNumber);
      int startOfText = positions.getStartOfText(chapterNumber);
      int endOfText = positions.getEndOfText(chapterNumber);
      int nextSmallHeading = getNextPosition(true, LineType.SMALLHEADING, startOfText);
      if (nextSmallHeading < endOfText && nextSmallHeading >= startOfText) {
        boolean someWhitelinesAfter =
            (getNextPosition(false, LineType.WHITELINE, nextSmallHeading + 1) - nextSmallHeading) > 1;
        if (onlyOneTypeBetween(startOfText - 1, nextSmallHeading, LineType.WHITELINE)
            && !onlyOneTypeBetween(nextSmallHeading, endOfText + 1, LineType.WHITELINE)
            && someWhitelinesAfter) {
          positions.changeChapterData(chapterNumber, startOfHeading, nextSmallHeading + 1,
              endOfText);
          numberOfAddedSmallHeadings++;
        }
      }
    }
    return numberOfAddedSmallHeadings;
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

  // TODO: FÜLLEN
  protected void getResult() {

  }

  // TODO: ENTFERNEN
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
      for (int i = 1; markedHeadingChapters.size() >= i; i++) {
        if (markedHeadingChapters.getStartOfHeading(i) <= lineNumber
            && markedHeadingChapters.getStartOfText(i) > lineNumber) {
          result = result + "M ";
        }
      }
      for (int i = 1; bigHeadingChapters.size() >= i; i++) {
        if (bigHeadingChapters.getStartOfHeading(i) <= lineNumber
            && bigHeadingChapters.getStartOfText(i) > lineNumber) {
          result = result + "B ";
        }
      }
      for (int i = 1; smallHeadingChapters.size() >= i; i++) {
        if (smallHeadingChapters.getStartOfHeading(i) <= lineNumber
            && smallHeadingChapters.getStartOfText(i) > lineNumber) {
          result = result + "S ";
        }
      }
      for (int i = 1; advancedBigHeadingChapters.size() >= i; i++) {
        if (advancedBigHeadingChapters.getStartOfHeading(i) <= lineNumber
            && advancedBigHeadingChapters.getStartOfText(i) > lineNumber) {
          result = result + "A ";
        }
      }
      for (int i = 1; twoWhitelinesChapters.size() >= i; i++) {
        if (twoWhitelinesChapters.getStartOfHeading(i) <= lineNumber
            && twoWhitelinesChapters.getStartOfText(i) >= lineNumber) {
          result = result + "W ";
        }
      }
      for (int i = 1; this.result.size() >= i; i++) {
        if (this.result.getStartOfHeading(i) <= lineNumber
            && this.result.getStartOfText(i) > lineNumber) {
          result = result + "R ";
        }
      }

      result = result + "      ";
      result = result + line.getType() + "      " + line.getText() + "\r\n";
      lineNumber++;
    }
    list.add(result);
    return list;
  }

}
