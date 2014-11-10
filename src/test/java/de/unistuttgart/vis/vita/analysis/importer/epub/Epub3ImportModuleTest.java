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

public class Epub3ImportModuleTest {
  ImportResult epub3Result;

  @Before
  public void setUp() throws URISyntaxException, FileNotFoundException, IOException,
      ParseException, NoExtractorFoundException {
    Path epub3TestPath =
        Paths.get(getClass().getResource("moby-dick-mo-20120214-parts.epub").toURI());
    EpubImportModule epub3ImportModule = new EpubImportModule(epub3TestPath);
    epub3Result = epub3ImportModule.execute(null, null);
  }


  @Test
  public void testMetadataTitle(){
    // TODO: ADD DATA
    String text = "todo";
    assertEquals(text,epub3Result.getMetadata().getTitle());
  }
  
  @Test
  public void testMetadataAuthor(){
    // TODO: ADD DATA
    String text = "todo";
    assertEquals(text,epub3Result.getMetadata().getAuthor());
  }

  @Test
  public void testMetadataEdition(){
    // TODO: ADD DATA
    String text = "todo";
    assertEquals(text,epub3Result.getMetadata().getEdition());
  }

  @Test
  public void testMetadataGenre(){
    // TODO: ADD DATA
    String text = "todo";
    assertEquals(text,epub3Result.getMetadata().getGenre());
  }
  
  @Test
  public void testMetadataPublisher(){
    // TODO: ADD DATA
    String text = "todo";
    assertEquals(text,epub3Result.getMetadata().getPublisher());
  }
  

  @Test
  public void testMetadataPublishYear(){
    // TODO: ADD DATA
    int text = -1;
    assertEquals(text,epub3Result.getMetadata().getPublishYear());
  }
  
  @Test
  public void testEpub3Sizes() {
    // parts size
    assertEquals(3, epub3Result.getParts().size());

    // chapters size of first part
    assertEquals(1, epub3Result.getParts().get(0).getChapters().size());

    // chapters size of second part
    assertEquals(48, epub3Result.getParts().get(1).getChapters().size());

    // chapters size of third part
    assertEquals(87, epub3Result.getParts().get(2).getChapters().size());
  }

  @Test
  public void testEpub3PartTitle() {
    assertEquals("Part I", epub3Result.getParts().get(0).getTitle());
    assertEquals("Part Ib", epub3Result.getParts().get(1).getTitle());
    assertEquals("Part II", epub3Result.getParts().get(2).getTitle());
  }

  @Test
  public void testEpub3ChaptersTitle() {
    assertEquals("Chapter 1. Loomings.", getChapter(epub3Result, 0, 0).getTitle());
    assertEquals("Chapter 2. The Carpet-Bag.", getChapter(epub3Result, 1, 0).getTitle());
    assertEquals("Chapter 4. The Counterpane.", getChapter(epub3Result, 1, 2).getTitle());
    assertEquals("Chapter 49. The Hyena.", getChapter(epub3Result, 1, 47).getTitle());
    assertEquals("Chapter 50. Ahab’s Boat and Crew. Fedallah.", getChapter(epub3Result, 2, 0)
        .getTitle());
    assertEquals("Chapter 61. Stubb Kills a Whale.", getChapter(epub3Result, 2, 11).getTitle());
    assertEquals("Epilogue", getChapter(epub3Result, 2, 86).getTitle());    
  }

  @Test
  public void testEpub3FirstChapterTextStart() {
    String text =
        "Call me Ishmael. Some years ago—never mind how long precisely—having"
            + " little or no money in my purse, and nothing particular to interest me on shore,";
    assertTrue(getChapter(epub3Result, 0, 0).getText().startsWith(text));
  }
  
  @Test
  public void testEpub3FirstChapterTextMiddle() {
    String text =
        "like this:\n\n" + "“GRAND CONTESTED ELECTION FOR THE PRESIDENCY OF THE UNITED STATES."
            + "\n\n“WHALING";
    assertTrue(getChapter(epub3Result, 0, 0).getText().contains(text));
  }
  
  @Test
  public void testEpub3FirstChapterTextEnd() {
    String text = "like a snow hill in the air.";
    assertTrue(getChapter(epub3Result, 0, 0).getText().endsWith(text));
  }

  @Test
  public void testEpub3LastChapterTextStart() {
    String text =
        "“AND I ONLY";
    assertTrue(getChapter(epub3Result, 2, 86).getText().startsWith(text));
  }
  
  @Test
  public void testEpub3LastChapterTextMiddle() {
    String text =
        "the wreck.\n\nIt so chanced,";
    assertTrue(getChapter(epub3Result, 2, 86).getText().contains(text));
  }
  
  @Test
  public void testEpub3LastChapterTextEnd() {
    String text = " only found another orphan.";
    assertTrue(getChapter(epub3Result, 2, 86).getText().endsWith(text));
  }

  private Chapter getChapter(ImportResult result, int partNumber, int chapterNumber) {
    return result.getParts().get(partNumber).getChapters().get(chapterNumber);
  }
}
