package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import de.unistuttgart.vis.vita.importer.epub.EmptyLinesRemover;

/**
 * JUnit test on EmptyLinesRemover
 * 
 *
 */
public class EmptyLinesRemoverTest {

  private EmptyLinesRemover emptyLinesRemover = new EmptyLinesRemover();
  private List<List<String>> partOne = new ArrayList<List<String>>();
  private List<List<List<String>>> parts = new ArrayList<List<List<String>>>();
  
  @Before
  public void setUp(){
    List<String> chapterOne = new ArrayList<String>();
    chapterOne.add("Text");
    chapterOne.add("Text");
    chapterOne.add("");
    chapterOne.add("Text");
    chapterOne.add("");
    
    partOne.add(chapterOne);
    emptyLinesRemover.removeEmptyLinesPart(partOne);
    
    List<List<String>> partTwo = new ArrayList<List<String>>();

    List<String> chapterTwo = new ArrayList<String>();
    chapterTwo.add("Text a");
    chapterTwo.add("Text a");
    chapterTwo.add("Text a");
    chapterTwo.add("");
    chapterTwo.add("");
    chapterTwo.add("");
    chapterTwo.add("Text a");

    
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
      assertEquals("Text", partOne.get(0).get(i));
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
      assertEquals("Text", parts.get(0).get(0).get(i));
    }
    
    for(int i = 0; i < 4; i++){
      assertEquals("Text a", parts.get(1).get(0).get(i));
    }
  }
}
