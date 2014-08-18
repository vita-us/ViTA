package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSplitter {

  private static final String START_OF_REGEX = "...\\s*START OF.+\\s*...";
  private static final String END_OF_REGEX = "...\\s*END OF.+\\s*...";
  private static final String TEXTDISTINCTION_REGEX = "START OF.+[^\\p{Punct}{3}]";
  private List<Line> metadataList = new ArrayList<>();
  private List<Line> textList = new ArrayList<>();
  private String textDistinction = "";

  public TextSplitter(List<Line> lines) {
    this.textList = lines;
  }

  public List<Line> getMetadataSection() {
    List<Line> removeTextElements = new ArrayList<Line>();
    if (containsMetadataSection(textList)) {
      for (Line line : textList) {
        if (line.getText() != null) {
          if (line.getText().matches(START_OF_REGEX)) {
            Pattern pattern = Pattern.compile(TEXTDISTINCTION_REGEX);
            Matcher matcher = pattern.matcher(line.getText());
            if (matcher.find()) {
              textDistinction = matcher.group(0);
              textDistinction = textDistinction.replace("START OF", "");
              textDistinction = textDistinction.trim();
            }
            break;

          } else {
            metadataList.add(line);
            removeTextElements.add(line);
          }
        }
      }
      textList.removeAll(removeTextElements);
      removeTextElements.clear();
    }
    return metadataList;
  }

  public List<Line> getTextSection() {
    int position = 0;
    List<Line> removeRestElements = new ArrayList<Line>();
    if (containsTextSection(textList) && !metadataList.isEmpty()) {
      for (Line line : textList) {
        if (line.getText() != null) {
          if (line.getText().matches(END_OF_REGEX) && line.getText().contains(textDistinction)) {
            position = textList.indexOf(line);
            break;
          }
        }
      }
      removeElements(position, removeRestElements);
    }
    return textList;
  }

  private void removeElements(int position, List<Line> removeRestElementsList) {
    for (int i = position; i < textList.size(); i++) {
      removeRestElementsList.add(textList.get(i));
    }
    textList.removeAll(removeRestElementsList);
    removeRestElementsList.clear();
  }


  public boolean containsMetadataSection(List<Line> newTextList) {
    for (Line line : newTextList) {
      if (line.getText() != null) {
        if (line.getText().matches(START_OF_REGEX)) {
          return true;
        }
      }
    }
    return false;

  }

  public boolean containsTextSection(List<Line> newTextList) {
    for (Line line : newTextList) {
      if (line.getText() != null) {
        if (line.getText().matches(END_OF_REGEX) && line.getText().contains(textDistinction)) {
          return true;
        }
      }
    }
    return false;
  }

}
