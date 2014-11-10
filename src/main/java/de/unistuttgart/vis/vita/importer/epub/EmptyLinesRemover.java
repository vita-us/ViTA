package de.unistuttgart.vis.vita.importer.epub;

import java.util.Collections;
import java.util.List;

/**
 * Removes empty Strings in part or parts 
 * 
 *
 */
public class EmptyLinesRemover {

  /**
   * Removes empty Strings in the current part
   * @param currentPart
   */
  public void removeEmptyLinesPart(List<List<String>> currentPart) {
    for (List<String> chapter : currentPart) {
      chapter.removeAll(Collections.singleton(""));
    }
  }

  /**
   * Removes empty Strings in the current parts
   * @param currentPart
   */
  public void removeEmptyLinesParts(List<List<List<String>>> currentParts) {
    for (List<List<String>> currentPart : currentParts) {
      removeEmptyLinesPart(currentPart);
    }
  }

}
