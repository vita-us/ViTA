package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Implements Callable - returning a Chapter. <br>
 * <br>
 * The ChapterBuilder is the link between Chapter Analyzer and the Chapter Objects used by all other
 * components of ViTA. It transforms the extracted Lines into String parameters for the Chapter and
 * assures these Strings have the correct text style.<br>
 * <br>
 * The structure of given List-parameters can be changed by this class.
 */
public class ChapterBuilder implements Callable<Chapter> {
  // Regex for detection of whitespace in a line
  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  private static final String WHITESPACEATTHEBEGINNING = "^" + WHITESPACE;
  private static final String WHITESPACEATTHEEND = WHITESPACE + "$";

  private ArrayList<Line> heading;
  private ArrayList<Line> text;
  private int chapterNumber;

  /**
   * Instantiates a new ChapterBuilder, the Chapter will be build when calling the method 'call()'.
   * To build a Chapter, the ChapterBuilder needs the number of the chapter in its DocumentPart.<br>
   * <br>
   * The structure of given List-parameters can be changed by this class.
   * 
   * @param heading ArrayList<Line> - The title of the Chapter. Null interpreted as empty.
   * @param text ArrayList<Line> - The text of the Chapter, Null interpreted as empty.
   * @param chapterNumber int - The Number of the Chapter in the Document
   */
  public ChapterBuilder(ArrayList<Line> heading, ArrayList<Line> text, int chapterNumber) {
    if (heading == null) {
      heading = new ArrayList<Line>();
      heading.add(new Line("", false));
    }

    if (text == null) {
      text = new ArrayList<Line>();
      text.add(new Line("", false));
    }

    this.heading = heading;
    this.text = text;
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
   * @param text - The text of the Chapter in a unified form.
   * @return Chapter - The Chapter with all known attributes set.
   */
  private Chapter buildChapter(String heading, String text) {
    Chapter chapter = new Chapter();
    chapter.setNumber(chapterNumber);
    chapter.setTitle(heading);
    chapter.setText(text);
    return chapter;
  }

  /**
   * Transforms the List of Lines into a String. A line break is added at the end of every Line.
   * 
   * @param lines ArrayList<Lines> - The Lines to transform.
   * @return String - Text of the Lines as a String.
   */
  private String buildString(ArrayList<Line> lines) {
    String result = "";
    StringBuilder content = new StringBuilder();

    for (Line line : lines) {
      content.append("\n");
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
   * @param text ArrayList<Line> - The Lines which should be unified in Chapter text form. They
   *        contain the result after the method execution.
   */
  private void unifyTextStyle(ArrayList<Line> text) {
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
   * @param heading ArrayList<Line> - The Lines which should be unified in Chapter title form. They
   *        contain the result after the method execution.
   */
  private void unifyHeadingStyle(ArrayList<Line> heading) {
    deleteSpaceCharactersAtTheBeginningOfAllLines(heading);
    deleteSpaceCharactersAtTheEndOfAllLines(heading);
    deleteMarkedHeadingSymbol(heading);
    deleteWhitelinesAtTheBeginning(heading);
    deleteWhitelinesAtTheEnd(heading);
  }

  /**
   * Lines which are not Whitelines will be concatenated until there is a Whiteline. A space
   * character is added between two texts, so there should be no space characters at the beginning
   * or end of a line. <br>
   * <br>
   * It is recommended to deactivate the LineType computation for reasons of performance.<br>
   * <br>
   * Example: <br>
   * <br>
   * List before:<br>
   * Text1<br>
   * Text2<br>
   * Text3<br>
   * Whiteline<br>
   * Text4<br>
   * Text5<br>
   * <br>
   * 
   * List after:<br>
   * Text1 Text2 Text3<br>
   * Whiteline<br>
   * Text4 Text5
   * 
   * @param lines ArrayList<Line> - The Lines to transform. They contain the result after the method
   *        execution.
   */
  private void concatenateTextLines(ArrayList<Line> lines) {
    boolean lineBeforeWasNotWhiteline = false;

    for (int index = lines.size() - 1; index >= 0; index--) {
      if (!lines.get(index).getType().equals(LineType.WHITELINE)) {
        if (lineBeforeWasNotWhiteline) {
          Line lineBefore = lines.get(index + 1);
          Line line = lines.get(index);
          String textBefore = lineBefore.getText();
          String text = line.getText();
          text = text.concat(" " + textBefore);
          line.setText(text);
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
   * Assures there is only one Whiteline in a row.<br>
   * <br>
   * 
   * Example:<br>
   * List before:<br>
   * Text1<br>
   * Whiteline<br>
   * Whiteline<br>
   * Whiteline<br>
   * Text2<br>
   * <br>
   * 
   * List after:<br>
   * Text1<br>
   * Whiteline<br>
   * Text2
   * 
   * @param lines ArrayList<Line> - The Lines to transform. They contain the result after the method
   *        execution.
   */
  private void reduceWhitelines(ArrayList<Line> lines) {
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
   * Deletes Whitelines at the beginning of the list until there is a line which is not a Whiteline.
   * 
   * @param lines ArrayList<Line> - The Lines to transform. They contain the result after the method
   *        execution.
   */
  private void deleteWhitelinesAtTheBeginning(ArrayList<Line> lines) {
    boolean onlyWhitelinesFound = true;
    while (lines.size() > 0 && onlyWhitelinesFound) {
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
   * @param lines ArrayList<Line> - The Lines to transform. They contain the result after the method
   *        execution.
   */
  private void deleteWhitelinesAtTheEnd(ArrayList<Line> lines) {
    boolean onlyWhitelinesFound = true;
    while (lines.size() > 0 && onlyWhitelinesFound) {
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
   * @param lines ArrayList<Line> - The Lines deactivate the computation. No new Types will be
   *        computed, when the text is changed.
   */
  private void deactivateLineTypeComputation(ArrayList<Line> lines) {
    for (Line line : lines) {
      line.setAutomatedTypeComputation(false);
    }
  }

  /**
   * When there are space characters at the beginning of a line, they will be deleted. Does not
   * change the structure of the list.
   * 
   * @param lines ArrayList<Line> - The Lines to transform. They contain the result after the method
   *        execution.
   */
  private void deleteSpaceCharactersAtTheBeginningOfAllLines(ArrayList<Line> lines) {
    for (Line line : lines) {
      deletePattern(line, WHITESPACEATTHEBEGINNING);
    }
  }

  /**
   * When there are space characters at the beginning of a line, they will be deleted. Does not
   * change the structure of the list.
   * 
   * @param lines ArrayList<Line> - The Lines to transform. They contain the result after the method
   *        execution.
   */
  private void deleteSpaceCharactersAtTheEndOfAllLines(ArrayList<Line> lines) {
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
  private void deleteMarkedHeadingSymbol(ArrayList<Line> lines) {
    for (Line line : lines) {
      if (line.getType().equals(LineType.MARKEDHEADING)) {
        line.setText(line.getText().substring(1));
      }
    }
  }

  /**
   * Will delete every found part of the line's text, which fits on the given regex.
   * 
   * @param line Line - The line to delete the occurences from. The line contains the result after
   *        the method execution.
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
