package de.unistuttgart.vis.vita.model.dao;

import javax.annotation.ManagedBean;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * Represents a data access object for accessing the progress of the document analysis.
 */
@ManagedBean
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "AnalysisProgress.findAllProgresses",
              query = "SELECT p "
                    + "FROM AnalysisProgress p"),

  @NamedQuery(name = "AnalysisProgress.findProgressById",
              query = "SELECT p "
                    + "FROM AnalysisProgress p "
                    + "WHERE p.id = :progressId"),

  @NamedQuery(name = "AnalysisProgress.findProgressByDocumentId",
              query = "SELECT p "
                    + "FROM AnalysisProgress p, Document d "
                    + "WHERE d.id = :documentId "
                    + "AND d.progress = p")
})
public class AnalysisProgressDao extends JpaDao<AnalysisProgress, String> {

  private static final String DOC_ID_PARAMETER = "documentId";

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
    docQuery.setParameter(DOC_ID_PARAMETER, docId);
    return docQuery.getSingleResult();
  }

}
