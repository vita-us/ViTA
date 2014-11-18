package de.unistuttgart.vis.vita.analysis.importer.epub;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.util.EpubModuleLine;
import de.unistuttgart.vis.vita.importer.output.BookBuilder;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

public class EpubBookBuilderTest {
  private List<DocumentPart> parts;
  private List<DocumentPart> partsWithEmptyTitles;
  private List<DocumentPart> emptyParts;
  private List<DocumentPart> nullParts;
  
  @Before
  public void setUp() {
    List<List<Line>> partLines = new ArrayList<List<Line>>();
    List<ChapterPosition> chapterPositions = new ArrayList<ChapterPosition>();
    List<String> partTitles = new ArrayList<String>();
    
    List<Line> part1 = new ArrayList<Line>();
    partTitles.add("PART I");
    ChapterPosition position1 = new ChapterPosition();

    part1.add(new EpubModuleLine("Chapter Heading"));
    part1.add(new EpubModuleLine("   "));
    part1.add(new EpubModuleLine("blablabla"));
    part1.add(new EpubModuleLine("   "));
    part1.add(new EpubModuleLine("blabla"));
    part1.add(new EpubModuleLine("   "));
    position1.addChapter(0, 2, 5);
    
    part1.add(new EpubModuleLine("  blabla "));
    part1.add(new EpubModuleLine("  blubb "));
    part1.add(new EpubModuleLine("  bla "));
    position1.addChapter(6, 6, 8);

    List<Line> part2 = new ArrayList<Line>();
    partTitles.add(" PART II ");
    ChapterPosition position2 = new ChapterPosition();

    part2.add(new EpubModuleLine("Chapter Heading"));
    part2.add(new EpubModuleLine("   "));
    part2.add(new EpubModuleLine("blablabla"));
    part2.add(new EpubModuleLine("   "));
    position2.addChapter(0, 1, 3);
    
    partLines.add(part1);
    partLines.add(part2);
    chapterPositions.add(position1);
    chapterPositions.add(position2);  
    
    // build book
    BookBuilder bookBuilder = new BookBuilder(partLines, chapterPositions, partTitles);
    this.parts = bookBuilder.call();
    
    // empty title
    BookBuilder bookBuilderWithEmptyTitles = new BookBuilder(partLines, chapterPositions, new ArrayList<String>());
    this.partsWithEmptyTitles = bookBuilderWithEmptyTitles.call();
    
    // empty book
    List<List<Line>> emptyPartLines = new ArrayList<List<Line>>();
    List<ChapterPosition> emptyChapterPositions = new ArrayList<ChapterPosition>();
    BookBuilder emptyBookBuilder = new BookBuilder(emptyPartLines, emptyChapterPositions);
    this.emptyParts = emptyBookBuilder.call();

    //null book
    BookBuilder nullBookBuilder = new BookBuilder(null, null);
    this.nullParts = nullBookBuilder.call();
  }
  
  @Test
  public void testPartNumbers(){
    assertEquals(1,parts.get(0).getNumber());
    assertEquals(2,parts.get(1).getNumber());
  }
  
  @Test
  public void testPartTitles(){
    assertEquals("PART I",parts.get(0).getTitle());
    assertEquals("PART II",parts.get(1).getTitle());
    
    assertEquals("",partsWithEmptyTitles.get(0).getTitle());
    assertEquals("",partsWithEmptyTitles.get(1).getTitle());
    }
  
  @Test
  public void testEmptyInput(){
    assertTrue(this.emptyParts.isEmpty());
  }
  
  @Test
  public void testNullInput(){
    assertTrue(this.nullParts.isEmpty());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void wrongDimensionInput(){
    List<List<Line>> partLines = new ArrayList<List<Line>>();
    partLines.add(null);
    partLines.add(null);
    List<ChapterPosition> chapterPositions = new ArrayList<ChapterPosition>();
    chapterPositions.add(new ChapterPosition());
    new BookBuilder(partLines, chapterPositions);
  }
  

  @Test
  public void testOffsets(){
    DocumentPart part1 = parts.get(0);
    DocumentPart part2 = parts.get(1);
    
    // Chapter 1
    int startChapter1 = part1.getChapters().get(0).getRange().getStart().getOffset();
    int endChapter1 = part1.getChapters().get(0).getRange().getEnd().getOffset();
    // new line -> \n is one char
    assertEquals(0,startChapter1);
    assertEquals(17,endChapter1);
    
    // Chapter 2
    int startChapter2 = part1.getChapters().get(1).getRange().getStart().getOffset();
    int endChapter2 = part1.getChapters().get(1).getRange().getEnd().getOffset();
    assertEquals(17,startChapter2);
    assertEquals(33,endChapter2);
    
    // Chapter 3
    int startChapter3 = part2.getChapters().get(0).getRange().getStart().getOffset();
    int endChapter3 = part2.getChapters().get(0).getRange().getEnd().getOffset();
    assertEquals(33,startChapter3);
    assertEquals(42,endChapter3);
  }
}
