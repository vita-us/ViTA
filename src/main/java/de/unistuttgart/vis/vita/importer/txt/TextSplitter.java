package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TextSplitter splits the text lines into metadata and text section
 * 
 *
 */
public class TextSplitter {

  private static final String START_OF_REGEX = "\\*\\*\\*\\s*start of.+\\s*\\*\\*\\*";
  private static final String END_OF_REGEX = "\\*\\*\\*\\s*end of.+\\s*\\*\\*\\*";
  private static final String TEXTDISTINCTION_REGEX = "start of.+[^\\p{Punct}{3}]";
  private List<Line> metadataList = new ArrayList<>();
  private List<Line> textList = new ArrayList<>();
  private String textDistinction = "";

  public TextSplitter(List<Line> lines) {
    this.textList = lines;
    getMetadataSection();
    getTextSection();
  }

  /**
   * Gets the metadata section from the textList
   * 
   * @return contains the metadata lines: metadataList
   */
  private List<Line> getMetadataSection() {
    List<Line> removeTextElements = new ArrayList<Line>();
    Pattern pattern = Pattern.compile(TEXTDISTINCTION_REGEX);
    if (containsMetadataSection(textList)) {
      for (Line line : textList) {
        if (line.getText() != null && line.getText().toLowerCase().matches(START_OF_REGEX)) {
          Matcher matcher = pattern.matcher(line.getText().toLowerCase());
          if (matcher.find()) {
            textDistinction = matcher.group(0);
            textDistinction = textDistinction.replaceAll("(?i)start of", "");
            textDistinction = textDistinction.trim();
          }
          break;

        } else {
          metadataList.add(line);
          removeTextElements.add(line);
        }
      }
      textList.removeAll(removeTextElements);
      removeTextElements.clear();
    }
    return metadataList;

  }


  /**
   * Gets only the text section from the textList
   * 
   * @return contains only the text lines: textList
   */
  private List<Line> getTextSection() {
    int position = 0;
    List<Line> removeRestElements = new ArrayList<Line>();
    if (containsTextSection(textList) && !metadataList.isEmpty()) {
      for (Line line : textList) {
        if (line.getText() != null && line.getText().toLowerCase().matches(END_OF_REGEX)
            && line.getText().toLowerCase().contains(textDistinction)) {
          position = textList.indexOf(line);
          break;
        }
      }
      removeElementsFromPosition(position, removeRestElements);
      textList.remove(0);
    }
    return textList;
  }



  /**
   * Removes the unnecessary Lines from "END OF .*" to the end
   * 
   * @param position
   * @param removeRestElementsList add the unnecessary lines
   */
  private void removeElementsFromPosition(int position, List<Line> removeRestElementsList) {
    for (int i = position; i < textList.size(); i++) {
      removeRestElementsList.add(textList.get(i));
    }
    textList.removeAll(removeRestElementsList);
    removeRestElementsList.clear();
  }

  /**
   * Checks whether a metadata section is existing in the textList
   * 
   * @param newTextList
   * @return
   */
  private boolean containsMetadataSection(List<Line> newTextList) {
    for (Line line : newTextList) {
      if (line.getText() != null && line.getText().toLowerCase().matches(START_OF_REGEX)) {
        return true;
      }
    }
    return false;

  }



  /**
   * Checks whether a text section is existing in the textList
   * 
   * @param newTextList
   * @return
   */
  private boolean containsTextSection(List<Line> newTextList) {
    for (Line line : newTextList) {
      if (line.getText() != null && line.getText().toLowerCase().matches(END_OF_REGEX)
          && line.getText().toLowerCase().contains(textDistinction)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the metadataList
   * 
   * @return metadataList
   */
  public List<Line> getMetadataList() {
    return metadataList;
  }

  /**
   * Returns the textList
   * 
   * @return textList
   */
  public List<Line> getTextList() {
    return textList;
  }
}
