package de.unistuttgart.vis.vita.importer.txt.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A Line contains a text-line of the imported file. The Line class can compute a type for the line
 * for further analysis of the text.
 */
public class Line {
  // Patterns for Types - static so only one has to be compiled for all existing Lines.

  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  private static final Pattern WHITESPACEPATTERN = Pattern.compile(WHITESPACE);

  private static final String DATADIVIDER = WHITESPACE + "(\\*\\*\\*)(.*)(\\*\\*\\*)" + WHITESPACE;
  private static final Pattern DATADIVIDERPATTERN = Pattern.compile(DATADIVIDER);

  private static final String MARKEDHEADING = WHITESPACE + "#.*";
  private static final Pattern MARKEDHEADINGPATTERN = Pattern.compile(MARKEDHEADING);

  private static final String PREFACE = WHITESPACE + "((Preface)|(To\\s*the\\s*Reader))([\\.:])?"
                                        + WHITESPACE;
  private static final Pattern PREFACEPATTERN = Pattern.compile(PREFACE, Pattern.CASE_INSENSITIVE);

  private static final String TABLEOFCONTENTS = WHITESPACE
                                                + "((Index)|(Contents)|(Table\\s*of\\s*Contents))([\\.:])?"
                                                + WHITESPACE;
  private static final Pattern TABLEOFCONTENTSPATTERN = Pattern.compile(TABLEOFCONTENTS,
                                                                        Pattern.CASE_INSENSITIVE);

  private static final String BIGHEADING = WHITESPACE + "([\\p{Upper}\\d][^\\p{Lower}]*)";
  private static final String BIGHEADINGQUOTES = WHITESPACE + "((\"" + BIGHEADING + "\")" + "|"
                                                 + "(\'" + BIGHEADING + "\'))" + WHITESPACE;
  private static final String EXTENDEDBIGHEADING = WHITESPACE + "_" + "(" + BIGHEADING + "|"
                                                   + BIGHEADINGQUOTES + ")" + "_" + WHITESPACE;
  private static final Pattern BIGHEADINGPATTERN = Pattern.compile("(" + BIGHEADING + ")|("
                                                                   + BIGHEADINGQUOTES + ")|("
                                                                   + EXTENDEDBIGHEADING + ")");

  private static final String NORMALHEADING = "(([\\d]+\\.)|([IVXML]+\\.))?" + WHITESPACE
                                              + "\\p{Upper}[^\\.\\?\\!]*\\.?";
  private static final String CHAPTER = "(?i)Chapter(?-i)" + WHITESPACE + "((\\d+)|([IVXML]+))?"
                                        + WHITESPACE + "\\p{Punct}?";
  private static final String ONEQUOTE = "((\").*(\"))|((\').*(\'))";
  private static final String SMALLHEADING = "(" + WHITESPACE + "(" + CHAPTER + ")?" + WHITESPACE
                                             + "((" + NORMALHEADING + ")" + "|" + "(" + ONEQUOTE
                                             + "))" + WHITESPACE + ")|(" + CHAPTER
                                             + ")";
  private static final String EXTENDEDSMALLHEADING = WHITESPACE + "_" + SMALLHEADING + "_"
                                                     + WHITESPACE;
  private static final Pattern SMALLHEADINGPATTERN = Pattern.compile(SMALLHEADING + "|"
                                                                     + EXTENDEDSMALLHEADING);

  private static final String NOSPECIALSIGNS = "\\p{Alnum}";
  private static final Pattern NOSPECIALSIGNSPATTERN = Pattern.compile(NOSPECIALSIGNS);

  private String text;
  private LineType type;
  private boolean automatedTypeComputation;

  /**
   * Creates a simple Line with activated type computation.
   *
   * @param text String - The text of the line. Should not be null.
   */
  public Line(String text) {
    this(text, true);
  }

  /**
   * Creates a simple Line. If the type computation is deactivated the type is set to UNKNOWN. You
   * can compute the type manually by calling 'computeType()'.
   *
   * @param text                     String - The text of the line. Should not be null.
   * @param automatedTypeComputation Boolean - True activates the automated type computation, which
   *                                 means every change to the text will update the type.
   */
  public Line(String text, Boolean automatedTypeComputation) {
    super();
    this.type = LineType.UNKNOWN;
    this.text = text;
    this.automatedTypeComputation = automatedTypeComputation;
    computeType();
  }

  /**
   * Gets the text of the Line.
   *
   * @return String - The text of the Line.
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text for the Line. When the automated type computation is activated, the type can be
   * changed too.
   *
   * @param text String - The text of the Line.
   */
  public void setText(String text) {
    this.text = text;
    computeType();
  }

  /**
   * Gets the type of the set text.
   *
   * @return LineType - The type of the text.
   */
  public LineType getType() {
    return type;
  }

  /**
   * Sets the type of the text. Should only be used if automated type computation is deactivated. To
   * set the type manually should be an exception, if it is impossible for the line to compute its
   * type itself.
   *
   * @param type LineType - The type of the text.
   */
  public void setType(LineType type) {
    this.type = type;
  }

  /**
   * Checks if the automated type computation is activated.
   *
   * @return Boolean - true: is activated. false: is deactivated.
   */
  public boolean isAutomatedTypeComputation() {
    return automatedTypeComputation;
  }

  /**
   * Activates/Deactivates the automated type computation. If activated, will compute the current
   * type instantly.
   *
   * @param automatedTypeComputation Boolean - true: activate. false: deactivate.
   */
  public void setAutomatedTypeComputation(boolean automatedTypeComputation) {
    this.automatedTypeComputation = automatedTypeComputation;
    computeType();
  }


  /**
   * Computes the type for the text.
   */
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

  /**
   * Checks if pattern matches the text.
   *
   * @param pattern Pattern - The pattern.
   * @return Boolean - true is a match, false is not.
   */
  private boolean matchesPattern(Pattern pattern) {
    Matcher matcher = pattern.matcher(text);
    return matcher.matches();
  }

  /**
   * Checks if pattern can be found in the text.
   *
   * @param pattern Pattern - The pattern.
   * @return Boolean - true if there is at least one occurrence.
   */
  private boolean containsPattern(Pattern pattern) {
    return pattern.matcher(text).find();
  }
}
