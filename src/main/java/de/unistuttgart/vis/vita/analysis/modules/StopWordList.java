package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class StopWordList {
  private static Set<String> stopWords;

  private static final Logger LOGGER = Logger.getLogger(StopWordList.class.getName());

  public static Set<String> getStopWords() throws IOException {
    if (stopWords != null){
      return stopWords;
    }

    stopWords = new HashSet<>(
        IOUtils.readLines(StopWordList.class.getResourceAsStream("stopwords.txt")));

    return stopWords;
  }

  public static String getLineSeparatedStopWords() {
    try {
      return StringUtils.join(getStopWords(), "\n");
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error loading stop words", e);
      return "";
    }
  }
}
