package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.output.ChapterBuilder;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.Chapter;

public class ChapterBuilderTxtTest {

  @Test
  public void testSpacesAtTheBeginning() {
    ArrayList<Line> heading = new ArrayList<Line>();
    ArrayList<Line> text = new ArrayList<Line>();

    String headingString = "This is the Heading.";
    String textString = "blablabla";
    
    heading.add(new Line(" This is the Heading. ", true));

    text.add(new Line(" blablabla ", true));


    ChapterBuilder builder = new ChapterBuilder(heading, text, 0);
    Chapter vitaChapter = builder.call();
    
    assertEquals(headingString, vitaChapter.getTitle());
    assertEquals(textString, vitaChapter.getText());
  }

  @Test
  public void testWhitelinesAtTheBeginning() {
    ArrayList<Line> heading = new ArrayList<Line>();
    ArrayList<Line> text = new ArrayList<Line>();

    String headingString = "This is the Heading.";
    String textString = "blablabla";

    heading.add(new Line(" ", true));
    heading.add(new Line(" This is the Heading. ", true));
    heading.add(new Line("", true));

    heading.add(new Line("", true));
    text.add(new Line(" blablabla ", true));
    heading.add(new Line(" ", true));

    ChapterBuilder builder = new ChapterBuilder(heading, text, 0);
    Chapter vitaChapter = builder.call();

    assertEquals(headingString, vitaChapter.getTitle());
    assertEquals(textString, vitaChapter.getText());
  }

  @Test
  public void testReduceWhitelines() {
    ArrayList<Line> heading = new ArrayList<Line>();
    ArrayList<Line> text = new ArrayList<Line>();

    String headingString = "This is the\n\n\nHeading.";
    String textString = "1blablabla\n\n2blablabla";

    heading.add(new Line(" This is the ", true));
    heading.add(new Line(" ", true));
    heading.add(new Line("", true));
    heading.add(new Line(" Heading. ", true));


    text.add(new Line(" 1blablabla ", true));
    text.add(new Line("", true));
    text.add(new Line(" ", true));
    text.add(new Line(" 2blablabla ", true));


    ChapterBuilder builder = new ChapterBuilder(heading, text, 0);
    Chapter vitaChapter = builder.call();

    assertEquals(headingString, vitaChapter.getTitle());
    assertEquals(textString, vitaChapter.getText());
  }

  @Test
  public void testConcatenateTextLines() {
    ArrayList<Line> heading = new ArrayList<Line>();
    ArrayList<Line> text = new ArrayList<Line>();

    String textString = "1blablabla 2blablabla";

    text.add(new Line(" 1blablabla ", true));
    text.add(new Line(" 2blablabla ", true));


    ChapterBuilder builder = new ChapterBuilder(heading, text, 0);
    Chapter vitaChapter = builder.call();

    assertEquals(textString, vitaChapter.getText());
  }

  @Test
  public void testMarkedHeading() {
    ArrayList<Line> heading = new ArrayList<Line>();
    ArrayList<Line> text = new ArrayList<Line>();

    String headingString = " This is the\n\n\nHeading.";

    heading.add(new Line(" # This is the ", true));
    heading.add(new Line(" ", true));
    heading.add(new Line("", true));
    heading.add(new Line(" #Heading. ", true));

    ChapterBuilder builder = new ChapterBuilder(heading, text, 0);
    Chapter vitaChapter = builder.call();

    assertEquals(headingString, vitaChapter.getTitle());
  }

  @Test
  public void testNullInput() {
    ArrayList<Line> heading = null;
    ArrayList<Line> text = null;

    String headingString = "";
    String textString = "";

    ChapterBuilder builder = new ChapterBuilder(heading, text, 0);
    Chapter vitaChapter = builder.call();

    assertEquals(headingString, vitaChapter.getTitle());
    assertEquals(textString, vitaChapter.getText());
  }

}
