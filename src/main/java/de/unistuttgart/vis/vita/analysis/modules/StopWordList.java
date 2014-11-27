package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class StopWordList {
  private static Set<String> stopWords;

  public static Set<String> getStopWords() throws IOException {
    if (stopWords != null)
      return stopWords;

    stopWords = new HashSet<>(
        IOUtils.readLines(StopWordList.class.getResourceAsStream("stopwords.txt")));

    return stopWords;
  }
}
