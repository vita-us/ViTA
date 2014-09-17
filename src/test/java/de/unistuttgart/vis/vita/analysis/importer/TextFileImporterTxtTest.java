package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.Line;
import de.unistuttgart.vis.vita.importer.txt.TextFileImporter;

public class TextFileImporterTxtTest {


  String testChars =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ,.;-!?`[]()\"'#0123456789$§&";

  @Test
  public void testEncodingANSI() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {

    Path testPath = Paths.get(getClass().getResource("TheStoryOfMoscow_ANSI.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    String encodingName = textFileImporter.getNameOfDetectedEncoding();
    ArrayList<Line> testList = textFileImporter.getLines();

    assertTrue(testList.size() == 10579);
    assertEquals(testChars, testList.get(8).getText());
    assertEquals("ISO-8859-1", encodingName);
  }

  @Test
  public void testEncodingUnicode() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {

    Path testPath = Paths.get(getClass().getResource("TheStoryOfMoscow_Unicode.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    String encodingName = textFileImporter.getNameOfDetectedEncoding();
    ArrayList<Line> testList = textFileImporter.getLines();

    assertTrue(testList.size() == 10579);
    assertEquals(testChars, testList.get(8).getText());
    assertEquals("UTF-16", encodingName);
  }

  @Test
  public void testEncodingUnicodeBE() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {

    Path testPath = Paths.get(getClass().getResource("TheStoryOfMoscow_UnicodeBE.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    String encodingName = textFileImporter.getNameOfDetectedEncoding();
    ArrayList<Line> testList = textFileImporter.getLines();


    ArrayList<String> possibleSolutions = new ArrayList<String>();
    possibleSolutions.add("UTF-16");
    possibleSolutions.add("UTF-16BE");

    assertTrue(testList.size() == 10579);
    assertEquals(testChars, testList.get(8).getText());
    System.out.println(encodingName);
    assertTrue(possibleSolutions.contains(encodingName));
  }

  @Test
  public void testEncodingUTF8() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {

    Path testPath = Paths.get(getClass().getResource("TheStoryOfMoscow_UTF8.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    String encodingName = textFileImporter.getNameOfDetectedEncoding();
    ArrayList<Line> testList = textFileImporter.getLines();

    assertTrue(testList.size() == 10579);
    assertEquals(testChars, testList.get(8).getText());
    assertEquals("UTF-8", encodingName);
  }

  @Test
  public void testEncodingASCI() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {

    Path testPath = Paths.get(getClass().getResource("46794.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    String encodingName = textFileImporter.getNameOfDetectedEncoding();
    ArrayList<Line> testList = textFileImporter.getLines();

    assertTrue(testList.size() == 8648);
    assertEquals("Release Date: September 7, 2014 [EBook #46794]", testList.get(16).getText());
    assertEquals("US-ASCII", encodingName);
  }

  @Test(expected = UnsupportedEncodingException.class)
  public void testImportEmptyFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {

    Path testPath = Paths.get(getClass().getResource("Empty.txt").toURI());
    new TextFileImporter(testPath);
  }

  @Test(expected = UnsupportedEncodingException.class)
  public void testImportSpecialSignsFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("specialSignsInOneLine.txt").toURI());
    new TextFileImporter(testPath);
  }

  @Test(expected = FileNotFoundException.class)
  public void testImportNotExistingFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get("blablabla.txt");
    new TextFileImporter(testPath);
  }

  @Test(expected = InvalidPathException.class)
  public void testImportNotATxt() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("NotATxt").toURI());
    new TextFileImporter(testPath);
  }

  @Test(expected = InvalidPathException.class)
  public void testImportNotNamedFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(".txt");
    new TextFileImporter(testPath);
  }
  
  @Test(expected = FileNotFoundException.class)
  public void testImportFolder() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("testFolder.txt").toURI());
    new TextFileImporter(testPath);
  }

}
