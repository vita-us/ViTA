package de.unistuttgart.vis.vita.analysis.importer.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.modules.EpubImportModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.importer.epub.NoExtractorFoundException;
import de.unistuttgart.vis.vita.model.document.Chapter;

public class Epub2ImportModuleTest {
    ImportResult epub2Result;

    @Before
    public void setUp() throws URISyntaxException, FileNotFoundException, IOException,
        ParseException, NoExtractorFoundException {
      Path epub2TestPath = Paths.get(getClass().getResource("pg78.epub").toURI());
      EpubImportModule epub2ImportModule = new EpubImportModule(epub2TestPath);
      epub2Result = epub2ImportModule.execute(null, null);
    }

    @Test
    public void testMetadataTitle(){
      // TODO: ADD DATA
      String text = "todo";
      assertEquals(text,epub2Result.getMetadata().getTitle());
    }
    
    @Test
    public void testMetadataAuthor(){
      // TODO: ADD DATA
      String text = "todo";
      assertEquals(text,epub2Result.getMetadata().getAuthor());
    }

    @Test
    public void testMetadataEdition(){
      // TODO: ADD DATA
      String text = "todo";
      assertEquals(text,epub2Result.getMetadata().getEdition());
    }

    @Test
    public void testMetadataGenre(){
      // TODO: ADD DATA
      String text = "todo";
      assertEquals(text,epub2Result.getMetadata().getGenre());
    }
    
    @Test
    public void testMetadataPublisher(){
      // TODO: ADD DATA
      String text = "todo";
      assertEquals(text,epub2Result.getMetadata().getPublisher());
    }
    

    @Test
    public void testMetadataPublishYear(){
      // TODO: ADD DATA
      int text = -1;
      assertEquals(text,epub2Result.getMetadata().getPublishYear());
    }
    
    @Test
    public void testEpub2Sizes() {
      // parts size
      assertEquals(1, epub2Result.getParts().size());

      // chapters size of first part
      assertEquals(28, epub2Result.getParts().get(0).getChapters().size());
    }

    @Test
    public void testEpub2PartTitle() {
      assertEquals("", epub2Result.getParts().get(0).getTitle());
    }

    @Test
    public void testEpub2ChaptersTitle() {
      assertEquals("Chapter I\n\nOut to Sea", getChapter(epub2Result, 0, 0).getTitle());
      assertEquals("Chapter X\n\nThe Fear-Phantom", getChapter(epub2Result, 0, 9).getTitle());
      assertEquals("Chapter XXVIII\n\nConclusion", getChapter(epub2Result, 0, 27).getTitle());    
    }

    @Test
    public void testEpub2FirstChapterTextStart() {
      String text =
          "I had this story from one who had no business to tell it to me,";
      assertTrue(getChapter(epub2Result, 0, 0).getText().startsWith(text));
    }
    
    @Test
    public void testEpub2FirstChapterTextMiddle() {
      String text =
          "asked Clayton.\n\n\"Mutiny!";
      assertTrue(getChapter(epub2Result, 0, 0).getText().contains(text));
    }
    
    @Test
    public void testEpub2FirstChapterTextEnd() {
      String text = " and wait for whatever may come.\"";
      assertTrue(getChapter(epub2Result, 0, 0).getText().endsWith(text));
    }

    @Test
    public void testEpub2LastChapterTextStart() {
      String text =
          "At the sight of Jane, cries of relief ";
      assertTrue(getChapter(epub2Result, 0, 27).getText().startsWith(text));
    }
    
    @Test
    public void testEpub2LastChapterTextMiddle() {
      String text =
          "bowed and beamed.\n\nCanler introduced him";
      assertTrue(getChapter(epub2Result, 0, 27).getText().contains(text));
    }
    
    @Test
    public void testEpub2LastChapterTextEnd() {
      String text = "knew who my father was.\"";
      assertTrue(getChapter(epub2Result, 0, 27).getText().endsWith(text));
    }

    private Chapter getChapter(ImportResult result, int partNumber, int chapterNumber) {
      return result.getParts().get(partNumber).getChapters().get(chapterNumber);
    }
}
