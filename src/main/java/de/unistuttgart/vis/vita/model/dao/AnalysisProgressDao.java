package de.unistuttgart.vis.vita.model.dao;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * Represents a data access object for accessing the progress of the document analysis.
 */
@Stateless
public class AnalysisProgressDao extends JpaDao<AnalysisProgress, String> {

  /**
   * Creates a new data access object for accessing the progress of the analysis.
   */
  public AnalysisProgressDao() {
    super(AnalysisProgress.class);
  }

  /**
   * Finds the AnalysisProgress for the document with the given id.
   * 
   * @param docId - the id of the document, which progress should be found
   * @return the AnalysisProgress for the given document
   */
  public AnalysisProgress findByDocumentId(String docId) {
    TypedQuery<AnalysisProgress> docQuery = em.createNamedQuery("AnalysisProgress.findProgressByDocumentId",
        AnalysisProgress.class);
    docQuery.setParameter("docId", docId);
    return docQuery.getSingleResult();
  }

}
