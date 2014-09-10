package de.unistuttgart.vis.vita.analysis.importer;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.Line;
import de.unistuttgart.vis.vita.importer.txt.TextFileImporter;
import de.unistuttgart.vis.vita.importer.txt.TextSplitter;

/**
 * JUnit test on TextSplitter
 * 
 *
 */
public class TextSplitterTxtTest {

  private List<Line> metadataList;
  private List<Line> textList;
  
  // divides the imported lines into metadata list and text list
  @Before
  public void setUp() throws URISyntaxException, IllegalArgumentException, FileNotFoundException, IllegalStateException, SecurityException{
    Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    TextSplitter textSplitter = new TextSplitter(textFileImporter.getLines());
    metadataList = textSplitter.getMetadataSection();
    textList = textSplitter.getTextSection();
  }
  
  
  @Test
  public void testMetdataList(){
    assertEquals(3, metadataList.size());
    assertEquals("Title: The Lord of the Rings", metadataList.get(0).getText());
    assertEquals("", metadataList.get(metadataList.size()-1).getText());

    
  }
  
  @Test
  public void testTextListSize(){
    assertEquals(4, textList.size());
    assertEquals("# Chapter 1", textList.get(0).getText());
    assertEquals("", textList.get(textList.size()-1).getText());
    
  }
}
