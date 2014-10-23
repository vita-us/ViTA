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
import de.unistuttgart.vis.vita.importer.txt.analyzers.FullTextChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;

public class FullTextChapterAnalyzerTxtTest {

  ChapterPosition chapterPosition;
  FullTextChapterAnalyzer analyzer;
  int linesLength;

  @Before
  public void prepareChapterPosition() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("FullTextChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);
    this.analyzer = new FullTextChapterAnalyzer(importer.getLines());
    this.chapterPosition = analyzer.call();
    this.linesLength = importer.getLines().size();
  }

  @Test
  public void testStartPosition() {
    int startPosition = this.analyzer.getStartOfAnalysis();
    assertTrue(startPosition == 0);
  }

  @Test
  public void testChapterSize() {
    assertTrue(chapterPosition.size() == 1);
  }

  @Test 
  public void testChapterPosition(){
    assertTrue(chapterPosition.getStartOfHeading(1) == 0);
    assertTrue(chapterPosition.getStartOfText(1) == 0);
    assertTrue(chapterPosition.getEndOfText(1) == linesLength - 1);
    assertFalse(chapterPosition.hasHeading(1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new BigHeadingChapterAnalyzer(null);
  }
}
