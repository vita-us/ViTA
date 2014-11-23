package de.unistuttgart.vis.vita.analysis.importer.epub;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.extractors.ChapterPositionMaker;
import de.unistuttgart.vis.vita.importer.epub.extractors.Epubline;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;

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

    List<List<Epubline>> partEpub = new ArrayList<List<Epubline>>();
    List<Epubline> chapterOneEpub = new ArrayList<Epubline>();
    chapterOneEpub.add(new Epubline("Heading", "Chapter I.", ""));
    chapterOneEpub.add(new Epubline("", "", ""));
    chapterOneEpub.add(new Epubline("SubHeading", "Out to See", ""));
    chapterOneEpub.add(new Epubline("", "", ""));
    chapterOneEpub.add(new Epubline("Textstart", "Start Text", ""));
    chapterOneEpub.add(new Epubline("", "", ""));
    chapterOneEpub.add(new Epubline("Text", "Text a", ""));
    chapterOneEpub.add(new Epubline("", "", ""));
    chapterOneEpub.add(new Epubline("Textend", "End Text", ""));
    chapterOneEpub.add(new Epubline("", "", ""));

    partEpub.add(chapterOneEpub);

    Book book = new Book();
    chapterPositionEpub2 =
        chapterPostionMaker.calculateChapterPositionsEpub2(partEpub, book.getContents(),
            book.getNcxResource());
    chapterPositionEpub3 = chapterPostionMaker.calculateChapterPositionsEpub3(partEpub);

  }

  @Test
  public void testChapterPositionEpub2() {

    assertEquals(0, chapterPositionEpub2.getStartOfHeading(1));
    assertEquals(4, chapterPositionEpub2.getStartOfText(1));
    assertEquals(8, chapterPositionEpub2.getEndOfText(1));
  }

  @Test
  public void testChapterPositionEpub3() {

    assertEquals(0, chapterPositionEpub3.getStartOfHeading(1));
    assertEquals(2, chapterPositionEpub3.getStartOfText(1));
    assertEquals(8, chapterPositionEpub3.getEndOfText(1));
  }
}
