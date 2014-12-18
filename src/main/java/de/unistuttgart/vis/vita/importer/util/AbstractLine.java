package de.unistuttgart.vis.vita.importer.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic Line which implements constructors, getters and setters and
 */
public abstract class AbstractLine implements Line {
  // Patterns for Types - static so only one has to be compiled for all existing Lines.

  // pattern for whitespace-type
  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  protected static final Pattern WHITESPACEPATTERN = Pattern.compile(WHITESPACE);

  // pattern for datadivider-type
  private static final String DATADIVIDER = WHITESPACE + "(\\*\\*\\*)(.*)(\\*\\*\\*)" + WHITESPACE;
  protected static final Pattern DATADIVIDERPATTERN = Pattern.compile(DATADIVIDER);

  // pattern for markedheading-type
  private static final String MARKEDHEADING = WHITESPACE + "#.*";
  protected static final Pattern MARKEDHEADINGPATTERN = Pattern.compile(MARKEDHEADING);

  // pattern for preface-type
  private static final String PREFACE = WHITESPACE + "((Preface)|(To\\s*the\\s*Reader))([\\.:])?"
      + WHITESPACE;
  protected static final Pattern PREFACEPATTERN = Pattern
      .compile(PREFACE, Pattern.CASE_INSENSITIVE);

  // pattern for tableofcontents-type
  private static final String TABLEOFCONTENTS = WHITESPACE
      + "((Index)|(Contents)|(Table\\s*of\\s*Contents))([\\.:])?" + WHITESPACE;
  protected static final Pattern TABLEOFCONTENTSPATTERN = Pattern.compile(TABLEOFCONTENTS,
      Pattern.CASE_INSENSITIVE);

  // pattern for subtypes
  private static final String ARABICNUMBER = "(\\d+)";
  private static final String ROMANNUMBER = "([IVXML]+)";
  private static final String NUMBER = "(" + WHITESPACE + "(" + ARABICNUMBER + "|" + ROMANNUMBER
      + ")" + WHITESPACE + "\\p{Punct}?" + WHITESPACE + ")";
  private static final String CHAPTER = "(" + WHITESPACE + "(?i)Chapter(?-i)" + WHITESPACE + ")";
  private static final String ONEQUOTE = WHITESPACE + "(((\").*(\"))|((\').*(\')))" + WHITESPACE;
  private static final String RESTRICTIVEPRECHAPTER = "(" + "(" + CHAPTER + NUMBER + "?" + ")"
      + "|" + "(" + NUMBER + CHAPTER + ")" + "(" + ROMANNUMBER + ")" + ")";;
  private static final String PRECHAPTER = "(" + "(" + CHAPTER + NUMBER + "?" + ")" + "|" + "("
      + NUMBER + CHAPTER + "?" + ")" + ")";
  protected static final Pattern SUBTYPECHAPTERPATTERN = Pattern.compile(WHITESPACE + CHAPTER
      + ".*");
  protected static final Pattern SUBTYPECHAPTERNUMBERPATTERN = Pattern.compile(WHITESPACE + CHAPTER
      + NUMBER + ".*");
  protected static final Pattern SUBTYPENUMBERPATTERN = Pattern.compile(WHITESPACE + "("
      + ROMANNUMBER + "|" + "(" + ARABICNUMBER + "|" + "(" + ROMANNUMBER + "([^\\S\\p{Graph}])"
      + ")" + ")" + ".*" + ")");
  protected static final Pattern SUBTYPENUMBERCHAPTERPATTERN = Pattern.compile(NUMBER + CHAPTER
      + ".*");
  protected static final Pattern ARABICNUMBERPATTERN = Pattern.compile(WHITESPACE + ARABICNUMBER
      + WHITESPACE);

  // pattern for bigheading-type
  private static final String SIMPLEBIGHEADING = "(" + WHITESPACE
      + "([\\p{Upper}\\d][^\\p{Lower}]*)" + WHITESPACE + ")";
  private static final String BIGHEADINGQUOTES = "(" + WHITESPACE + "((\"" + SIMPLEBIGHEADING
      + "\")" + "|" + "(\'" + WHITESPACE + SIMPLEBIGHEADING + WHITESPACE + "\'))" + WHITESPACE
      + ")";
  private static final String BIGHEADING = "(" + RESTRICTIVEPRECHAPTER + "|" + "(" + PRECHAPTER
      + "?" + "(" + SIMPLEBIGHEADING + "|" + BIGHEADINGQUOTES + ")" + ")" + ")";
  private static final String EXTENDEDBIGHEADING = WHITESPACE + "_" + "(" + BIGHEADING + "|"
      + BIGHEADINGQUOTES + ")" + "_" + WHITESPACE;
  protected static final Pattern BIGHEADINGPATTERN = Pattern.compile(BIGHEADING + "|"
      + EXTENDEDBIGHEADING);

  // pattern for smallheading-type
  private static final String SIMPLESMALLHEADING = "(" + WHITESPACE + "\\p{Upper}[^\\.\\?\\!]*.?"
      + WHITESPACE + ")";
  private static final String SIMPLESMALLHEADINGLESSRESTRICTED = "(" + WHITESPACE + "\\p{Upper}.*"
      + WHITESPACE + ")";
  private static final String SMALLHEADING = "(" + PRECHAPTER + "|" + "(" + PRECHAPTER + "("
      + SIMPLESMALLHEADINGLESSRESTRICTED + "|" + ONEQUOTE + ")" + ")" + "|" + SIMPLESMALLHEADING
      + "|" + ONEQUOTE + ")";
  private static final String EXTENDEDSMALLHEADING = WHITESPACE + "_" + SMALLHEADING + "_"
      + WHITESPACE;
  protected static final Pattern SMALLHEADINGPATTERN = Pattern.compile(SMALLHEADING + "|"
      + EXTENDEDSMALLHEADING);

  // pattern for nospecialsigns-type
  private static final String NOSPECIALSIGNS = "\\p{Alnum}";
  protected static final Pattern NOSPECIALSIGNSPATTERN = Pattern.compile(NOSPECIALSIGNS);

  protected String text;
  protected Set<LineType> type;
  protected LineSubType subType;
  protected boolean automatedTypeComputation;
  protected boolean hasSubtype;

  /**
   * Creates a simple Line with activated type computation.
   *
   * @param text String - The text of the line. Should not be null.
   */
  public AbstractLine(String text) {
    this(text, true);
  }

  /**
   * Creates a simple Line. If the type computation is deactivated the type is set to UNKNOWN. You
   * can compute the type manually by calling 'computeType()'.
   *
   * @param text String - The text of the line. Should not be null.
   * @param automatedTypeComputation Boolean - True activates the automated type computation, which
   *        means every change to the text will update the type.
   */
  public AbstractLine(String text, Boolean automatedTypeComputation) {
    super();
    this.type = new HashSet<LineType>();
    this.type.add(LineType.UNKNOWN);
    this.hasSubtype = false;
    this.text = text;
    this.automatedTypeComputation = automatedTypeComputation;
    computeType();
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setText(String text) {
    this.text = text;
    computeType();
  }

  @Override
  public boolean isType(LineType type) {
    return this.type.contains(type);
  }

  @Override
  public boolean isType(Iterable<LineType> types) {
    boolean found = false;
    for (LineType type : types) {
      if (this.isType(type)) {
        found = true;
        break;
      }
    }
    return found;
  }

  @Override
  public void setType(LineType type) {
    this.type.clear();
    this.type.add(type);
  }

  @Override
  public boolean isAutomatedTypeComputation() {
    return automatedTypeComputation;
  }

  @Override
  public void setAutomatedTypeComputation(boolean automatedTypeComputation) {
    this.automatedTypeComputation = automatedTypeComputation;
    computeType();
  }

  @Override
  public boolean hasSubType() {
    return this.hasSubtype;
  }

  @Override
  public boolean isSubType(LineSubType subType) {
    return this.hasSubType() && this.subType.equals(subType);
  }

  @Override
  public abstract void computeType();

  /**
   * Checks if pattern matches the text.
   *
   * @param pattern Pattern - The pattern.
   * @return Boolean - true is a match, false is not.
   */
  protected boolean matchesPattern(Pattern pattern) {
    Matcher matcher = pattern.matcher(text);
    return matcher.matches();
  }

  /**
   * Checks if pattern can be found in the text.
   *
   * @param pattern Pattern - The pattern.
   * @return Boolean - true if there is at least one occurrence.
   */
  protected boolean containsPattern(Pattern pattern) {
    return pattern.matcher(text).find();
  }

}
