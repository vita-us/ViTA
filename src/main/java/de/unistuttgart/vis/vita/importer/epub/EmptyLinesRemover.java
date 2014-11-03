package de.unistuttgart.vis.vita.importer.epub;

import java.util.Collections;
import java.util.List;

public class EmptyLinesRemover {


  public void removeEmptyLinesPart(List<List<String>> currentPart) {
    for (List<String> chapter : currentPart) {
      chapter.removeAll(Collections.singleton(""));
    }
  }

  public void removeEmptyLinesParts(List<List<List<String>>> currentParts) {
    for (List<List<String>> currentPart : currentParts) {
      for (List<String> chapter : currentPart) {
        chapter.removeAll(Collections.singleton(""));
      }
    }
  }

}
