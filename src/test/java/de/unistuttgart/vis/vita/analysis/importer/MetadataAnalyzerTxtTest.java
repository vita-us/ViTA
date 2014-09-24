package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;

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
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * JUnit test on MetadataAnalyzer
 * 
 *
 */
public class MetadataAnalyzerTxtTest {

  private DocumentMetadata documentMetadataText1 = new DocumentMetadata();
  private DocumentMetadata documentMetadataText2 = new DocumentMetadata();

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
  }

  @Test
  public void testMetadataTitle() {
    assertEquals("The Lord of the Rings", documentMetadataText1.getTitle());
    assertEquals("\"Captains Courageous\"", documentMetadataText2.getTitle());

  }

  @Test
  public void testMetadataAuthor() {
    assertEquals("J.R.R. Tolkien", documentMetadataText1.getAuthor());
    assertEquals("Rudyard Kipling", documentMetadataText2.getAuthor());

  }

  @Test
  public void testPublisherYear() {
    assertEquals(2006, documentMetadataText1.getPublishYear());
    assertEquals(1999, documentMetadataText2.getPublishYear());
  }

  @Test
  public void testGenre() {
    assertEquals("Fantasy/Science-Fiction", documentMetadataText2.getGenre());
  }

  @Test
  public void testPublisher() {
    assertEquals("Paul Paulson", documentMetadataText2.getPublisher());
  }

  @Test
  public void testEdition() {
    assertEquals("13", documentMetadataText2.getEdition());
  }
}
