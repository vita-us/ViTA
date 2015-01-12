/*
 * AnnieDatastore.java
 *
 */

package de.unistuttgart.vis.vita.analysis.results;

import gate.Corpus;
import gate.DataStore;
import gate.LanguageResource;
import gate.persist.PersistenceException;

/**
 * Module for storing the Annie annotations in a corresponding datastore.
 */
public interface AnnieDatastore {

  /**
   * Returns the Gate Corpus with given document name.
   *
   * @param documentName The desired document name.
   * @return The correct corpus or if not found null.
   */
  public Corpus getStoredAnalysis(String documentName) throws PersistenceException;

  /**
   * Datastore have to be opened and closed to be operated correctly.
   * @return The datastore.
   */
  public DataStore getDatastore();

  /**
   * Stores the desired resource with the linked document name.
   *
   * @param resource   The corpus.
   * @param documentName The document name.
   * @throws PersistenceException If any problems with the persisting happens.
   */
  public void storeResult(LanguageResource resource, String documentName)
      throws PersistenceException;

  /**
   * Removes a resource from the datastore.
   *
   * @param documentName The desired name.
   * @throws PersistenceException If any problems with the persisting happens.
   */
  public void removeResult(String documentName) throws PersistenceException;
}
