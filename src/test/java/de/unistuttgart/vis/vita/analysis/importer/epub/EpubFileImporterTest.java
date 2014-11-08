package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.siegmann.epublib.domain.Book;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.ContentBuilder;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;

/**
 * JUnit test on EpubFileImporter
 * 
 *
 */
public class EpubFileImporterTest {

  private EpubFileImporter epubFileImporter;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private String opf;
  private Book ebook = new Book();
  
  @Before
  public void setUp() throws URISyntaxException, IOException{
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214.epub").toURI());
    epubFileImporter = new EpubFileImporter(testPath);
    ebook = epubFileImporter.getEbook();
    opf = contentBuilder.getStringFromInputStream(ebook.getOpfResource().getInputStream());
    
  }
  
  @Test
  public void nullCheckTest(){
    assertTrue(ebook != null);
    assertTrue(ebook.getOpfResource()!= null);

  }
  
  @Test
  public void testEbookTitle(){
    assertEquals("Moby-Dick", ebook.getTitle());
  }
  
  @Test
  public void opfContentTest(){
    assertTrue(opf.contains("<dc:title id=\"title\">Moby-Dick</dc:title>"));
    assertTrue(opf.contains("<dc:creator id=\"creator\">Herman Melville</dc:creator>"));
  }
  
}
