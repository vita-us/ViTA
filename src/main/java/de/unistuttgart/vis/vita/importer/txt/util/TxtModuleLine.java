package de.unistuttgart.vis.vita.importer.txt.util;

/**
 * A Line contains a text-line of the imported file. The Line class can compute a type for the line
 * for further analysis of the text.
 */
public class TxtModuleLine extends AbstractLine {

  /**
   * Creates a simple Line with activated type computation.
   *
   * @param text String - The text of the line. Should not be null.
   */
  public TxtModuleLine(String text) {
    this(text, true);
  }

  /**
   * Creates a simple Line. If the type computation is deactivated the type is set to UNKNOWN. You
   * can compute the type manually by calling 'computeType()'.
   *
   * @param text The text of the line. Should not be null.
   * @param automatedTypeComputation True activates the automated type computation, which means
   *        every change to the text will update the type.
   */
  public TxtModuleLine(String text, Boolean automatedTypeComputation) {
    super(text, automatedTypeComputation);
  }

  @Override
  public void computeType() {
    if (automatedTypeComputation) {
      // a lower type will be overwritten
      LineType highestType = LineType.TEXT;
      if (matchesPattern(SMALLHEADINGPATTERN)) {
        highestType = LineType.SMALLHEADING;
      }
      if (matchesPattern(BIGHEADINGPATTERN)) {
        highestType = LineType.BIGHEADING;
      }
      if (matchesPattern(TABLEOFCONTENTSPATTERN)) {
        highestType = LineType.TABLEOFCONTENTS;
      }
      if (matchesPattern(PREFACEPATTERN)) {
        highestType = LineType.PREFACE;
      }
      if (!containsPattern(NOSPECIALSIGNSPATTERN) && !text.contains("...")) {
        highestType = LineType.SPECIALSIGNS;
      }
      if (matchesPattern(MARKEDHEADINGPATTERN)) {
        highestType = LineType.MARKEDHEADING;
      }
      if (matchesPattern(DATADIVIDERPATTERN)) {
        highestType = LineType.DATADIVIDER;
      }
      if (matchesPattern(WHITESPACEPATTERN)) {
        highestType = LineType.WHITELINE;
      }
      this.type = highestType;
    }
  }
}
