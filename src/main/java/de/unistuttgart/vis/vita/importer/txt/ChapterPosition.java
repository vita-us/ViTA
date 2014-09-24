package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;

/**
 * Holds the information of the positions of a chapter's text and title in a List of lines. The
 * ChapterPosition does not know the text to analyze itself.
 */
public class ChapterPosition implements Cloneable {
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

  /**
   * Sets the positions of a new Chapter, it will be the last in the ChapterPosition. Are start and
   * headingStart the same number, no heading exists.<br>
   * 
   * Must fulfill the following rule: headingStart <= start <= end.
   * 
   * @param headingStart int - Start of the heading, includes the given index.
   * @param start int - Start of the text, the heading ends one line before.
   * @param end int - End of the text, including the given index.
   */
  public void addChapter(int headingStart, int start, int end) {
    if (!((headingStart <= start) && (start <= end))) {
      throw new IllegalStateException("Index is not: headingStart <= start <= end");
    }

    this.headingStartList.add(headingStart);
    this.startList.add(start);
    this.endList.add(end);
  }

  /**
   * Deletes a chapter. All following chapters will get a new chapterNumber.
   * 
   * @param chapterNumber int - The number of the chapter. The possible number starts with 1 and
   *        ends at the size of the ChapterPosition.
   */
  public void deleteChapter(int chapterNumber) {
    if (chapterNumber <= 0) {
      throw new IllegalArgumentException("chapterNumber should not be <= 0");
    }
    if (chapterNumber > startList.size()) {
      throw new IndexOutOfBoundsException("Index " + chapterNumber + "is out of range " + "1 to "
          + startList.size());
    }

    this.headingStartList.remove(chapterNumber - 1);
    this.startList.remove(chapterNumber - 1);
    this.endList.remove(chapterNumber - 1);
  }

  /**
   * Change the positions of a chapter.<br>
   * 
   * Must fulfill the following rule: headingStart <= start <= end.
   * 
   * @param chapterNumber int - The number of the chapter. The possible number starts with 1 and
   *        ends at the size of the ChapterPosition.
   * @param headingStart int - Start of the heading, includes the given index.
   * @param start int - Start of the text, the heading ends one line before.
   * @param end int - End of the text, including the given index.
   */
  public void changeChapterData(int chapterNumber, int startOfHeading, int startOfText,
      int endOfText) {
    if (chapterNumber <= 0) {
      throw new IllegalArgumentException("chapterNumber should not be <= 0");
    }
    if (chapterNumber > startList.size()) {
      throw new IndexOutOfBoundsException("Index " + chapterNumber + "is out of range " + "1 to "
          + startList.size());
    }
    if (!((startOfHeading <= startOfText) && (startOfText <= endOfText))) {
      throw new IllegalStateException("Index is not: headingStart <= start <= end");
    }
    headingStartList.set(chapterNumber - 1, startOfHeading);
    startList.set(chapterNumber - 1, startOfText);
    endList.set(chapterNumber - 1, endOfText);
  }

  /**
   * Gets the start Position of the heading. Ends one line before the text starts.
   * 
   * @param chapterNumber int - The number of the chapter. The possible number starts with 1 and
   *        ends at the size of the ChapterPosition.
   * @return int - The start of the heading.
   */
  public int getStartOfHeading(int chapterNumber) {
    if (chapterNumber <= 0) {
      throw new IllegalArgumentException("chapterNumber should not be <= 0");
    }
    if (chapterNumber > startList.size()) {
      throw new IndexOutOfBoundsException("Index " + chapterNumber + "is out of range " + "1 to "
          + startList.size());
    }
    return headingStartList.get(chapterNumber - 1);
  }

  /**
   * Gets the start position of the text.
   * 
   * @param chapterNumber int - The number of the chapter. The possible number starts with 1 and
   *        ends at the size of the ChapterPosition.
   * @return int - The start of the text.
   */
  public int getStartOfText(int chapterNumber) {
    if (chapterNumber <= 0) {
      throw new IllegalArgumentException("chapterNumber should not be <= 0");
    }
    if (chapterNumber > startList.size()) {
      throw new IndexOutOfBoundsException("Index " + chapterNumber + "is out of range " + "1 to "
          + startList.size());
    }
    return startList.get(chapterNumber - 1);
  }

  /**
   * Gets the last position of the text.
   * 
   * @param chapterNumber int - The number of the chapter. The possible number starts with 1 and
   *        ends at the size of the ChapterPosition.
   * @return int - The end of the text.
   */
  public int getEndOfText(int chapterNumber) {
    if (chapterNumber <= 0) {
      throw new IllegalArgumentException("chapterNumber should not be <= 0");
    }
    if (chapterNumber > startList.size()) {
      throw new IndexOutOfBoundsException("Index " + chapterNumber + "is out of range " + "1 to "
          + startList.size());
    }
    return endList.get(chapterNumber - 1);
  }

  /**
   * Checks if this chapter has a heading or not. This information is only valid if there is more
   * than one line in the Chapter. Otherwise you have to decide whether this line is interpreted as
   * heading or as text.
   * 
   * @param chapterNumber int - The number of the chapter. The possible number starts with 1 and
   *        ends at the size of the ChapterPosition.
   * @return Boolean - true: there is a heading. false: there is no heading
   */
  public boolean hasHeading(int chapterNumber) {
    return !(startList.get(chapterNumber - 1).equals(headingStartList.get(chapterNumber - 1)));
  }

  /**
   * Returns the size of the ChapterPositions. The size is equivalent to the number of chapters and
   * the index of the last chapter.
   * 
   * @return int - The size of the ChapterPositions.
   */
  public int size() {
    return startList.size();
  }
}
