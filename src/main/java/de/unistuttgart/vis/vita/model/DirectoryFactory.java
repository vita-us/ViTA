package de.unistuttgart.vis.vita.model;

import java.io.IOException;

import org.apache.lucene.store.Directory;

/**
 * An interface to get lucene directories for identifying names
 */
public interface DirectoryFactory {
  /**
   * Gets or creates the directory identified by the given name
   * 
   * @param name the name. Must be a valid file name
   * @return the directory
   */
  Directory getDirectory(String name) throws IOException;
}
