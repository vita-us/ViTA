package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.siegmann.epublib.domain.Book;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.importer.epub.EpubVersion;
import de.unistuttgart.vis.vita.importer.epub.EpubVersionDetector;

public class EpubVersionDetectorTest {

  @Test
  public void testVersionUnknown() throws URISyntaxException, FileNotFoundException, IOException{
    Path testPath = Paths.get(getClass().getResource("noversion.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    Book book = epubFileImporter.getEbook();
    
    EpubVersionDetector detector = new EpubVersionDetector(book);
    assertEquals(EpubVersion.unknown,detector.getVersion());
  }
  
  
  @Test
  public void testVersion2() throws URISyntaxException, FileNotFoundException, IOException{
    Path testPath = Paths.get(getClass().getResource("pg78.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    Book book = epubFileImporter.getEbook();
    
    EpubVersionDetector detector = new EpubVersionDetector(book);
    assertEquals(EpubVersion.standard2,detector.getVersion());
  }
  
  @Test
  public void testVersion3() throws URISyntaxException, FileNotFoundException, IOException{
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    Book book = epubFileImporter.getEbook();
            
    EpubVersionDetector detector = new EpubVersionDetector(book);
    assertEquals(EpubVersion.standard3,detector.getVersion());
  }
}
