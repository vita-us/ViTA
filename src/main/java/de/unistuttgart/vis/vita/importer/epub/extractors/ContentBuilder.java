package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the content of an Inputstream and transforms it into a string
 * 
 *
 */
public class ContentBuilder {
  
  private static final Logger LOGGER = Logger.getLogger(ContentBuilder.class.toString());

  public String getStringFromInputStream(InputStream is) {
    BufferedReader bufferedReader = null;
    StringBuilder stringBuilder = new StringBuilder();

    String line;
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "an exception was thrown. Is Encoding of epub UTF-8?", e);

    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "an exception was thrown", e);
        }
      }
    }

    return stringBuilder.toString();

  }
}
