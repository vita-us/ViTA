package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import de.unistuttgart.vis.vita.importer.epub.ChapterPositionMaker;
import de.unistuttgart.vis.vita.importer.epub.Epubline;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;

public class ChapterPositionMakerTest {

  /**
   * JUnit test on ChapterPositionMaker
   * 
   *
   */
  private ChapterPositionMaker chapterPostionMaker = new ChapterPositionMaker();
  ChapterPosition chapterPositionEpub2 = new ChapterPosition();
  ChapterPosition chapterPositionEpub3 = new ChapterPosition();


  @Before
  public void setUp() {

    List<List<Epubline>> partEpub2 = new ArrayList<List<Epubline>>();
    List<Epubline> chapterOneEpub2 = new ArrayList<Epubline>();
    chapterOneEpub2.add(new Epubline("Heading", "Chapter I.", ""));
    chapterOneEpub2.add(new Epubline("", "", ""));
    chapterOneEpub2.add(new Epubline("SubHeading", "Out to See", ""));
    chapterOneEpub2.add(new Epubline("", "", ""));
    chapterOneEpub2.add(new Epubline("Text", "Text a", ""));
    chapterOneEpub2.add(new Epubline("", "", ""));
    chapterOneEpub2.add(new Epubline("Textend", "End Text", ""));
    chapterOneEpub2.add(new Epubline("", "", ""));

    partEpub2.add(chapterOneEpub2);
    chapterPositionEpub2 =
        chapterPostionMaker.calculateChapterPositionsEpub2(partEpub2, new Book());

    List<List<String>> partEpub3 = new ArrayList<List<String>>();
    List<String> chapterOneEpub3 = new ArrayList<String>();
    chapterOneEpub3.add("Chapter I.");
    chapterOneEpub3.add("");
    chapterOneEpub3.add("Text a");
    chapterOneEpub3.add("");
    chapterOneEpub3.add("End Text");
    chapterOneEpub3.add("");

    partEpub3.add(chapterOneEpub3);
    chapterPositionEpub3 = chapterPostionMaker.calculateChapterPositionsEpub3(partEpub3);

  }

  @Test
  public void testChapterPositionEpub2() {

    assertEquals(0, chapterPositionEpub2.getStartOfHeading(1));
    assertEquals(2, chapterPositionEpub2.getStartOfText(1));
    assertEquals(6, chapterPositionEpub2.getEndOfText(1));
  }

  @Test
  public void testChapterPositionEpub3() {

    assertEquals(0, chapterPositionEpub3.getStartOfHeading(1));
    assertEquals(2, chapterPositionEpub3.getStartOfText(1));
    assertEquals(4, chapterPositionEpub3.getEndOfText(1));
  }
}
