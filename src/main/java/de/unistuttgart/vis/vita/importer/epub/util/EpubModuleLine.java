package de.unistuttgart.vis.vita.importer.epub.util;

import de.unistuttgart.vis.vita.importer.util.AbstractLine;
import de.unistuttgart.vis.vita.importer.util.LineType;

/**
 * A Line contains a text-line of the imported file. This Line class can compute a type for the line
 * and can be used to build chapters at the end of the epub extract.
 */
public class EpubModuleLine extends AbstractLine{

  /**
   * Creates a simple Line with activated type computation.
   *
   * @param text String - The text of the line. Should not be null.
   */
  public EpubModuleLine(String text) {
    super(text);
  }
  
  /**
   * Creates a simple Line. If the type computation is deactivated the type is set to UNKNOWN. You
   * can compute the type manually by calling 'computeType()'.
   *
   * @param text                     String - The text of the line. Should not be null.
   * @param automatedTypeComputation Boolean - True activates the automated type computation, which
   *                                 means every change to the text will update the type.
   */
  public EpubModuleLine(String text, Boolean automatedTypeComputation) {
    super(text, automatedTypeComputation);
  }

  @Override
  public void computeType() {
    if (automatedTypeComputation) {
      // a lower type will be overwritten
      LineType highestType = LineType.TEXT;
      if (matchesPattern(WHITESPACEPATTERN)) {
        highestType = LineType.WHITELINE;
      }
      this.type = highestType;
    }
  }

}
