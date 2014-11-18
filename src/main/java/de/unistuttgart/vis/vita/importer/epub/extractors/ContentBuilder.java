package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the content of an Inputstream and transforms it into a string
 * 
 *
 */
public class ContentBuilder {

  public String getStringFromInputStream(InputStream is) {
    BufferedReader bufferedReader = null;
    StringBuilder stringBuilder = new StringBuilder();

    String line;
    try {

      bufferedReader = new BufferedReader(new InputStreamReader(is));
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }
    } catch (IOException e) {
      Logger logger = Logger.getLogger(ContentBuilder.class.getName());
      logger.log(Level.SEVERE, "an exception was thrown");
      
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          Logger logger = Logger.getLogger(ContentBuilder.class.getName());
          logger.log(Level.SEVERE, "an exception was thrown");
        }
      }
    }

    return stringBuilder.toString();

  }
}
