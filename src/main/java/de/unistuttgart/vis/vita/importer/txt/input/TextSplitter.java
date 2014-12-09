package de.unistuttgart.vis.vita.importer.txt.input;

import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.importer.util.LineType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TextSplitter splits the text lines into metadata and text section
 */
public class TextSplitter {

  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  private static final String START_OF_REGEX = WHITESPACE + "\\*\\*\\*\\s*start of.+\\s*\\*\\*\\*"
      + WHITESPACE;
  private static final String END_OF_REGEX = WHITESPACE + "\\*\\*\\*\\s*end of.+\\s*\\*\\*\\*"
      + WHITESPACE;
  private static final String START_OF_DATADIVIDER = "^" + WHITESPACE + "\\*\\*\\*";
  private static final String END_OF_DATADIVIDER = "\\*\\*\\*" + WHITESPACE + "$";
  private static final String TEXTDISTINCTION_REGEX = "start of.+[^\\p{Punct}{3}]";
  private List<Line> metadataList = new ArrayList<>();
  private List<Line> textList = new ArrayList<>();
  private String textDistinction = "";

  /**
   * Takes the imported lines and deploys the listed methods to these lines
   * 
   * @param lines
   */
  public TextSplitter(List<Line> lines) {
    this.textList = lines;
    concatenateDatadivider();
    getMetadataSection();
    getTextSection();
    removeDatadividers(this.textList);
    removeDatadividers(this.metadataList);
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

  /**
   * Concatenates Multiline-Datadividers so there are only one-line-Datadividers in the textList.
   */
  private void concatenateDatadivider() {
    Pattern startPattern = Pattern.compile(START_OF_DATADIVIDER);

    for (int index = 0; index < this.textList.size(); index++) {
      Line line = this.textList.get(index);
      // in case automated type computation is deactivated
      line.computeType();
      if (startPattern.matcher(line.getText()).find() && containsDatadividerEnding(index)) {
        while ((index + 1 <= this.textList.size() - 1)
            && !line.isType(LineType.DATADIVIDER)) {
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
      if (line.isType(LineType.DATADIVIDER)) {
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
    Pattern endPattern = Pattern.compile(END_OF_DATADIVIDER);
    boolean found = false;
    boolean datadividerFound = false;

    if (this.textList.size() > startPosition + 1) {
      List<Line> searchList = this.textList.subList(startPosition + 1, this.textList.size());

      Iterator<Line> linesIterator = searchList.iterator();
      while (linesIterator.hasNext() && !found && !datadividerFound) {
        Line line = linesIterator.next();
        if (endPattern.matcher(line.getText()).find()) {
          // in any case the loop will break because datadivider is found
          found = !line.isType(LineType.DATADIVIDER);
          datadividerFound = line.isType(LineType.DATADIVIDER);
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
    List<Line> removeTextElements = new ArrayList<>();
    if (containsMetadataSection(textList)) {
      for (Line line : textList) {
        if (line.getText() != null && line.getText().toLowerCase().matches(START_OF_REGEX)) {
          gatherTextDistinction(line);
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
   * Detects and saves the text distinction of the found datadivider.
   *
   * @param line Line - The line containing the datadivider.
   */
  private void gatherTextDistinction(Line line) {
    Pattern pattern = Pattern.compile(TEXTDISTINCTION_REGEX);
    Matcher matcher = pattern.matcher(line.getText().toLowerCase());
    if (matcher.find()) {
      textDistinction = matcher.group(0);
      textDistinction = textDistinction.replaceAll("(?i)start of", "");
      textDistinction = textDistinction.trim();
    }
  }

  /**
   * Gets only the text section from the textList
   *
   * @return contains only the text lines: textList
   */
  private List<Line> getTextSection() {
    int position = 0;
    List<Line> removeRestElements = new ArrayList<>();
    if (containsTextSection(textList) && !metadataList.isEmpty()) {
      for (Line line : textList) {
        if (line.getText() != null && line.getText().toLowerCase().matches(END_OF_REGEX)
            && line.getText().toLowerCase().contains(textDistinction)) {
          position = textList.indexOf(line);
          break;
        }
      }
      removeElementsFromPosition(position, removeRestElements);
    }
    return textList;
  }


  /**
   * Removes the unnecessary Lines from "END OF .*" to the end
   *
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
}
