package de.unistuttgart.vis.vita.importer.txt;

public enum LineType {
  WHITELINE, DATADIVIDER, MARKEDHEADING, PREFACE, TABLEOFCONTENTS, BIGHEADING, SMALLHEADING, TEXT, UNKNOWN;

  public static LineType getLowestValidType() {
    return TEXT;
  }

}
