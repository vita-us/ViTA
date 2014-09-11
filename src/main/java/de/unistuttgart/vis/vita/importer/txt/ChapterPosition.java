package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;

public class ChapterPosition {
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

  // TODO: DELETE
  public void print(int i) {
    System.out.println(headingStartList.get(i) + " " + startList.get(i) + " " + endList.get(i));
  }

  public boolean hasHeading(int chapterNumber) {
    return !(startList.get(chapterNumber - 1) == headingStartList.get(chapterNumber - 1));
  }

  // TODO: End / Start Heading

  public int size() {
    return startList.size();
  }
}