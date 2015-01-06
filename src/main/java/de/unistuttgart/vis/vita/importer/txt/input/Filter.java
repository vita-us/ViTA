package de.unistuttgart.vis.vita.importer.txt.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unistuttgart.vis.vita.importer.txt.util.TxtModuleLine;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineType;

/**
 * The Filter filters unnecessary comments in the entireEbookList and removes them
 */
public class Filter {

  //Default comments and to remove comments
  private static final String DEFAULT_BEGIN_BRACKET = "(\\[)";
  private static final String DEFAULT_END_BRACKET = "(\\])";
  private static final String DEFAULT_COMMENT = "(?s).*\\[.*\\].*";
  private static final String REMOVE_COMMENT = "\\[.*?\\]";
  private static final String DEFAULT_BEGIN_COMMENT = ".*\\[.*[^\\]]";
  private static final String REMOVE_BEGIN_COMMENT = "\\[.*";
  private static final String DEFAULT_END_COMMENT = ".*\\].*";
  private static final String REMOVE_END_COMMENT = ".*\\]";
  private static final String BEGIN_COMMENT_WITH_WHITESPACES = "\\s{3}.*";

  // All characters without "]" character
  private static final String DEFAULT_CHARACTERS_EX_END_BRACKET = "[\\w\\s\\p{Punct}&&[^\\]]]*";

  // All characters with "[" character, but it must not start with "]" character and this character
  // must not appear after "[" character
  private static final String DEFAULT_CHARACTERS_WITH_BEGIN_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_BEGIN_BRACKET
      + DEFAULT_CHARACTERS_EX_END_BRACKET + ")+";
  
  //All characters and it must have "]" character
  private static final String DEFAULT_CHARACTERS_WITH_END_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_END_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET
      + ")+";
  
  // All characters with "[" and "]" characters, but it must not start with "]" character, this character
  // must not appear after "[" and "]" characters 
  private static final String DEFAULT_CHARACTERS_WITH_BEGIN_EX_END_END_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_BEGIN_BRACKET
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_END_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET
      + ")+";

  // All characters with "]" and "[" character, but it must not start with "]" character and this character
  // must not appear after "]" and "[" characters
  private static final String DEFAULT_CHARACTERS_WITH_END_BEGIN_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_END_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET
      + DEFAULT_BEGIN_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET + ")+";

  private static final String MULTIPLE_WHITESPACES = "\\s+";
  private List<Line> entireEbookList = new ArrayList<Line>();


  /**
   * The commited lines will be used in the methods of this class
   * 
   * @param newEntireEbookList
   */
  public Filter(List<Line> newEntireEbookList) {
    this.entireEbookList = newEntireEbookList;
  }

  /**
   * Filters unnecessary comments and most lines with special signs in the entireEbookList and
   * removes them
   *
   * @return the edited entireEbookList
   */
  public List<Line> filterEbookText() {
    removeComments();
    removeSpecialSigns(entireEbookList);
    return entireEbookList;
  }

  /**
   * Calls the two listed methods
   */
  private void removeComments() {
    removeOneLineDefaultComments();
    removeMultipleLinesComments();
  }

  /**
   * Removes the unnecessary comments with begin and end bracket("[...]")
   */
  private void removeOneLineDefaultComments() {
    Map<Integer, Line> editedLinesMap = new HashMap<Integer, Line>();
    String editedLine;

    for (Line line : entireEbookList) {
      if (line.getText().matches(DEFAULT_COMMENT)) {
        editedLine = replaceMultipleWhitespaces(line, REMOVE_COMMENT);
        editedLinesMap.put(entireEbookList.indexOf(line), new TxtModuleLine(editedLine));
      }
    }
    setEditedLines(editedLinesMap);
    editedLinesMap.clear();
  }

  /**
   * Sets the edited lines in the entireEbookList
   * 
   * @param editedLinesMap
   */
  private void setEditedLines(Map<Integer, Line> editedLinesMap) {
    for (Map.Entry<Integer, Line> entry : editedLinesMap.entrySet()) {
      entireEbookList.set(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Removes the unnecessary comments in multiple lines
   */
  private void removeMultipleLinesComments() {
    Map<Integer, Line> editedLinesMap = new HashMap<Integer, Line>();
    List<Line> removeLines = new ArrayList<Line>();
    String editedLine;

    for (Line line : entireEbookList) {
      if (line.getText().matches(DEFAULT_BEGIN_COMMENT)) {
        if (existsEndBracket(line)) {
          addLinesToRemove(removeLines, line, editedLinesMap);
          editedLine = replaceMultipleWhitespaces(line, REMOVE_BEGIN_COMMENT);
          editedLinesMap.put(entireEbookList.indexOf(line), new TxtModuleLine(editedLine));
        }
      }
    }
    setEditedLines(editedLinesMap);
    entireEbookList.removeAll(removeLines);
    removeLines.clear();
    editedLinesMap.clear();

  }

  /**
   * Add the lines, which are between begin and end bracket to removeLines and edits the line with
   * the end bracket
   * 
   * @param removeLines
   * @param line
   * @param editedLinesMap
   */
  private void addLinesToRemove(List<Line> removeLines, Line line, Map<Integer, Line> editedLinesMap) {

    for (int i = entireEbookList.indexOf(line) + 1; i < entireEbookList.size(); i++) {
      if (entireEbookList.get(i).getText().matches(DEFAULT_END_COMMENT)) {
        String editedLine = replaceMultipleWhitespaces(entireEbookList.get(i), REMOVE_END_COMMENT);
        editedLinesMap.put(i, new TxtModuleLine(editedLine));
        break;
      } else {
        removeLines.add(entireEbookList.get(i));
      }
    }
  }

  /**
   * Checks whether one of the following lines after the current line have an end bracket
   */
  private boolean existsEndBracket(Line currentLine) {
    for (int i = entireEbookList.indexOf(currentLine) + 1; i < entireEbookList.size(); i++) {
      if (entireEbookList.get(i).getText().matches(DEFAULT_CHARACTERS_WITH_BEGIN_BRACKET)) {
        return false;
      } else if (entireEbookList.get(i).getText()
          .matches(DEFAULT_CHARACTERS_WITH_BEGIN_EX_END_END_BRACKET)) {
        return false;
      } else if (entireEbookList.get(i).getText()
          .matches(DEFAULT_CHARACTERS_WITH_END_BEGIN_BRACKET)) {
        return true;
      } else if (entireEbookList.get(i).getText().matches(DEFAULT_CHARACTERS_WITH_END_BRACKET)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Replaces all occurences of the found regex and replaces all multiple whitespaces between
   * Strings with a single whitespace.
   *
   * @param editLine Line - The line to edit.
   * @param regex String - The regex which determines what should be removed.
   * @return String - The edited line text. The line itself is not changed.
   */
  private String replaceMultipleWhitespaces(Line editLine, String regex) {
    String editStringLine;

    if (editLine.getText().matches(BEGIN_COMMENT_WITH_WHITESPACES)) {
      editStringLine = editLine.getText().replaceAll(regex, "");
      return editStringLine;

    } else {
      editStringLine = editLine.getText().replaceAll(regex, " ");
      return editStringLine.replaceAll(MULTIPLE_WHITESPACES, " ");
    }
  }


  /**
   * Removes Lines from the given List, if their type is SpecialSign.
   *
   * @param removeList List of Line - The Lines from which Special Signs should be removed.
   */
  private void removeSpecialSigns(List<Line> removeList) {
    for (int index = removeList.size() - 1; index >= 0; index--) {
      if (removeList.get(index).isType(LineType.SPECIALSIGNS)) {
        if (areWhitelinesAround(index, removeList)) {
          removeList.remove(index + 1);
          removeList.remove(index);
        } else {
          removeList.remove(index);
        }
      }
    }
  }

  /**
   * Checks if there is a Whiteline before and after the given Line.
   *
   * @param index int - The index of the Line in the removeList.
   * @param removeList List of Line - The full List containing the Line to check.
   * @return boolean - true: There is a Whiteline before and after the Line to check. false: There
   *         is no Whiteline before and after the Line to check.
   */
  private boolean areWhitelinesAround(int index, List<Line> removeList) {
    boolean whitelinesAround = false;
    if (index > 0 && index < removeList.size() - 1) {
      whitelinesAround =
          removeList.get(index + 1).isType(LineType.WHITELINE)
              && removeList.get(index - 1).isType(LineType.WHITELINE);
    }
    return whitelinesAround;
  }
}
