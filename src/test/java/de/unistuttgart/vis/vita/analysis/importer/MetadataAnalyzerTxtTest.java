package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.analyzers.MetadataAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * JUnit test on MetadataAnalyzer
 * 
 *
 */
public class MetadataAnalyzerTxtTest {

  private DocumentMetadata documentMetadataText1 = new DocumentMetadata();
  private DocumentMetadata documentMetadataText2 = new DocumentMetadata();
  private DocumentMetadata documentMetadataText3 = new DocumentMetadata();
  private DocumentMetadata documentMetadataText4 = new DocumentMetadata();

  /**
   * sets the values of documentMetadata after MetadataAnalyzer has analyzed the imported lines
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

    {
      Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
      TextFileImporter textFileImporter;
      textFileImporter = new TextFileImporter(testPath);
      List<Line> testList = textFileImporter.getLines();
      MetadataAnalyzer metadataAnalyzer = new MetadataAnalyzer(testList, testPath);
      documentMetadataText1 = metadataAnalyzer.extractMetadata();
    }

    {
      Path testPath = Paths.get(getClass().getResource("text2.txt").toURI());
      TextFileImporter textFileImporter;
      textFileImporter = new TextFileImporter(testPath);
      List<Line> testList = textFileImporter.getLines();
      MetadataAnalyzer metadataAnalyzer = new MetadataAnalyzer(testList, testPath);
      documentMetadataText2 = metadataAnalyzer.extractMetadata();
    }
    {
      Path testPath = Paths.get(getClass().getResource("metadata.txt").toURI());
      TextFileImporter textFileImporter;
      textFileImporter = new TextFileImporter(testPath);
      List<Line> testList = textFileImporter.getLines();
      MetadataAnalyzer metadataAnalyzer = new MetadataAnalyzer(testList, testPath);
      documentMetadataText3 = metadataAnalyzer.extractMetadata();
    }
    {
      Path testPath = Paths.get(getClass().getResource("TooLongData.txt").toURI());
      TextFileImporter textFileImporter;
      textFileImporter = new TextFileImporter(testPath);
      List<Line> testList = textFileImporter.getLines();
      MetadataAnalyzer metadataAnalyzer = new MetadataAnalyzer(testList, testPath);
      documentMetadataText4 = metadataAnalyzer.extractMetadata();
    }
  }

  @Test
  public void testMetadataTitle() {
    assertEquals("The Lord of the Rings", documentMetadataText1.getTitle());
    assertEquals("\"Captains # Courageous\"", documentMetadataText2.getTitle());
    assertEquals("The Lord of the Rings", documentMetadataText3.getTitle());
  }

  @Test
  public void testMetadataAuthor() {
    assertEquals("J.R.R. Tolkien", documentMetadataText1.getAuthor());
    assertEquals("Rudyard Kipling", documentMetadataText2.getAuthor());
    assertEquals("J.R.R. Tolkien", documentMetadataText3.getAuthor());
  }

  @Test
  public void testPublisherYear() {
    assertEquals(2006, documentMetadataText1.getPublishYear());
    assertEquals(1999, documentMetadataText2.getPublishYear());
    assertEquals(2010, documentMetadataText3.getPublishYear());
  }

  @Test
  public void testGenre() {
    assertEquals("Fantasy/Science-Fiction", documentMetadataText2.getGenre());
    assertEquals("Fantasy", documentMetadataText3.getGenre());
  }

  @Test
  public void testPublisher() {
    assertEquals("Karl Klobus", documentMetadataText2.getPublisher());
    assertEquals("Mr. XY", documentMetadataText3.getPublisher());
  }

  @Test
  public void testEdition() {
    assertEquals("13", documentMetadataText2.getEdition());
    assertEquals("1", documentMetadataText3.getEdition());
  }
  
  @Test
  public void testTooLongData(){
    String sequence = "abcdefghij";
    String longText = "";
    for(int i = 0; i < 100; i++){
      longText += sequence;
    }
    assertEquals(longText, documentMetadataText4.getTitle());
    assertEquals(longText, documentMetadataText4.getAuthor());
    assertEquals(longText, documentMetadataText4.getGenre());
    assertEquals(longText, documentMetadataText4.getEdition());
    assertEquals(longText, documentMetadataText4.getPublisher());
  }
  
}
