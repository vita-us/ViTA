package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;

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

import de.unistuttgart.vis.vita.importer.txt.input.Filter;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.importer.txt.util.TxtModuleLine;

/**
 * JUnit test on Filter
 * 
 *
 */
public class FilterTxtTest {

  private List<Line> filteredList = new ArrayList<Line>();

  /**
   * Sets the filteredList after the imported List is filtered
   * 
   * @throws URISyntaxException
   * @throws FileNotFoundException
   * @throws SecurityException
   * @throws UnsupportedEncodingException
   * @throws InvalidPathException
   */
  @Before
  public void setUp() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
    TextFileImporter textFileImporter;
    textFileImporter = new TextFileImporter(testPath);
    Filter filter = new Filter(textFileImporter.getLines());
    filteredList = filter.filterEbookText();
  }

  @Test
  public void testFilterEbookText() {
    assertFalse(filteredList.get(11).getText().equals("[Ebook The Lord of the Rings]"));
    assertEquals(filteredList.get(12).getText(), "The text of the first chapter.");
  }

  @Test
  public void testMultiLineComment() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("blabla [this"));
    lines.add(new TxtModuleLine("is"));
    lines.add(new TxtModuleLine("a comment] blabla"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("blabla ", filteredList.get(0).getText());
    assertEquals(" blabla", filteredList.get(1).getText());
  }

  @Test
  public void testCommentInOneLineWithoutSpacesAround() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("blabla[this]blabla"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(1, filteredList.size());
    assertEquals("blabla blabla", filteredList.get(0).getText());

  }

  @Test
  public void testCommentInOneLineWithSpacesAround() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("blabla [this] blabla"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(1, filteredList.size());
    assertEquals("blabla blabla", filteredList.get(0).getText());
  }

  @Test
  public void testCommentInAWholeLineWithSpacesAround() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("comment1"));
    lines.add(new TxtModuleLine(""));
    lines.add(new TxtModuleLine("[comment2]"));
    lines.add(new TxtModuleLine(""));
    lines.add(new TxtModuleLine("text"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(5, filteredList.size());
    assertEquals("comment1", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
    assertEquals(" ", filteredList.get(2).getText());
    assertEquals("", filteredList.get(3).getText());
    assertEquals("text", filteredList.get(4).getText());
  }

  @Test
  public void testRemoveSpecialSigns() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("blabla usw"));
    lines.add(new TxtModuleLine("...?"));
    lines.add(new TxtModuleLine("???"));
    lines.add(new TxtModuleLine(""));
    lines.add(new TxtModuleLine("* * *"));
    lines.add(new TxtModuleLine("blubb"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(4, filteredList.size());
    assertEquals("blabla usw", filteredList.get(0).getText());
    assertEquals("...?", filteredList.get(1).getText());
    assertEquals("blubb", filteredList.get(3).getText());
  }

  @Test
  public void testRemoveSpecialSignsWithWhitelineReduction() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("blabla usw"));
    lines.add(new TxtModuleLine(""));
    lines.add(new TxtModuleLine("* * *"));
    lines.add(new TxtModuleLine(""));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("blabla usw", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
  }

  @Test
  public void testRemoveSpecialSignBottomBorder() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("blabla usw"));
    lines.add(new TxtModuleLine(""));
    lines.add(new TxtModuleLine("* * *"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("blabla usw", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
  }

  @Test
  public void testRemoveSpecialSignsTopBorder() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("* * *"));
    lines.add(new TxtModuleLine(""));
    lines.add(new TxtModuleLine("blabla usw"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("", filteredList.get(0).getText());
    assertEquals("blabla usw", filteredList.get(1).getText());
  }

  @Test
  public void testMissingEndBracket() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("bla  blabla ["));
    lines.add(new TxtModuleLine("text1"));
    lines.add(new TxtModuleLine("text2"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(3, filteredList.size());
    assertEquals("bla  blabla [", filteredList.get(0).getText());
    assertEquals("text1", filteredList.get(1).getText());
    assertEquals("text2", filteredList.get(2).getText());
  }

  @Test
  public void testMissingStartBracket() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("blablabla"));
    lines.add(new TxtModuleLine("[blubb.]text1]"));
    lines.add(new TxtModuleLine("text2"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(3, filteredList.size());
    assertEquals("blablabla", filteredList.get(0).getText());
    assertEquals(" text1]", filteredList.get(1).getText());
    assertEquals("text2", filteredList.get(2).getText());
  }

  @Test
  public void testDoubleBrackets() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("text [bla text1 bla text2]"));

    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();
    
    assertEquals(1, filteredList.size());
    assertEquals("text ", filteredList.get(0).getText());
  }

  @Test
  public void testMultilineBrackets() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("text [bla"));
    lines.add(new TxtModuleLine("te[xt1 ]bla"));
    lines.add(new TxtModuleLine("[text2]"));
    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();
    
    assertEquals(3, filteredList.size());
    assertEquals("text [bla", filteredList.get(0).getText());
    assertEquals("te bla", filteredList.get(1).getText());
    assertEquals(" ", filteredList.get(2).getText());
  }

  @Test
  public void testDoubleBracketsInOneLine() {
    List<Line> lines = new ArrayList<Line>();
    lines.add(new TxtModuleLine("text[bla2 bla3 blubb]"));


    Filter filter = new Filter(lines);
    List<Line> filteredList = filter.filterEbookText();

    assertEquals(1, filteredList.size());
    assertEquals("text ", filteredList.get(0).getText());
  }
}
