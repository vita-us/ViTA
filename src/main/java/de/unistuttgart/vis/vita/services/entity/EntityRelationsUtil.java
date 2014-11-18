package de.unistuttgart.vis.vita.services.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains utility functions related to EntityRelations.
 */
public class EntityRelationsUtil {
  
  /**
   * Converts an comma-separated id-String into a list of Strings, ignoring "[]" and removing 
   * whitespace.
   * 
   * @param idString - a String of comma-separated ids
   * @return list of ids
   */
  public static List<String> convertIdStringToList(String idString) {
    List<String> idList = new ArrayList<>();
    for (String subString : idString.replaceAll("\\[|\\]","").split(",")) {
      idList.add(subString.trim());
    }
    return idList;
  }

}
