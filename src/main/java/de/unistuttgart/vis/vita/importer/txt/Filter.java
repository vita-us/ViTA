package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctc.wstx.io.EBCDICCodec;

public class Filter {

  private static final String DEFAULT_COMMENT_FILTER = ".*(\\[).+(\\]).*";
  private static final String DEFAULT_COMMENT_BEGIN = ".*(\\[).*";
  private static final String DEFAULT_COMMENT_END_1 = ".*(\\]).*";
  private static final String DEFAULT_COMMENT_END_2 = ".*(\\])\\s*";
  private static final String DEFAULT_COMMENT_END_3 = ".*(\\])\\s*[a-zA-Z0-9]+.*";
  private static final String REPLACE_DEFAULT_COMMENT = "(\\[).+(\\])";
  private static final String REPLACE_DEFAULT_COMMENT_BEGIN = "(\\[).*";
  private static final String REPLACE_DEFAULT_COMMENT_END_3 = ".*(\\])";
  private List<Line> entireEbookLines = new ArrayList<Line>();

  public Filter(List<Line> newEntireEbookLines) {
    this.entireEbookLines = newEntireEbookLines;
  }

  public List<Line> filterEbookText() {
    List<Line> removeList = new ArrayList<Line>();
    Map<Integer, Line> defaultCommentMap = new HashMap<Integer, Line>();
    String editedLine;
    for (Line line : entireEbookLines) {
      if (line.getText() != null) {
        if (line.getText().matches(DEFAULT_COMMENT_FILTER)) {
          editedLine = line.getText().replaceAll(REPLACE_DEFAULT_COMMENT, "");
          defaultCommentMap.put(entireEbookLines.indexOf(line), new Line(editedLine));

        } else if (line.getText().matches(DEFAULT_COMMENT_BEGIN)) {
          editedLine = line.getText().replaceAll(REPLACE_DEFAULT_COMMENT_BEGIN, "");
          defaultCommentMap.put(entireEbookLines.indexOf(line), new Line(editedLine));
          removeComments(removeList, defaultCommentMap, line);
        }
      }
    }

    for (Map.Entry<Integer, Line> entry : defaultCommentMap.entrySet()) {
      entireEbookLines.set(entry.getKey(), entry.getValue());
    }
    entireEbookLines.removeAll(removeList);
    removeList.clear();
    return entireEbookLines;

  }

  private void removeComments(List<Line> removeList, Map<Integer, Line> defaultCommentMap, Line line) {
    String editedLine;
    for (int i = entireEbookLines.indexOf(line) + 1; i < entireEbookLines.size(); i++) {

      if (!entireEbookLines.get(i).getText().matches(DEFAULT_COMMENT_END_1)) {
        removeList.add(entireEbookLines.get(i));

      } else if (entireEbookLines.get(i).getText().matches(DEFAULT_COMMENT_END_2)) {
        removeList.add(entireEbookLines.get(i));
        break;

      } else if (entireEbookLines.get(i).getText().matches(DEFAULT_COMMENT_END_3)) {
        editedLine =
            entireEbookLines.get(i).getText().replaceAll(REPLACE_DEFAULT_COMMENT_END_3, "");
        defaultCommentMap.put(i, new Line(editedLine));
        break;
      }
    }
  }
}
