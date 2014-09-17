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

import de.unistuttgart.vis.vita.importer.txt.Line;
import de.unistuttgart.vis.vita.importer.txt.MetadataAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.TextFileImporter;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * JUnit test on MetadataAnalyzer
 * 
 *
 */
public class MetadataAnalyzerTxtTest {

  private DocumentMetadata documentMetadata = new DocumentMetadata();

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

    Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
    TextFileImporter textFileImporter;
    textFileImporter = new TextFileImporter(testPath);
    List<Line> testList = textFileImporter.getLines();
    MetadataAnalyzer metadataAnalyzer = new MetadataAnalyzer(testList, testPath);
    documentMetadata = metadataAnalyzer.extractMetadata();
  }

  @Test
  public void testMetadataTitle() {
    assertEquals("The Lord of the Rings", documentMetadata.getTitle());
  }

  @Test
  public void testMetadataAuthor() {
    assertEquals("J.R.R. Tolkien", documentMetadata.getAuthor());

  }

  @Test
  public void testPublisherYear() {
    assertEquals(2006, documentMetadata.getPublishYear());

  }
}
