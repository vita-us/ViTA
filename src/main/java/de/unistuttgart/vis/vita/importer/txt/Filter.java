package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Filter filters unnecessary comments in the entireEbookList and removes them
 * 
 *
 */
public class Filter {

  private static final String DEFAULT_COMMENT_FILTER = ".*(\\[).+(\\]).*";
  private static final String DEFAULT_COMMENT_BEGIN = ".*(\\[).*";
  private static final String DEFAULT_COMMENT_END_1 = ".*(\\]).*";
  private static final String DEFAULT_COMMENT_END_2 = ".*(\\])\\s*";
  private static final String DEFAULT_COMMENT_END_3 = ".*(\\])\\s*[a-zA-Z0-9]+.*";
  private static final String REPLACE_DEFAULT_COMMENT = "(\\[).+(\\])";
  private static final String REPLACE_DEFAULT_COMMENT_BEGIN = "(\\[).*";
  private static final String REPLACE_DEFAULT_COMMENT_END_3 = ".*(\\])";
  private static final String DEFAULT__VERIFY_END_BRACKET = ".*(\\]).*[^\\[]*";

  private List<Line> entireEbookList = new ArrayList<Line>();

  public Filter(List<Line> newEntireEbookList) {
    this.entireEbookList = newEntireEbookList;
  }

  /**
   * Filters unnecessary comments in the entireEbookList and removes them
   * 
   * @return the edited entireEbookList
   */
  public List<Line> filterEbookText() {
    // In this line the part with "[.*" or ".*]" is removed
    String editedLine;

    // The removeList contains the lines, which are between "[.*" and ".*]" or which ends with ".*]"
    List<Line> removeList = new ArrayList<Line>();

    // The defaultCommentMap contains the positions of entireEbookList and the edited lines
    Map<Integer, Line> defaultCommentMap = new HashMap<Integer, Line>();

    for (Line line : entireEbookList) {
      if (line.getText() != null) {
        if (line.getText().matches(DEFAULT_COMMENT_FILTER)) {
          editedLine = line.getText().replaceAll(REPLACE_DEFAULT_COMMENT, "");
          defaultCommentMap.put(entireEbookList.indexOf(line), new Line(editedLine));

        } else if (line.getText().matches(DEFAULT_COMMENT_BEGIN)) {
          if (existsEndBracket(line)) {
            editedLine = line.getText().replaceAll(REPLACE_DEFAULT_COMMENT_BEGIN, "");
            defaultCommentMap.put(entireEbookList.indexOf(line), new Line(editedLine));
            addElementsToRemoveListAndMap(removeList, defaultCommentMap, line);
          } else {
            continue;
          }
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
    return entireEbookList;

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
        editedLine = entireEbookList.get(i).getText().replaceAll(REPLACE_DEFAULT_COMMENT_END_3, "");
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
      if (!entireEbookList.get(i).getText().matches(".*\\[.*")) {
        if (entireEbookList.get(i).getText().matches(DEFAULT__VERIFY_END_BRACKET)) {
          return true;
        }
      } else {
        return false;
      }
    }
    return false;
  }
}
