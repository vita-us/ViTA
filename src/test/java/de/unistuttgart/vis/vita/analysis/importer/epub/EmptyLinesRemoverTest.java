package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import de.unistuttgart.vis.vita.importer.epub.EmptyLinesRemover;
import de.unistuttgart.vis.vita.importer.epub.Epubline;

/**
 * JUnit test on EmptyLinesRemover
 * 
 *
 */
public class EmptyLinesRemoverTest {

  private EmptyLinesRemover emptyLinesRemover = new EmptyLinesRemover();
  private List<List<Epubline>> partOne = new ArrayList<List<Epubline>>();
  private List<List<List<Epubline>>> parts = new ArrayList<List<List<Epubline>>>();
  
  @Before
  public void setUp(){
    List<Epubline> chapterOne = new ArrayList<Epubline>();
    chapterOne.add(new Epubline("", "Text", ""));
    chapterOne.add(new Epubline("", "Text", ""));
    chapterOne.add(new Epubline("", "", ""));
    chapterOne.add(new Epubline("", "Text", ""));
    chapterOne.add(new Epubline("", "", ""));
    
    partOne.add(chapterOne);
    emptyLinesRemover.removeEmptyLinesPart(partOne);
    
    List<List<Epubline>> partTwo = new ArrayList<List<Epubline>>();

    List<Epubline> chapterTwo = new ArrayList<Epubline>();
    chapterTwo.add(new Epubline("", "Text a", ""));
    chapterTwo.add(new Epubline("", "Text a", ""));
    chapterTwo.add(new Epubline("", "", ""));
    chapterTwo.add(new Epubline("", "", ""));
    chapterTwo.add(new Epubline("", "", ""));
    chapterTwo.add(new Epubline("", "Text a", ""));
    chapterTwo.add(new Epubline("", "Text a", ""));

    partTwo.add(chapterTwo);
    parts.add(partOne);
    parts.add(partTwo);
    emptyLinesRemover.removeEmptyLinesParts(parts);
    
  }
  
  @Test
  public void testPartChapterSize(){
    assertEquals(3, partOne.get(0).size());
  }
  
  @Test
  public void testPartChapterContent(){
    for(int i = 0; i < 3; i++){
      assertEquals("Text", partOne.get(0).get(i).getEpubline());
    }
  }
  
  @Test
  public void testPartsChapterSize(){
    assertEquals(3, parts.get(0).get(0).size());
    assertEquals(4, parts.get(1).get(0).size());

  }
  
  @Test
  public void testPartsContent(){
    
    for(int i = 0; i < 3; i++){
      assertEquals("Text", parts.get(0).get(0).get(i).getEpubline());
    }
    
    for(int i = 0; i < 4; i++){
      assertEquals("Text a", parts.get(1).get(0).get(i).getEpubline());
    }
  }
}
