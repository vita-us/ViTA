package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Represents a data access object for accessing DocumentParts.
 */
public class DocumentPartDao extends JpaDao<DocumentPart, String> {

  /**
   * Creates a new data access object for DocumentParts.
   */
  public DocumentPartDao() {
    super(DocumentPart.class);
  }
  
  /**
   * Find all DocumentParts of a Document with the given id.
   * 
   * @param docId - the id of the Document
   * @return list of all DocumentParts of the Document
   */
  public List<DocumentPart> findPartsInDocument(String docId) {
    TypedQuery<DocumentPart> docQuery = em.createNamedQuery("DocumentPart.findPartsInDocument", 
                                                            DocumentPart.class);
    docQuery.setParameter("documentId", docId);
    
    return docQuery.getResultList();
  }
  
  /**
   * Finds the DocumentPart with the given title.
   * 
   * @param partTitle - the title of the DocumentPart to find
   * @return the DocumentPart with the given title
   */
  public DocumentPart findPartByTitle(String partTitle) {
    TypedQuery<DocumentPart> partQuery = em.createNamedQuery("DocumentPart.findPartByTitle",
                                                              DocumentPart.class);
    
    partQuery.setParameter("partTitle", partTitle);
    
    return partQuery.getSingleResult();
  }

}
