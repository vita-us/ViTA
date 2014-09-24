package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.txt.input.TextSplitter;
import de.unistuttgart.vis.vita.importer.txt.util.Line;

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
  public void setUp() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    TextSplitter textSplitter = new TextSplitter(textFileImporter.getLines());
    textList = textSplitter.getTextList();
    metadataList = textSplitter.getMetadataList();
  }
  
  
  @Test
  public void testMetdataList(){
    assertEquals(9, metadataList.size());
    assertEquals("Title: The", metadataList.get(0).getText());
    assertEquals("", metadataList.get(metadataList.size()-1).getText());

    
  }
  
  @Test
  public void testTextListSize(){
    assertEquals(4, textList.size());
    assertEquals("# Chapter 1", textList.get(0).getText());
    assertEquals("", textList.get(textList.size()-1).getText());
    
  }

  @Test
  public void testMultiLineDivider() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line("***Start of this", true));
    lines.add(new Line("book***", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));
    lines.add(new Line("***End of this", true));
    lines.add(new Line("book***", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(2, textSplitter.getMetadataList().size());
    assertEquals("Title: Book Title", textSplitter.getMetadataList().get(0).getText());
    assertEquals("blabla", textSplitter.getMetadataList().get(1).getText());

    assertEquals(2, textSplitter.getTextList().size());
    assertEquals("Chapter 1", textSplitter.getTextList().get(0).getText());
    assertEquals("blablabla", textSplitter.getTextList().get(1).getText());
  }

  @Test
  public void testMultiLineDividerWithSpaces() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line(" ***Start of this", true));
    lines.add(new Line("book*** ", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));
    lines.add(new Line("***End of this", true));
    lines.add(new Line("book***", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(2, textSplitter.getMetadataList().size());
    assertEquals("Title: Book Title", textSplitter.getMetadataList().get(0).getText());
    assertEquals("blabla", textSplitter.getMetadataList().get(1).getText());

    assertEquals(2, textSplitter.getTextList().size());
    assertEquals("Chapter 1", textSplitter.getTextList().get(0).getText());
    assertEquals("blablabla", textSplitter.getTextList().get(1).getText());
  }

  @Test
  public void testWithoutEnding() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line("***Start of this book***", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(2, textSplitter.getMetadataList().size());
    assertEquals("Title: Book Title", textSplitter.getMetadataList().get(0).getText());
    assertEquals("blabla", textSplitter.getMetadataList().get(1).getText());

    assertEquals(2, textSplitter.getTextList().size());
    assertEquals("Chapter 1", textSplitter.getTextList().get(0).getText());
    assertEquals("blablabla", textSplitter.getTextList().get(1).getText());
  }

  @Test
  public void testWithoutStart() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));
    lines.add(new Line("***End of book***", true));
    lines.add(new Line("Postface", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(0, textSplitter.getMetadataList().size());

    assertEquals(5, textSplitter.getTextList().size());
    assertEquals("Title: Book Title", textSplitter.getTextList().get(0).getText());
    assertEquals("Postface", textSplitter.getTextList().get(4).getText());
  }

  @Test
  public void testNoMetadata() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(0, textSplitter.getMetadataList().size());

    assertEquals(4, textSplitter.getTextList().size());
    assertEquals("Title: Book Title", textSplitter.getTextList().get(0).getText());
    assertEquals("blablabla", textSplitter.getTextList().get(3).getText());
  }

  @Test
  public void testWithFalseEnding() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line("***Start of this book***", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));
    lines.add(new Line("***End of another book***", true));
    lines.add(new Line("Postface", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(2, textSplitter.getMetadataList().size());
    assertEquals("Title: Book Title", textSplitter.getMetadataList().get(0).getText());
    assertEquals("blabla", textSplitter.getMetadataList().get(1).getText());

    assertEquals(3, textSplitter.getTextList().size());
    assertEquals("Chapter 1", textSplitter.getTextList().get(0).getText());
    assertEquals("Postface", textSplitter.getTextList().get(2).getText());
  }

  @Test
  public void testWithHalfMarker() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line("***", true));
    lines.add(new Line("Text", true));
    lines.add(new Line("***", true));
    lines.add(new Line("***", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));
    lines.add(new Line("***End of another book***", true));
    lines.add(new Line("Postface", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(0, textSplitter.getMetadataList().size());

    assertEquals(6, textSplitter.getTextList().size());
    assertEquals("Title: Book Title", textSplitter.getTextList().get(0).getText());
    assertEquals("blablabla", textSplitter.getTextList().get(4).getText());
  }

  @Test
  public void testWithDoubleMarkers() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("Title: Book Title", true));
    lines.add(new Line("blabla", true));
    lines.add(new Line("*** Start of BOOK ***", true));
    lines.add(new Line("text1", true));
    lines.add(new Line("*** Start of BOOK ***", true));
    lines.add(new Line("Chapter 1", true));
    lines.add(new Line("blablabla", true));
    lines.add(new Line("***End of BOOK***", true));
    lines.add(new Line("text2", true));
    lines.add(new Line("***End of BOOK***", true));
    lines.add(new Line("Postface", true));

    TextSplitter textSplitter = new TextSplitter(lines);

    assertEquals(2, textSplitter.getMetadataList().size());

    assertEquals(3, textSplitter.getTextList().size());
    assertEquals("text1", textSplitter.getTextList().get(0).getText());
    assertEquals("blablabla", textSplitter.getTextList().get(2).getText());
  }

}