package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;

public abstract class AbstractChapterAnalyzer implements Callable<ChapterPosition> {


  protected int minimumChapterSize = 200;
  protected final ArrayList<Line> chapterArea;
  protected HashSet<LineType> skipTags = new HashSet<LineType>();
  protected ChapterPosition chapterPositions;

  public AbstractChapterAnalyzer(ArrayList<Line> chapterArea) throws IllegalArgumentException {
    if (chapterArea == null) {
      throw new IllegalArgumentException("chapterArea must not be null");
    }
    this.chapterArea = chapterArea;
    createSkipTags();
  }

  protected void createSkipTags() {
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

  protected ChapterPosition detectSimpleChapters(Boolean findThisType, LineType type,
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

  protected int countCharacters(int chapterNumber, ChapterPosition positions) {
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

  protected abstract ChapterPosition useRule();

  @Override
  public ChapterPosition call() {
    return useRule();
  }



}
