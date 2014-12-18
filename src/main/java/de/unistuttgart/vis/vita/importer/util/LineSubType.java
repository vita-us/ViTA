package de.unistuttgart.vis.vita.importer.util;

import java.util.ArrayList;

/**
 * The subtype a line can have.
 */
public enum LineSubType {
  CHAPTER, NUMBER, CHAPTER_NUMBER, NUMBER_CHAPTER;
      
  /**
   * Returns all types which are extended by a subtype.
   * 
   * @return the types.
   */
  public static Iterable<LineType> getTypesWithSubtypes(){
    ArrayList<LineType> types = new ArrayList<LineType>();
    types.add(LineType.SMALLHEADING);
    types.add(LineType.BIGHEADING);
    return types;
  }
}
