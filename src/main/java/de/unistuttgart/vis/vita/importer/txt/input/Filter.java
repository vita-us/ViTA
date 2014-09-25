package de.unistuttgart.vis.vita.importer.txt.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.importer.txt.util.LineType;

/**
 * The Filter filters unnecessary comments in the entireEbookList and removes them
 * 
 *
 */
public class Filter {

  private static final String DEFAULT_BEGIN_BRACKET = "(\\[)";
  private static final String DEFAULT_END_BRACKET = "(\\])";
  private static final String DEFAULT_COMMENT_FILTER = "(.*[^\\]])(\\[.*\\].*[^\\[\\]])+";
  private static final String DEFAULT_COMMENT_FILTER_WITH_WHITESPACES =
      "(([\\w\\s][\\p{Punct}&&[^\\]]])*\\s+\\[.*\\]\\s+([\\w\\s][\\p{Punct}&&[^\\]]])*)+";
  private static final String DEFAULT_COMMENT_BEGIN_WITH_WHITESPACES =
      ".*\\s+(\\[)[\\w\\s\\p{Punct}&&[^\\]]]*(\\])\\s+.*";
  private static final String DEFAULT_COMMENT_BEGIN = ".*(\\[).*";
  private static final String DEFAULT_COMMENT_END_1 = ".*(\\]).*";
  private static final String DEFAULT_COMMENT_END_2 = ".*(\\])\\s*";
  private static final String DEFAULT_COMMENT_END_3 = ".*(\\])\\s*[a-zA-Z0-9]+.*";
  private static final String REPLACE_DEFAULT_COMMENT = "(\\[)[\\w\\s\\p{Punct}&&[^\\]]]*(\\])";
  private static final String REPLACE_DEFAULT_COMMENT_BEGIN =
      "(\\[)[\\w\\s\\p{Punct}&&[^\\]]]*(\\])*";
  private static final String REPLACE_DEFAULT_COMMENT_END_3 = "[\\w\\s\\p{Punct}&&[^\\]]]*(\\])";

  private static final String DEFAULT_CHARACTERS_EX_END_BRACKET = "[\\w\\s\\p{Punct}&&[^\\]]]*";
  private static final String ALL_CHARACTERS = ".*";
  private static final String REPLACE_ALL_CHARACTERS = "\\[.*\\]";

  private static final String DEFAULT_CHARACTERS_WITH_BEGIN_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_BEGIN_BRACKET
      + DEFAULT_CHARACTERS_EX_END_BRACKET + ")+";

  private static final String DEFAULT_CHARACTERS_WITH_END_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_END_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET
      + ")+";

  private static final String DEFAULT_CHARACTERS_WITH_BEGIN_EX_END_END_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_BEGIN_BRACKET
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_END_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET
      + ")+";
  private static final String DEFAULT_CHARACTERS_WITH_BEGIN_END_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_BEGIN_BRACKET + ALL_CHARACTERS
      + DEFAULT_END_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET + ")+";

  private static final String DEFAULT_CHARACTERS_WHITESPACE_WITH_BEGIN_END_BRACKET_WHITESPACE = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + "\\s*" + DEFAULT_BEGIN_BRACKET
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_END_BRACKET + "\\s*"
      + DEFAULT_CHARACTERS_EX_END_BRACKET + ")+";

  private static final String DEFAULT_CHARACTERS_WITH__END_BEGIN_BRACKET = "("
      + DEFAULT_CHARACTERS_EX_END_BRACKET + DEFAULT_END_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET
      + DEFAULT_BEGIN_BRACKET + DEFAULT_CHARACTERS_EX_END_BRACKET + ")+";

  private static final String MULTIPLE_WHITESPACES = "\\s+";

  private ArrayList<Line> entireEbookList = new ArrayList<Line>();

  public Filter(ArrayList<Line> newEntireEbookList) {
    this.entireEbookList = newEntireEbookList;
  }

  /**
   * Filters unnecessary comments and most lines with special signs in the entireEbookList and
   * removes them
   * 
   * @return the edited entireEbookList
   */
  public ArrayList<Line> filterEbookText() {
    removeComments();
    removeSpecialSigns(entireEbookList);
    return entireEbookList;
  }

  /**
   * Filters unnecessary comments in the entireEbookList and removes them
   */
  private void removeComments() {
    // In this line the part with "[.*" or ".*]" is removed
    String editedLine;

    // The removeList contains the lines, which are between "[.*" and ".*]" or which ends with ".*]"
    List<Line> removeList = new ArrayList<Line>();

    // The defaultCommentMap contains the positions of entireEbookList and the edited lines
    Map<Integer, Line> defaultCommentMap = new HashMap<Integer, Line>();

    for (Line line : entireEbookList) {
      if (line.getText() != null) {
        if (line.getText().matches(DEFAULT_COMMENT_FILTER)) {
          if (line.getText().matches("(.*[^\\]])(\\[(.*[^\\[\\]])\\].*[^\\[\\]])+")) {
            editedLine = replaceMultipleWhitespaces(line, REPLACE_DEFAULT_COMMENT);

          } else {
            editedLine = replaceMultipleWhitespaces(line, "\\[.*\\]");
          }
          defaultCommentMap.put(entireEbookList.indexOf(line), new Line(editedLine));

        } else if (line.getText().matches(DEFAULT_COMMENT_FILTER_WITH_WHITESPACES)) {
          editedLine = replaceMultipleWhitespaces(line, REPLACE_DEFAULT_COMMENT);
          defaultCommentMap.put(entireEbookList.indexOf(line), new Line(editedLine));
        } else if (line.getText().matches(DEFAULT_COMMENT_BEGIN)) {
          verifyExistenceOfEndBracket(removeList, defaultCommentMap, line);
        }
      }
    }


    // Set the new edited line entry.getValue() in the entireEbookList at the position
    // entry.getKey()
    for (Map.Entry<Integer, Line> entry : defaultCommentMap.entrySet()) {
      entireEbookList.set(entry.getKey(), entry.getValue());
    }

    // Remove the unnecessary comments in the entireEbookList
    entireEbookList.removeAll(removeList);
    removeList.clear();
    defaultCommentMap.clear();

  }

  /**
   * Verify the extistence of a end bracket after the current line
   * 
   * @param removeList
   * @param defaultCommentMap
   * @param line
   */
  private void verifyExistenceOfEndBracket(List<Line> removeList,
      Map<Integer, Line> defaultCommentMap, Line line) {
    String editedLine;
    if (existsEndBracket(line)) {
      findAndReplaceComments(removeList, defaultCommentMap, line);

      // if there is not even one line after the current with an end bracket, still the current line
      // could have
      // unnecessary comments, so remove them
    } else if (line.getText().matches(DEFAULT_CHARACTERS_WITH_BEGIN_EX_END_END_BRACKET)) {
      if (line.getText().matches(DEFAULT_CHARACTERS_WHITESPACE_WITH_BEGIN_END_BRACKET_WHITESPACE)) {
        editedLine = replaceMultipleWhitespaces(line, REPLACE_DEFAULT_COMMENT);

      } else {
        editedLine = line.getText().replaceAll(REPLACE_DEFAULT_COMMENT, " ");
      }
      defaultCommentMap.put(entireEbookList.indexOf(line), new Line(editedLine));
    } else if (line.getText().matches(DEFAULT_CHARACTERS_WITH_BEGIN_END_BRACKET)) {
      editedLine = line.getText().replaceAll(REPLACE_ALL_CHARACTERS, "");
      defaultCommentMap.put(entireEbookList.indexOf(line), new Line(editedLine));

    }
  }

  /**
   * Find the lines with unnecessary lines, which come after the current line, and remove those
   * comments
   * 
   * @param removeList
   * @param defaultCommentMap
   * @param line
   */
  private void findAndReplaceComments(List<Line> removeList, Map<Integer, Line> defaultCommentMap,
      Line line) {
    String editedLine;
    // the map could already have the edited lines, which come after the current line, so remove
    // only the
    // "[..." part, because the "...]" is already removed in the addElementsToRemoveListAndMap
    // method
    if (defaultCommentMap.containsKey(entireEbookList.indexOf(line))) {
      editedLine =
          defaultCommentMap.get(entireEbookList.indexOf(line)).getText()
              .replaceAll(REPLACE_DEFAULT_COMMENT_BEGIN, " ");

    } else if (line.getText().matches(DEFAULT_COMMENT_BEGIN_WITH_WHITESPACES)) {
      editedLine = replaceMultipleWhitespaces(line, REPLACE_DEFAULT_COMMENT_BEGIN);

    } else {
      editedLine = line.getText().replaceAll(REPLACE_DEFAULT_COMMENT_BEGIN, " ");
    }
    defaultCommentMap.put(entireEbookList.indexOf(line), new Line(editedLine));
    addElementsToRemoveListAndMap(removeList, defaultCommentMap, line);
  }

  /**
   * Adds the unnecessary comments to the removeList and edited lines to the defaultCommentMap
   * 
   * @param removeList
   * @param defaultCommentMap
   * @param line, which helps to find the current position in the entireEbookList
   */
  private void addElementsToRemoveListAndMap(List<Line> removeList,
      Map<Integer, Line> defaultCommentMap, Line currentLine) {
    String editedLine;
    for (int i = entireEbookList.indexOf(currentLine) + 1; i < entireEbookList.size(); i++) {
      if (!entireEbookList.get(i).getText().matches(DEFAULT_COMMENT_END_1)) {
        removeList.add(entireEbookList.get(i));

      } else if (entireEbookList.get(i).getText().matches(DEFAULT_COMMENT_END_2)) {
        removeList.add(entireEbookList.get(i));
        break;

      } else if (entireEbookList.get(i).getText().matches(DEFAULT_COMMENT_END_3)) {
        editedLine =
            entireEbookList.get(i).getText().replaceAll(REPLACE_DEFAULT_COMMENT_END_3, " ");

        defaultCommentMap.put(i, new Line(editedLine));
        break;
      }
    }
  }

  /**
   * Checks whether one of the following lines after the current line have an end bracket
   * 
   * @param currentLine
   * @return
   */
  private boolean existsEndBracket(Line currentLine) {
    for (int i = entireEbookList.indexOf(currentLine) + 1; i < entireEbookList.size(); i++) {

      if (entireEbookList.get(i).getText().matches(DEFAULT_CHARACTERS_WITH_BEGIN_BRACKET)) {
        return false;

      } else if (entireEbookList.get(i).getText()
          .matches(DEFAULT_CHARACTERS_WITH_BEGIN_EX_END_END_BRACKET)) {
        return false;

      } else if (entireEbookList.get(i).getText()
          .matches(DEFAULT_CHARACTERS_WITH__END_BEGIN_BRACKET)) {
        return true;

      } else if (entireEbookList.get(i).getText().matches(DEFAULT_CHARACTERS_WITH_END_BRACKET)) {
        return true;

      }

    }
    return false;
  }

  /**
   * Replaces multiple whitespaces between Strings
   * 
   * @param editLine
   * @param regex
   * @return
   */
  private String replaceMultipleWhitespaces(Line editLine, String regex) {
    String editStringLine = editLine.getText().replaceAll(regex, " ");
    return editStringLine = editStringLine.replaceAll(MULTIPLE_WHITESPACES, " ");
  }


  /**
   * Removes Lines from the given List, if their type is SpecialSign.
   * 
   * @param removeList List of Line - The Lines from which Special Signs should be removed.
   */
  private void removeSpecialSigns(List<Line> removeList) {
    for (int index = removeList.size() - 1; index >= 0; index--) {
      if (removeList.get(index).getType().equals(LineType.SPECIALSIGNS)) {
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
          removeList.get(index + 1).getType().equals(LineType.WHITELINE)
              && removeList.get(index - 1).getType().equals(LineType.WHITELINE);
    }
    return whitelinesAround;
  }

}
