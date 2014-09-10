package de.unistuttgart.vis.vita.analysis.importer;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
   * @throws IllegalArgumentException
   * @throws FileNotFoundException
   * @throws IllegalStateException
   * @throws SecurityException
   * @throws URISyntaxException
   */
  @Before
  public void setUp() throws IllegalArgumentException, FileNotFoundException,
      IllegalStateException, SecurityException, URISyntaxException {

    Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
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
}
