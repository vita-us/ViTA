package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.ChapterPosition;

public class ChapterPositionTxtTest {

  @Test
  public void testAddChapter() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);

    assertTrue(positions.getStartOfHeading(1) == 0);
    assertTrue(positions.getStartOfText(1) == 2);
    assertTrue(positions.getEndOfText(1) == 5);
  }

  @Test(expected = IllegalStateException.class)
  public void testAddChapterWithBadPositions1() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(1, 0, 5);
  }

  @Test(expected = IllegalStateException.class)
  public void testAddChapterWithBadPositions2() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 1, 0);
  }

  @Test
  public void testChangeChapterData() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);
    positions.changeChapterData(1, 5, 8, 9);

    assertTrue(positions.getStartOfHeading(1) == 5);
    assertTrue(positions.getStartOfText(1) == 8);
    assertTrue(positions.getEndOfText(1) == 9);
  }

  @Test(expected = IllegalStateException.class)
  public void testChangeChapterDataWithBadPositions1() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);
    positions.changeChapterData(1, 5, 4, 9);
  }

  @Test(expected = IllegalStateException.class)
  public void testChangeChapterDataWithBadPositions2() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);
    positions.changeChapterData(1, 5, 8, 7);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testChangeChapterDataWithIllegalChapterNumber1() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);
    positions.changeChapterData(0, 5, 8, 7);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testChangeChapterDataWithIllegalChapterNumber2() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);
    positions.changeChapterData(2, 5, 8, 7);
  }

  @Test
  public void testDeleteChapterAndSize() {
    ChapterPosition positions = new ChapterPosition();
    assertTrue(positions.size() == 0);

    positions.addChapter(0, 2, 5);
    assertTrue(positions.size() == 1);

    positions.changeChapterData(1, 5, 8, 9);
    assertTrue(positions.size() == 1);

    positions.addChapter(0, 2, 5);
    assertTrue(positions.size() == 2);

    positions.deleteChapter(1);
    assertTrue(positions.size() == 1);

    assertTrue(positions.getStartOfHeading(1) == 0);
    assertTrue(positions.getStartOfText(1) == 2);
    assertTrue(positions.getEndOfText(1) == 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteChapterDataWithIllegalChapterNumber1() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);
    positions.deleteChapter(0);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testDeleteChapterDataWithIllegalChapterNumber2() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);
    positions.deleteChapter(2);
  }

  @Test
  public void testHasHeading() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 0, 2);
    assertFalse(positions.hasHeading(1));

    positions.addChapter(3, 3, 3);
    assertFalse(positions.hasHeading(2));


    positions.addChapter(4, 5, 5);
    assertTrue(positions.hasHeading(3));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStartOfHeadingWithIllegalChapterNumber1() {
    ChapterPosition positions = new ChapterPosition();
    positions.getStartOfHeading(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStartOfTextWithIllegalChapterNumber1() {
    ChapterPosition positions = new ChapterPosition();
    positions.getStartOfText(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetEndOfTextWithIllegalChapterNumber1() {
    ChapterPosition positions = new ChapterPosition();
    positions.getEndOfText(0);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetStartOfHeadingWithIllegalChapterNumber2() {
    ChapterPosition positions = new ChapterPosition();
    positions.getStartOfHeading(1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetStartOfTextWithIllegalChapterNumber2() {
    ChapterPosition positions = new ChapterPosition();
    positions.getStartOfText(1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetEndOfTextWithIllegalChapterNumber2() {
    ChapterPosition positions = new ChapterPosition();
    positions.getEndOfText(1);
  }

  @Test
  public void testClone() {
    ChapterPosition positions = new ChapterPosition();
    positions.addChapter(0, 2, 5);

    ChapterPosition clonedPositions = positions.clone();

    positions.changeChapterData(1, 1, 3, 6);

    assertTrue(clonedPositions.getStartOfHeading(1) == 0);
    assertTrue(clonedPositions.getStartOfText(1) == 2);
    assertTrue(clonedPositions.getEndOfText(1) == 5);

    assertTrue(positions.getStartOfHeading(1) == 1);
    assertTrue(positions.getStartOfText(1) == 3);
    assertTrue(positions.getEndOfText(1) == 6);
  }


}
