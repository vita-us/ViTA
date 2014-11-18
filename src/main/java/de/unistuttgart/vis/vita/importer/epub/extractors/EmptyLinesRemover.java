package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.util.Iterator;
import java.util.List;

/**
 * Removes empty Strings in part or parts
 * 
 *
 */
public class EmptyLinesRemover {

  /**
   * Removes Epublines with empty lines in the current part
   * 
   * @param currentPart
   */
  public void removeEmptyLinesPart(List<List<Epubline>> currentPart) {

    for (List<Epubline> chapter : currentPart) {
      for (Iterator<Epubline> iterator = chapter.iterator(); iterator.hasNext();) {
        Epubline currentLine = iterator.next();
        if (currentLine.getEpubline().isEmpty()) {
          iterator.remove();
        }
      }
    }
  }

  /**
   * Removes Epublines with empty lines in the current parts
   * 
   * @param currentPart
   */
  public void removeEmptyLinesParts(List<List<List<Epubline>>> currentParts) {
    for (List<List<Epubline>> currentPart : currentParts) {
      removeEmptyLinesPart(currentPart);
    }
  }

}
