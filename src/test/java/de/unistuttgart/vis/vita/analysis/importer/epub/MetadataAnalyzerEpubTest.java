package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import de.unistuttgart.vis.vita.importer.epub.extractors.MetadataAnalyzerEpub;
import de.unistuttgart.vis.vita.importer.epub.input.EpubFileImporter;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * JUnit test on MetadataAnalyzerEpub
 * 
 *
 */
public class MetadataAnalyzerEpubTest {

  private DocumentMetadata documentMetadata = new DocumentMetadata();

  @Before
  public void setUp() throws URISyntaxException, IOException {
    Path testPath = Paths.get(getClass().getResource("pg78.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    MetadataAnalyzerEpub metadataAnalyzerEpub =
        new MetadataAnalyzerEpub(epubFileImporter.getEbook());
    documentMetadata = metadataAnalyzerEpub.extractMetadata();
  }

  @Test
  public void testMetadata() {
    assertEquals("Edgar Rice Burroughs", documentMetadata.getAuthor());
    assertEquals("Tarzan of the Apes", documentMetadata.getTitle());
    assertEquals(1993, documentMetadata.getPublishYear().intValue());
    assertEquals("", documentMetadata.getPublisher());
    assertEquals("", documentMetadata.getEdition());
    assertEquals("", documentMetadata.getGenre());
  }
}
