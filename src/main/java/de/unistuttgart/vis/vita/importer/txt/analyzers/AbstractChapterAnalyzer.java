package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineType;

/**
 * Implements Callable returning ChapterPosition.<br> <br>
 *
 * The Chapter Analyzer takes the text Lines of an eBook and returns the Positions of the found
 * Chapters. The Abstract Chapter Analyzer itself provides some protected auxiliary methods and
 * attributes. To implement a concrete Chapter Analyzer Rule you have to extend this class and
 * implement at least the useRule()-method.
 */
public abstract class AbstractChapterAnalyzer implements Callable<ChapterPosition> {

  // the lines to analyze, should not be modified.
  protected final List<Line> chapterArea;

  protected int minimumChapterSize = 200;
  protected Set<LineType> skipTags = new HashSet<>();
  protected ChapterPosition chapterPositions = new ChapterPosition();

  /**
   * Initialize the Abstract Chapter Analyzer and set the lines to analyze.
   *
   * @param chapterArea ArrayList of Line - The lines in which the Chapters are.
   * @throws IllegalArgumentException If input is null.
   */
  public AbstractChapterAnalyzer(List<Line> chapterArea) {
    if (chapterArea == null) {
      throw new IllegalArgumentException("chapterArea must not be null");
    }
    this.chapterArea = chapterArea;
    createSkipTags();
  }

  @Override
  public ChapterPosition call() {
    return useRule();
  }

  /**
   * Get the line index at which the analysis begins.
   *
   * @return int - index at which analysis begins. The value is a valid index of the list of lines.
   */
  public abstract int getStartOfAnalysis();

  /**
   * The Detection Rule which builds the ChapterPosition.
   *
   * @return ChapterPosition - The positions of the detected chapters.
   */
  protected abstract ChapterPosition useRule();

  /**
   * Sets the Tags which mark the beginning of a clause to skip.
   */
  protected void createSkipTags() {
    skipTags.add(LineType.PREFACE);
    skipTags.add(LineType.TABLEOFCONTENTS);
  }

  /**
   * Gets the start of the analysis when the clauses behind a skip tag and everything before should
   * be skipped. To avoid the whole text being skipped, a position in the second half of the text
   * will be recognized as error and 0 will be returned.
   *
   * @return int - The found start position for analysis.
   */
  protected int getStartPosition() {
    int lastFoundPosition = 0;
    int lineIndex = 0;

    boolean thisLineIsWhite;
    boolean lastLineWasWhite = false;

    // true if the end of the clause to skip is searched
    boolean searchingAreaEnd = false;

    // true if last non whiteline was the skip tag
    boolean behindStartTag = false;

    for (Line line : chapterArea) {
      thisLineIsWhite = line.isType(LineType.WHITELINE);
      behindStartTag = behindStartTag && thisLineIsWhite;
      // Recognize Skip Area Start
      if (line.isType(skipTags)) {
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
      // Preparing next line data
      lastLineWasWhite = line.isType(LineType.WHITELINE);
      lineIndex++;
    }
    // Check if result is in first half of the text
    if (lastFoundPosition > 0.5 * chapterArea.size()) {
      lastFoundPosition = 0;
    }
    return lastFoundPosition;
  }

  /**
   * Detects the beginning of a Chapter everytime the given LineType is found or everytime the given
   * LineType is NOT found.
   *
   * @param findThisType  - Boolean - true: the type marks the start of the Chapter. false: all
   *                      other types mark the start of the Chapter.
   * @param type          LineType - The type of the Line.
   * @param startPosition int - Line index at which the analysis should start.
   * @return ChapterPosition - Contains positions of all found simple chapters.
   */
  protected ChapterPosition detectSimpleChapters(Boolean findThisType, LineType type,
                                                 int startPosition) {
    int startOfHeading;
    int endOfText = -1;
    int nextPosition;

    // initialize position; both -1 if there is none.
    startOfHeading = getNextPosition(findThisType, type, startPosition);
    nextPosition = getNextPosition(findThisType, type, startOfHeading + 1);

    // build Chapters until nextPosition has invalid value.
    while (startOfHeading < nextPosition) {
      endOfText = nextPosition - 1;
      this.chapterPositions.addChapter(startOfHeading,
                                       getSimpleStartOfText(startOfHeading, endOfText),
                                       endOfText);

      // set data for next chapter
      startOfHeading = nextPosition;
      nextPosition = getNextPosition(findThisType, type, nextPosition + 1);
    }
    // add the last chapter, if there is at least one found
    addLastChapter(startOfHeading, getSimpleStartOfText(startOfHeading, endOfText));
    return this.chapterPositions;
  }

  /**
   * This rule assures that all headings of the given chapters have at least one Whiteline after the
   * found heading. If not, the Chapter will be attached to the Chapter before or deleted if there
   * is no chapter before.
   *
   * @param extendHeading Boolean - If attached and chapter before has empty text.. True: the new
   *                      heading will contain the both headings. False: the new heading will only
   *                      contain the heading of the chapter before.
   */
  protected void useWhitelineAfterRule(Boolean extendHeading) {
    for (int chapterNumber = this.chapterPositions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = this.chapterPositions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber - 1);
      int thisChapterBeginning = this.chapterPositions.getStartOfHeading(chapterNumber);
      int thisChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = this.chapterPositions.getEndOfText(chapterNumber);
      int textBorder;

      boolean someWhitelinesAfter =
          (getNextPosition(false, LineType.WHITELINE, thisChapterBeginning + 1)
           - thisChapterBeginning) > 1;

      if (!someWhitelinesAfter) {
        if (isEmptyChapter(chapterNumber - 1) && extendHeading) {
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        this.chapterPositions.changeChapterData(chapterNumber - 1, beforeChapterBeginning,
                                                textBorder, thisChapterTextEnd);
        this.chapterPositions.deleteChapter(chapterNumber);
      }
    }
    // special case: if first chapter does not fit the rule, it must be deleted
    if (!this.chapterPositions.isEmpty()) {
      int firstChapterBeginning = this.chapterPositions.getStartOfHeading(1);
      boolean someWhitelinesAfter =
          (getNextPosition(false, LineType.WHITELINE, firstChapterBeginning + 1)
           - firstChapterBeginning) > 1;
      if (!someWhitelinesAfter) {
        this.chapterPositions.deleteChapter(1);
      }
    }
  }

  /**
   * This rule assures that for the given heading at least two Whitelines are in front. Exceptions
   * are at the beginning of the text analysis.
   *
   * @param startHeading int - start of the chapter's heading.
   * @return Boolean - true: Chapter fits the rule. false: Chapter does not fit the rule.
   */
  protected boolean fitsTwoWhitelinesBeforeRule(int startHeading) {
    boolean chapterFits;
    if (startHeading - getStartOfAnalysis() >= 2) {
      chapterFits =
          chapterArea.get(startHeading - 1).isType(LineType.WHITELINE)
          && chapterArea.get(startHeading - 2).isType(LineType.WHITELINE);
    } else if (startHeading - getStartOfAnalysis() == 1) {
      chapterFits = chapterArea.get(startHeading - 1).isType(LineType.WHITELINE);
    } else {
      chapterFits = true;
    }
    return chapterFits;
  }

  /**
   * This rule assures that for all given chapters at least two Whitelines are in front of the
   * heading. If this is not the case, it will be attached to the Chapter before or deleted if there
   * is no chapter before.
   *
   * @param extendHeading Boolean - If attached and chapter before has empty text.. True: the new
   *                      heading will contain the both headings. False: the new heading will only
   *                      contain the heading of the chapter before.
   */
  protected void useTwoWhitelinesBeforeRule(Boolean extendHeading) {
    for (int chapterNumber = this.chapterPositions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = this.chapterPositions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber - 1);
      int thisChapterBeginning = this.chapterPositions.getStartOfHeading(chapterNumber);
      int thisChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = this.chapterPositions.getEndOfText(chapterNumber);
      int textBorder;

      if (!fitsTwoWhitelinesBeforeRule(thisChapterBeginning)) {
        if (isEmptyChapter(chapterNumber - 1) && extendHeading) {
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        this.chapterPositions.changeChapterData(chapterNumber - 1, beforeChapterBeginning,
                                                textBorder,
                                                thisChapterTextEnd);
        this.chapterPositions.deleteChapter(chapterNumber);
      }
    }
    // special case: if first chapter does not fit the rule, it must be deleted
    int firstChapter = 1;
    if (!this.chapterPositions.isEmpty()
        && !fitsTwoWhitelinesBeforeRule(this.chapterPositions.getStartOfHeading(firstChapter))) {
      this.chapterPositions.deleteChapter(firstChapter);
    }
  }


  /**
   * This rule assures that there are no empty chapters. An empty chapter's text only contains
   * Whitelines. If there is an empty chapter, the chapter behind will be attached to the Chapter.
   * If in the end the last chapter or the first chapter is empty, they will be deleted.
   *
   * @param extendHeading Boolean - If attached.. True: the new heading will contain the text of the
   *                      chapter before and the old headings. False: the new heading will only
   *                      contain the heading of the chapter before, the rest will be added to the
   *                      text.
   */
  protected void useEmptyChapterRule(Boolean extendHeading) {
    for (int chapterNumber = this.chapterPositions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = this.chapterPositions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber - 1);
      int thisChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber);
      int thisChapterTextEnd = this.chapterPositions.getEndOfText(chapterNumber);
      int textBorder;

      if (isEmptyChapter(chapterNumber - 1)) {
        if (extendHeading) {
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        this.chapterPositions.changeChapterData(chapterNumber - 1, beforeChapterBeginning,
                                                textBorder,
                                                thisChapterTextEnd);
        this.chapterPositions.deleteChapter(chapterNumber);
      }
    }

    // special case: if last chapter is empty, it must be deleted.
    int lastChapter = this.chapterPositions.size();
    if (!this.chapterPositions.isEmpty() && isEmptyChapter(lastChapter)) {
      this.chapterPositions.deleteChapter(lastChapter);
    }

    // special case: if first chapter is empty, it must be deleted.
    int firstChapter = 1;
    if (!this.chapterPositions.isEmpty() && isEmptyChapter(firstChapter)) {
      this.chapterPositions.deleteChapter(firstChapter);
    }
  }

  /**
   * First all little chapters at the beginning will be deleted until there is a big chapter. Then
   * chapters with a very short text will be attached to the chapter before. If there is no chapter
   * before, the chapter will be deleted.
   *
   * @param minimumLength - The minimum amount of characters a chapter should have. This includes
   *                      invisible characters in lines which are not a Whiteline.
   * @param extendHeading Boolean - If attached.. True: the new heading will contain the text of the
   *                      chapter before and the old headings. False: the new heading will only
   *                      contain the heading of the chapter before, the rest will be added to the
   *                      text.
   */
  protected void useLittleChapterRule(int minimumLength,
                                      boolean extendHeading) {
    deleteLittleChaptersAtTheBeginning(minimumLength);
    for (int chapterNumber = this.chapterPositions.size(); chapterNumber >= 2; chapterNumber--) {
      int beforeChapterBeginning = this.chapterPositions.getStartOfHeading(chapterNumber - 1);
      int beforeChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber - 1);
      int thisChapterTextEnd = this.chapterPositions.getEndOfText(chapterNumber);
      int thisChapterTextStart = this.chapterPositions.getStartOfText(chapterNumber);
      int textBorder;

      if (computeTextLength(chapterNumber) < minimumLength) {
        if (isEmptyChapter(chapterNumber - 1) && extendHeading) {
          textBorder = thisChapterTextStart;
        } else {
          textBorder = beforeChapterTextStart;
        }
        this.chapterPositions.changeChapterData(chapterNumber - 1, beforeChapterBeginning,
                                                textBorder,
                                                thisChapterTextEnd);
        this.chapterPositions.deleteChapter(chapterNumber);
      }
    }

    // special case: if first chapter is nearly empty, it must be deleted.
    int firstChapter = 1;
    if (!this.chapterPositions.isEmpty() && (computeTextLength(firstChapter) < minimumLength)) {
      this.chapterPositions.deleteChapter(firstChapter);
    }
  }

  /**
   * Checks if the Chapter's text contains only Whitelines.
   *
   * @param chapterNumber int - The index of the Chapter, should be between 1 and Chapter's size.
   * @return boolean - true: the Chapter's text contains only Whitelines. false: there are other
   * Linetypes.
   */
  protected boolean isEmptyChapter(int chapterNumber) {
    int chapterBeginning = this.chapterPositions.getStartOfHeading(chapterNumber);
    int chapterTextStart = this.chapterPositions.getStartOfText(chapterNumber);
    int chapterTextEnd = this.chapterPositions.getEndOfText(chapterNumber);

    return (onlyOneTypeBetween(chapterTextStart - 1, chapterTextEnd + 1, LineType.WHITELINE))
           || ((chapterTextEnd - chapterBeginning) == 0);
  }

  /**
   * From the given start position, the next position of a type will be returned.
   *
   * @param thisType Boolean - true: search for type. false: search for everything else than type.
   * @param type     LineType - The type to search.
   * @param start    int - The number of the line at which the next position should be searched.
   *                 This value must be a valid index of the list of lines.
   * @return int - The next position of the given type OR everything else than the given type,
   * depending on thisType. Will return -1 if nothing is found or start value is not valid.
   */
  protected int getNextPosition(Boolean thisType, LineType type, int start) {
    int nextPosition = -1;
    if (!((start < 0) || (start >= chapterArea.size()))) {
      for (int index = start; index < chapterArea.size(); index++) {
        Line line = chapterArea.get(index);
        if (thisType && line.isType(type)) {
          nextPosition = index;
        } else if (!thisType && !line.isType(type)) {
          nextPosition = index;
        }

        // if- or else-if has to be executed then you can finish.
        if (nextPosition >= start) {
          break;
        }
      }
    }
    return nextPosition;
  }

  /**
   * Counts the length of all non-Whiteline-lines in the Chapter's text.
   *
   * @param chapterNumber int - The index of the Chapter, should be between 1 and Chapter's size.
   * @return int - The sum of the lengths. Invisible Characters are added too.
   */
  protected int computeTextLength(int chapterNumber) {
    int characterCount = 0;
    int firstLine = this.chapterPositions.getStartOfText(chapterNumber);
    int lastLine = this.chapterPositions.getEndOfText(chapterNumber);
    for (int lineNumber = firstLine; lineNumber <= lastLine; lineNumber++) {
      Line line = chapterArea.get(lineNumber);
      if (!line.isType(LineType.WHITELINE)) {
        characterCount += line.getText().length();
      }
    }
    return characterCount;
  }

  /**
   * Checks if the lines between the given borders have the same type.
   *
   * @param start int - Bottom border, excluding this line.
   * @param end   int - Top border, excluding this line.
   * @param type  LineType - The type which the lines should have.
   * @return boolean - true if all the lines between the borders have the given type. Will also
   * return true if no lines are analysed.
   */
  protected boolean onlyOneTypeBetween(int start, int end, LineType type) {
    boolean sameType = true;
    for (int index = start + 1; index < end; index++) {
      Line line = chapterArea.get(index);
      if (!line.isType(type)) {
        sameType = false;
        break;
      }
    }
    return sameType;
  }

  /**
   * From start and end of the Chapter, the start of the text will be computed. It is assumed that
   * the heading is always the first line. When there is only one line, this line will formally be
   * set as text.
   *
   * @param startOfHeading int - the start of the heading is the start of the chapter
   * @param endOfText      int - the end of the text is the end of the chapter
   * @return int - the start of the text
   */
  private int getSimpleStartOfText(int startOfHeading, int endOfText) {
    int startOfText;
    if ((endOfText - startOfHeading) >= 1) {
      startOfText = startOfHeading + 1;
    } else {
      startOfText = startOfHeading;
    }
    return startOfText;
  }

  /**
   * A chapter from the given start to the end of the lines will be created, if the values are
   * valid.
   *
   * @param startOfHeading int - start of the chapter, is valid if the line with this index exists.
   * @param startOfText    int - start of text, is valid if the line with this index exists. A
   *                       number smaller than startOfHeading will be corrected so the first line of
   *                       the chapter is the heading.
   */
  private void addLastChapter(int startOfHeading, int startOfText) {
    int correctStartOfText = startOfText;
    if ((startOfHeading >= 0) && (startOfHeading < chapterArea.size() - 1)) {
      if (correctStartOfText <= startOfHeading) {
        correctStartOfText = startOfHeading + 1;
      }
      if (!(correctStartOfText >= chapterArea.size())) {
        this.chapterPositions
            .addChapter(startOfHeading, correctStartOfText, chapterArea.size() - 1);
      }
    }
  }

  /**
   * Deletes all chapters at the beginning until there is chapter containing more characters than
   * determined by the minimum length.
   *
   * @param minimumLength - The minimum amount of characters a chapter should have. This includes
   *                      invisible characters in lines which are not a Whiteline.
   */
  private void deleteLittleChaptersAtTheBeginning(int minimumLength) {
    while (this.chapterPositions.size() > 1 && computeTextLength(1) < minimumLength) {
      this.chapterPositions.deleteChapter(1);
    }
  }

}
