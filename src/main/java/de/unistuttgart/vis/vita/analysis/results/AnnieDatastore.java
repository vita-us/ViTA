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
   * Returns the Gate Corpus with given document id.
   *
   * @param documentID The desired document id.
   * @retur The correct corpus or if not found null.
   */
  public Corpus getStoredAnalysis(String documentID);

  /**
   * @return The datastore.
   */
  public DataStore getDatastore();

  /**
   * Stores the desired resource with the linked document id.
   *
   * @param resource   The corpus.
   * @param documentID The document id.
   * @throws PersistenceException If any problems with the persisting happens.
   */
  public void storeResult(LanguageResource resource, String documentID)
      throws PersistenceException;

  /**
   * Removes a resource from the datastore.
   *
   * @param documentID The desired id.
   * @throws PersistenceException If any problems with the persisting happens.
   */
  public void removeResult(String documentID) throws PersistenceException;
}
