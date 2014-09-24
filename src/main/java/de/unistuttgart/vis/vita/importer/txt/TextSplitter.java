package de.unistuttgart.vis.vita.importer.txt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TextSplitter splits the text lines into metadata and text section
 * 
 *
 */
public class TextSplitter {

  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  private static final String START_OF_REGEX = WHITESPACE + "\\*\\*\\*\\s*start of.+\\s*\\*\\*\\*"
      + WHITESPACE;
  private static final String END_OF_REGEX = WHITESPACE + "\\*\\*\\*\\s*end of.+\\s*\\*\\*\\*"
      + WHITESPACE;
  private static final String TEXTDISTINCTION_REGEX = "start of.+[^\\p{Punct}{3}]";
  private ArrayList<Line> metadataList = new ArrayList<>();
  private ArrayList<Line> textList = new ArrayList<>();
  private String textDistinction = "";

  public TextSplitter(ArrayList<Line> lines) {
    this.textList = lines;
    concatenateDatadivider();
    getMetadataSection();
    getTextSection();
    removeDatadividers(this.textList);
    removeDatadividers(this.metadataList);
  }

  /**
   * Concatenates Multiline-Datadividers so there are only one-line-Datadividers in the textList.
   */
  private void concatenateDatadivider() {
    String RegexStart = "^" + WHITESPACE + "\\*\\*\\*";
    Pattern startPattern = Pattern.compile(RegexStart);

    for (int index = 0; index < this.textList.size(); index++) {
      Line line = this.textList.get(index);
      // in case automated type computation is deactivated
      line.computeType();
      if (startPattern.matcher(line.getText()).find() && containsDatadividerEnding(index)) {
        while ((index + 1 <= this.textList.size() - 1)
            && !line.getType().equals(LineType.DATADIVIDER)) {
          line.setText(line.getText().concat(this.textList.get(index + 1).getText()));
          this.textList.remove(index + 1);
        }
      }
    }
  }

  /**
   * Removes all Datadividers from the given list.
   * 
   * @param lines List of Line - The list from which the lines should be removed.
   */
  private void removeDatadividers(List<Line> lines) {
    Iterator<Line> linesIterator = lines.iterator();
    while (linesIterator.hasNext()) {
      Line line = linesIterator.next();
      if (line.getType().equals(LineType.DATADIVIDER)) {
        linesIterator.remove();
      }
    }
  }

  /**
   * Checks if there is a multi-line-datadivider-ending in the text list, before a
   * one-line-datadivider appears.
   * 
   * @param startPosition int - The Index of the text list from which (including) the ending should
   *        be searched.
   * @return boolean - true: there is an ending. false: there is no ending.
   */
  private boolean containsDatadividerEnding(int startPosition) {
    String RegexEnd = "\\*\\*\\*" + WHITESPACE + "$";
    Pattern endPattern = Pattern.compile(RegexEnd);
    boolean found = false;

    if (this.textList.size() > startPosition + 1) {
      List<Line> searchList = this.textList.subList(startPosition + 1, this.textList.size());

      Iterator<Line> linesIterator = searchList.iterator();
      while (linesIterator.hasNext() && !found) {
        Line line = linesIterator.next();
        if (endPattern.matcher(line.getText()).find()) {
          if (line.getType().equals(LineType.DATADIVIDER)) {
            break;
          } else {
            found = true;
          }
        }
      }
    }
    return found;
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
    } else {

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
  public ArrayList<Line> getMetadataList() {
    return metadataList;
  }

  /**
   * Returns the textList
   * 
   * @return textList
   */
  public ArrayList<Line> getTextList() {
    return textList;
  }
}
