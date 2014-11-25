package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.analyzers.BigHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.analyzers.SimpleWhitelinesChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;

public class SimpleWhitelinesChapterAnalyzerTxtTest {

  ChapterPosition chapterPosition;
  SimpleWhitelinesChapterAnalyzer analyzer;

  @Before
  public void prepareChapterPosition() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("SimpleWhitelinesChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);
    this.analyzer = new SimpleWhitelinesChapterAnalyzer(importer.getLines());
    this.chapterPosition = analyzer.call();
  }

  @Test
  public void testStartPosition() {
    int startPosition = this.analyzer.getStartOfAnalysis();
    assertTrue(startPosition >= 74);
    assertTrue(startPosition <= 78);
  }

  @Test
  public void testFirstChapter() {
    int chapterNumber = 1;

    assertFalse(chapterPosition.hasHeading(chapterNumber));

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 79);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 81);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 259);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 261);
  }

  @Test
  public void testSecondChapter() {
    int chapterNumber = 2;

    assertFalse(chapterPosition.hasHeading(chapterNumber));

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 260);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 262);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 399);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 401);
  }

  @Test
  public void testThirdChapter() {
    int chapterNumber = 3;

    assertFalse(chapterPosition.hasHeading(chapterNumber));

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 400);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 402);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 563);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 569);
  }

  @Test
  public void testChapterSize() {
    assertTrue(chapterPosition.size() == 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new BigHeadingChapterAnalyzer(null);
  }

}
