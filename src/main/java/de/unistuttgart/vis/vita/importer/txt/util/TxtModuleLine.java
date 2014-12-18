package de.unistuttgart.vis.vita.importer.txt.util;

import de.unistuttgart.vis.vita.importer.util.AbstractLine;
import de.unistuttgart.vis.vita.importer.util.LineSubType;
import de.unistuttgart.vis.vita.importer.util.LineType;

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
      this.type.clear();
      if (matchesPattern(SMALLHEADINGPATTERN)) {
        this.type.add(LineType.SMALLHEADING);
      }
      if (matchesPattern(BIGHEADINGPATTERN) && !matchesPattern(ARABICNUMBERPATTERN)) {
        this.type.add(LineType.BIGHEADING);
      }
      if (matchesPattern(TABLEOFCONTENTSPATTERN)) {
        this.type.add(LineType.TABLEOFCONTENTS);
      }
      if (matchesPattern(PREFACEPATTERN)) {
        this.type.add(LineType.PREFACE);
      }
      if (!containsPattern(NOSPECIALSIGNSPATTERN) && !text.contains("...")) {
        this.type.add(LineType.SPECIALSIGNS);
      }
      if (matchesPattern(MARKEDHEADINGPATTERN)) {
        this.type.add(LineType.MARKEDHEADING);
      }
      if (matchesPattern(DATADIVIDERPATTERN)) {
        this.type.add(LineType.DATADIVIDER);
      }
      if (matchesPattern(WHITESPACEPATTERN)) {
        this.type.clear();
        this.type.add(LineType.WHITELINE);
      }
      if (type.isEmpty()) {
        this.type.add(LineType.TEXT);
      }
      // compute subtype if needed
      this.hasSubtype = false;
      if (this.isType(LineSubType.getTypesWithSubtypes())) {
        computeSubtype();
      }
    }
  }

  /**
   * Computes the subtype of the line. Will only change the value if there is a new one found.
   */
  private void computeSubtype() {
    if (matchesPattern(SUBTYPECHAPTERNUMBERPATTERN)) {
      this.hasSubtype = true;
      this.subType = LineSubType.CHAPTER_NUMBER;
    } else if (matchesPattern(SUBTYPENUMBERCHAPTERPATTERN)) {
      this.hasSubtype = true;
      this.subType = LineSubType.NUMBER_CHAPTER;
    } else if (matchesPattern(SUBTYPENUMBERPATTERN)) {
      this.hasSubtype = true;
      this.subType = LineSubType.NUMBER;
    } else if (matchesPattern(SUBTYPECHAPTERPATTERN)) {
      this.hasSubtype = true;
      this.subType = LineSubType.CHAPTER;
    }
  }
}
