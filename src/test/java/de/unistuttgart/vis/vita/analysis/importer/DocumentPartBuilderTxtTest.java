package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.output.DocumentPartBuilder;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.importer.txt.util.TxtModuleLine;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

public class DocumentPartBuilderTxtTest {

  @Test
  public void testBuildDocumentPart() {
    List<Line> lines = new ArrayList<Line>();
    ChapterPosition position = new ChapterPosition();

    String firstChapterHeading = "First Chapter Heading";
    String secondChapterHeading = "Second Chapter Heading";
    String thirdChapterHeading = "Chapter 3";
    String fourthChapterHeading = "Chapter 4";
    String firstChapterText = "Text Of First Chapter Blabla bla bla blubb";
    String secondChapterText = "Text Of Second Chapter Blabla bla bla blubb";
    String thirdChapterText = "Text Of Third Chapter Blabla bla bla blubb";
    String fourthChapterText = "Text Of Fourth Chapter";

    lines.add(new TxtModuleLine("First Chapter Heading", true));
    lines.add(new TxtModuleLine("", true));
    lines.add(new TxtModuleLine("Text Of First Chapter", true));
    lines.add(new TxtModuleLine("Blabla bla bla blubb", true));
    lines.add(new TxtModuleLine("", true));
    lines.add(new TxtModuleLine("Second Chapter Heading", true));
    lines.add(new TxtModuleLine("Text Of Second Chapter", true));
    lines.add(new TxtModuleLine("Blabla bla bla blubb", true));
    lines.add(new TxtModuleLine("", true));
    lines.add(new TxtModuleLine("Text Of Third Chapter", true));
    lines.add(new TxtModuleLine("Blabla bla bla blubb", true));
    lines.add(new TxtModuleLine("", true));
    lines.add(new TxtModuleLine("Text Of Fourth Chapter", true));

    position.addChapter(0, 2, 3);
    position.addChapter(4, 6, 7);
    position.addChapter(8, 8, 10);
    position.addChapter(12, 12, 12);

    DocumentPartBuilder builder = new DocumentPartBuilder(lines, position);
    DocumentPart vitaDocumentPart = builder.call();

    List<Chapter> chapters = vitaDocumentPart.getChapters();
    assertEquals(firstChapterHeading, chapters.get(0).getTitle());
    assertEquals(firstChapterText, chapters.get(0).getText());

    assertEquals(secondChapterHeading, chapters.get(1).getTitle());
    assertEquals(secondChapterText, chapters.get(1).getText());

    assertEquals(thirdChapterHeading, chapters.get(2).getTitle());
    assertEquals(thirdChapterText, chapters.get(2).getText());

    assertEquals(fourthChapterHeading, chapters.get(3).getTitle());
    assertEquals(fourthChapterText, chapters.get(3).getText());
  }
}
