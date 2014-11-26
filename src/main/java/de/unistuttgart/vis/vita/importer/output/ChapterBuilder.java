package de.unistuttgart.vis.vita.importer.output;

import de.unistuttgart.vis.vita.importer.txt.util.TxtModuleLine;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineType;
import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements Callable - returning a Chapter. <br> <br> The ChapterBuilder is the link between
 * Chapter Analyzer and the Chapter Objects used by all other components of ViTA. It transforms the
 * extracted Lines into String parameters for the Chapter and assures these Strings have the correct
 * text style.<br> <br> The structure of given List-parameters can be changed by this class.
 */
public class ChapterBuilder implements Callable<Chapter> {

  // Regex for detection of whitespace in a line
  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  private static final String WHITESPACEATTHEBEGINNING = "^" + WHITESPACE;
  private static final String WHITESPACEATTHEEND = WHITESPACE + "$";

  private String endOfLine = "\n";
  private List<Line> heading;
  private List<Line> text;
  private int chapterNumber;

  /**
   * Instantiates a new ChapterBuilder, the Chapter will be build when calling the method 'call()'.
   * To build a Chapter, the ChapterBuilder needs the number of the chapter in its DocumentPart.<br>
   * <br> The structure of given List-parameters can be changed by this class.
   *
   * @param heading       The title of the Chapter. Null interpreted as empty.
   * @param text          The text of the Chapter, Null interpreted as empty.
   * @param chapterNumber The Number of the Chapter in the Document
   */
  public ChapterBuilder(List<Line> heading, List<Line> text, int chapterNumber) {
    if (heading == null) {
      this.heading = new ArrayList<>();
      this.heading.add(new TxtModuleLine("", false));
    } else {
      this.heading = heading;
    }

    if (text == null) {
      this.text = new ArrayList<>();
      this.text.add(new TxtModuleLine("", false));
    } else {
      this.text = text;
    }

    this.chapterNumber = chapterNumber;
  }

  @Override
  public Chapter call() {
    unifyTextStyle(text);
    unifyHeadingStyle(heading);
    return buildChapter(buildString(heading), buildString(text));
  }

  /**
   * Builds the Chapter and sets all known attributes.
   *
   * @param heading String - The title of the Chapter in a unified form.
   * @param text    - The text of the Chapter in a unified form.
   * @return Chapter - The Chapter with all known attributes set.
   */
  private Chapter buildChapter(String heading, String text) {
    Chapter chapter = new Chapter();
    chapter.setNumber(chapterNumber);
    chapter.setTitle(heading);
    chapter.setText(text);
    chapter.setLength(text.length());
    return chapter;
  }

  /**
   * Transforms the List of Lines into a String. A line break is added at the end of every Line.
   *
   * @param lines ArrayList<Lines> - The Lines to transform.
   * @return String - Text of the Lines as a String.
   */
  private String buildString(List<Line> lines) {
    String result = "";
    StringBuilder content = new StringBuilder();

    for (Line line : lines) {
      content.append(endOfLine);
      content.append(line.getText());
    }

    // Remove line break at the beginning
    if (content.length() > 0) {
      result = content.substring(1);
    }
    return result;
  }

  /**
   * Applies some methods on the given List of Lines to assure the unified form of Chapter texts.
   *
   * @param text The Lines which should be unified in Chapter text form. They contain the result
   *             after the method execution.
   */
  private void unifyTextStyle(List<Line> text) {
    deactivateLineTypeComputation(text);
    deleteWhitelinesAtTheBeginning(text);
    deleteWhitelinesAtTheEnd(text);
    reduceWhitelines(text);
    deleteSpaceCharactersAtTheBeginningOfAllLines(text);
    deleteSpaceCharactersAtTheEndOfAllLines(text);
    concatenateTextLines(text);
  }

  /**
   * Applies some methods on the given List of Lines to assure the unified form of Chapter title.
   *
   * @param heading The Lines which should be unified in Chapter title form. They contain the result
   *                after the method execution.
   */
  private void unifyHeadingStyle(List<Line> heading) {
    deactivateLineTypeComputation(text);
    deleteSpaceCharactersAtTheBeginningOfAllLines(heading);
    deleteSpaceCharactersAtTheEndOfAllLines(heading);
    deleteMarkedHeadingSymbol(heading);
    deleteWhitelinesAtTheBeginning(heading);
    deleteWhitelinesAtTheEnd(heading);
  }

  /**
   * Lines which are not Whitelines will be concatenated until there is a Whiteline. A space
   * character is added between two texts, so there should be no space characters at the beginning
   * or end of a line. <br> <br> It is recommended to deactivate the LineType computation for
   * reasons of performance.<br> <br> Example: <br> <br> List before:<br> Text1<br> Text2<br>
   * Text3<br> Whiteline<br> Text4<br> Text5<br> <br>
   *
   * List after:<br> Text1 Text2 Text3<br> Whiteline<br> Text4 Text5
   *
   * @param lines The Lines to transform. They contain the result after the method execution.
   */
  private void concatenateTextLines(List<Line> lines) {

    boolean lineBeforeWasNotWhiteline = false;

    for (int index = lines.size() - 1; index >= 0; index--) {
      if (!lines.get(index).getType().equals(LineType.WHITELINE)) {
        if (lineBeforeWasNotWhiteline) {
          Line lineBefore = lines.get(index + 1);
          Line line = lines.get(index);
          String lineTextBefore = lineBefore.getText();
          String lineText = line.getText();
          lineText = lineText.concat(" " + lineTextBefore);
          line.setText(lineText);
          lines.remove(index + 1);
        } else {
          lineBeforeWasNotWhiteline = true;
        }
      } else {
        lineBeforeWasNotWhiteline = false;
      }
    }
  }

  /**
   * Assures there is only one Whiteline in a row.<br> <br>
   *
   * Example:<br> List before:<br> Text1<br> Whiteline<br> Whiteline<br> Whiteline<br> Text2<br>
   * <br>
   *
   * List after:<br> Text1<br> Whiteline<br> Text2
   *
   * @param lines The Lines to transform. They contain the result after the method execution.
   */
  private void reduceWhitelines(List<Line> lines) {
    boolean lineBeforeWasWhiteline = false;
    for (int index = lines.size() - 1; index >= 0; index--) {
      if (lines.get(index).getType().equals(LineType.WHITELINE)) {
        if (lineBeforeWasWhiteline) {
          lines.remove(index);
        } else {
          lineBeforeWasWhiteline = true;
        }
      } else {
        lineBeforeWasWhiteline = false;
      }
    }
  }

  /**
   * Deletes Whitelines at the beginning of the list until there is a line which is not a
   * Whiteline.
   *
   * @param lines The Lines to transform. They contain the result after the method execution.
   */
  private void deleteWhitelinesAtTheBeginning(List<Line> lines) {
    boolean onlyWhitelinesFound = true;
    while (!lines.isEmpty() && onlyWhitelinesFound) {
      int firstIndex = 0;
      Line firstLine = lines.get(firstIndex);
      if (firstLine.getType().equals(LineType.WHITELINE)) {
        lines.remove(firstIndex);
      } else {
        onlyWhitelinesFound = false;
      }
    }
  }

  /**
   * Deletes Whitelines at the end of the list until there is a line which is not a Whiteline.
   *
   * @param lines The Lines to transform. They contain the result after the method execution.
   */
  private void deleteWhitelinesAtTheEnd(List<Line> lines) {
    boolean onlyWhitelinesFound = true;
    while (!lines.isEmpty() && onlyWhitelinesFound) {
      int lastIndex = lines.size() - 1;
      Line lastLine = lines.get(lastIndex);
      if (lastLine.getType().equals(LineType.WHITELINE)) {
        lines.remove(lastIndex);
      } else {
        onlyWhitelinesFound = false;
      }
    }
  }

  /**
   * Deactivates the LineType computation of all lines.
   *
   * @param lines The Lines deactivate the computation. No new Types will be computed, when the text
   *              is changed.
   */
  private void deactivateLineTypeComputation(List<Line> lines) {
    for (Line line : lines) {
      line.setAutomatedTypeComputation(false);
    }
  }

  /**
   * When there are space characters at the beginning of a line, they will be deleted. Does not
   * change the structure of the list.
   *
   * @param lines The Lines to transform. They contain the result after the method execution.
   */
  private void deleteSpaceCharactersAtTheBeginningOfAllLines(List<Line> lines) {
    for (Line line : lines) {
      deletePattern(line, WHITESPACEATTHEBEGINNING);
    }
  }

  /**
   * When there are space characters at the beginning of a line, they will be deleted. Does not
   * change the structure of the list.
   *
   * @param lines The Lines to transform. They contain the result after the method execution.
   */
  private void deleteSpaceCharactersAtTheEndOfAllLines(List<Line> lines) {
    for (Line line : lines) {
      deletePattern(line, WHITESPACEATTHEEND);
    }
  }

  /**
   * Used to delete the marker symbol of marked Headings. It must be assured, that this symbol is
   * the first symbol of the line's text.
   *
   * @param lines - The Lines to transform. They contain the result after the method execution.
   */
  private void deleteMarkedHeadingSymbol(List<Line> lines) {
    for (Line line : lines) {
      if (line.getType().equals(LineType.MARKEDHEADING)) {
        line.setText(line.getText().substring(1));
      }
    }
  }

  /**
   * Will delete every found part of the line's text, which fits on the given regex.
   *
   * @param line  Line - The line to delete the occurences from. The line contains the result after
   *              the method execution.
   * @param regex String - A valid regular expression.
   */
  private void deletePattern(Line line, String regex) {
    Matcher matcher = Pattern.compile(regex).matcher(line.getText());
    StringBuffer stringBuffer = new StringBuffer(line.getText().length());

    // Delete occurence
    while (matcher.find()) {
      matcher.appendReplacement(stringBuffer, "");
    }
    matcher.appendTail(stringBuffer);
    line.setText(stringBuffer.toString());
  }
}
