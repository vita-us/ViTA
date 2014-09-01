package de.unistuttgart.vis.vita.importer.txt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Line {
  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  private static final Pattern WHITESPACEPATTERN = Pattern.compile(WHITESPACE);

  private static final String DATADIVIDER = WHITESPACE + "(\\*\\*\\*)(.*)(\\*\\*\\*)" + WHITESPACE;
  private static final Pattern DATADIVIDERPATTERN = Pattern.compile(DATADIVIDER);

  private static final String MARKEDHEADING = WHITESPACE + "#.*";
  private static final Pattern MARKEDHEADINGPATTERN = Pattern.compile(MARKEDHEADING);

  private static final String PREFACE = WHITESPACE + "#?((Preface)|(To\\s*the\\s*Reader))([\\.:])?"
      + WHITESPACE;
  private static final Pattern PREFACEPATTERN = Pattern.compile(PREFACE, Pattern.CASE_INSENSITIVE);

  private static final String TABLEOFCONTENTS = WHITESPACE + ".*"
      + "#?((Index)|(Contents)|(Table\\s*of\\s*Contents))([\\.:])?" + WHITESPACE;
  private static final Pattern TABLEOFCONTENTSPATTERN = Pattern.compile(TABLEOFCONTENTS,
      Pattern.CASE_INSENSITIVE);

  private static final String BIGHEADING = WHITESPACE
      + "([\\p{Upper}\\d\\p{Punct}][\\p{Upper}\\d\\p{Punct}\\s]*)";
  private static final String BIGHEADINGQUOTES = WHITESPACE + "(\"" + BIGHEADING + "\")" + "|"
      + "(\'" + BIGHEADING + "\')";
  private static final Pattern BIGHEADINGPATTERN = Pattern.compile(BIGHEADING + "|"
      + BIGHEADINGQUOTES);

  private static final String BIGSIGNS = "[\\p{Upper}\\d]";
  private static final Pattern BIGSIGNSPATTERN = Pattern.compile(BIGSIGNS);

  private static final String NORMALHEADING = "\\p{Upper}[^\\.\\?\\!,]*\\.?";
  private static final String ONEQUOTE = "(\"|\')\\p{Upper}[^\"\']*(\"|\')";
  private static final String CHAPTER = "(?i)Chapter(?-i)" + WHITESPACE + "((\\d+)|([IVXML]+))?"
      + WHITESPACE + "\\p{Punct}?";
  private static final String SMALLHEADING = "(" + WHITESPACE + "(" + CHAPTER + ")?" + WHITESPACE
      + "((" + "([\\d]+\\.)?" + WHITESPACE + NORMALHEADING + ")" + "|" + "(" + ONEQUOTE + "))"
      + WHITESPACE + ")|(" + CHAPTER + ")";
  private static final Pattern SMALLHEADINGPATTERN = Pattern.compile(SMALLHEADING);

  private String text;
  private LineType type;

  private void computeType() {
    LineType highestType = LineType.getLowestValidType();
    if (matchesPattern(SMALLHEADINGPATTERN)) {
      highestType = LineType.SMALLHEADING;
    }
    if (matchesPattern(BIGHEADINGPATTERN) && containsPattern(BIGSIGNSPATTERN)) {
      highestType = LineType.BIGHEADING;
    }
    if (matchesPattern(TABLEOFCONTENTSPATTERN)) {
      highestType = LineType.TABLEOFCONTENTS;
    }
    if (matchesPattern(PREFACEPATTERN)) {
      highestType = LineType.PREFACE;
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

  private boolean matchesPattern(Pattern pattern) {
    Matcher matcher = pattern.matcher(text);
    return matcher.matches();
  }

  private boolean containsPattern(Pattern pattern) {
    return pattern.matcher(text).find();
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    computeType();
  }

  public LineType getType() {
    return type;
  }

  public Line(String text) {
    super();
    this.text = text;
    computeType();
  }

}
