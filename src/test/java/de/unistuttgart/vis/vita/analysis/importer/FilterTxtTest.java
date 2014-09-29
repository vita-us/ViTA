package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("blabla [this"));
    lines.add(new Line("is"));
    lines.add(new Line("a comment] blabla"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("blabla  ", filteredList.get(0).getText());
    assertEquals("  blabla", filteredList.get(1).getText());
  }

  @Test
  public void testCommentInOneLineWithoutSpacesAround() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("blabla[this]blabla"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(1, filteredList.size());
    assertEquals("blabla blabla", filteredList.get(0).getText());

  }

  @Test
  public void testCommentInOneLineWithSpacesAround() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("blabla [this] blabla"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(1, filteredList.size());
    assertEquals("blabla blabla", filteredList.get(0).getText());
  }

  @Test
  public void testCommentInAWholeLineWithSpacesAround() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("comment1"));
    lines.add(new Line(""));
    lines.add(new Line("[comment2]"));
    lines.add(new Line(""));
    lines.add(new Line("text"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(5, filteredList.size());
    assertEquals("comment1", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
    assertEquals(" ", filteredList.get(2).getText());
    assertEquals("", filteredList.get(3).getText());
    assertEquals("text", filteredList.get(4).getText());
  }

  @Test
  public void testRemoveSpecialSigns() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("blabla usw"));
    lines.add(new Line("...?"));
    lines.add(new Line("???"));
    lines.add(new Line(""));
    lines.add(new Line("* * *"));
    lines.add(new Line("blubb"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(4, filteredList.size());
    assertEquals("blabla usw", filteredList.get(0).getText());
    assertEquals("...?", filteredList.get(1).getText());
    assertEquals("blubb", filteredList.get(3).getText());
  }

  @Test
  public void testRemoveSpecialSignsWithWhitelineReduction() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("blabla usw"));
    lines.add(new Line(""));
    lines.add(new Line("* * *"));
    lines.add(new Line(""));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("blabla usw", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
  }

  @Test
  public void testRemoveSpecialSignBottomBorder() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("blabla usw"));
    lines.add(new Line(""));
    lines.add(new Line("* * *"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("blabla usw", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
  }

  @Test
  public void testRemoveSpecialSignsTopBorder() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("* * *"));
    lines.add(new Line(""));
    lines.add(new Line("blabla usw"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("", filteredList.get(0).getText());
    assertEquals("blabla usw", filteredList.get(1).getText());
  }

  @Test
  public void testMissingEndBracket() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("bla  blabla ["));
    lines.add(new Line("text1"));
    lines.add(new Line("text2"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(3, filteredList.size());
    assertEquals("bla  blabla [", filteredList.get(0).getText());
    assertEquals("text1", filteredList.get(1).getText());
    assertEquals("text2", filteredList.get(2).getText());
  }

  @Test
  public void testMissingStartBracket() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("blablabla"));
    lines.add(new Line("[blubb.]text1]"));
    lines.add(new Line("text2"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(3, filteredList.size());
    assertEquals("blablabla", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
    assertEquals("text2", filteredList.get(2).getText());
  }

  @Test
  public void testDoubledBrackets() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("text [blate[xt1 ]bla[]text2]"));

    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();
    
    assertEquals(1, filteredList.size());
    assertEquals("text ", filteredList.get(0).getText());
  }

  @Test
  public void testMultilineDoubledBrackets() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("text [bla"));
    lines.add(new Line("te[xt1 ]bla"));
    lines.add(new Line("[]text2]"));
    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(2, filteredList.size());
    assertEquals("text ", filteredList.get(0).getText());
    assertEquals("", filteredList.get(1).getText());
  }

  @Test
  public void testDoubledBracketsInOneLine() {
    ArrayList<Line> lines = new ArrayList<Line>();
    lines.add(new Line("text[bla2[bla3] blubb]"));


    Filter filter = new Filter(lines);
    ArrayList<Line> filteredList = filter.filterEbookText();

    assertEquals(1, filteredList.size());
    assertEquals("text", filteredList.get(0).getText());
  }
}
