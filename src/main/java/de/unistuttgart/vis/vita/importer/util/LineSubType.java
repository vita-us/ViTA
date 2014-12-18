package de.unistuttgart.vis.vita.importer.util;

import java.util.ArrayList;

public enum LineSubType {
  CHAPTER, NUMBER, CHAPTER_NUMBER, NUMBER_CHAPTER;
      
  public static Iterable<LineType> getTypesWithSubtypes(){
    ArrayList<LineType> types = new ArrayList<LineType>();
    types.add(LineType.SMALLHEADING);
    types.add(LineType.BIGHEADING);
    return types;
  }
}
