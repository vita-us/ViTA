package de.unistuttgart.vis.vita.services.occurrence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Abstract base class of every service dealing with Occurrences. Offers methods to convert 
 * Lists of TextSpans into Lists of Occurrences and get the Document length.
 */
public abstract class OccurrencesService {

  protected EntityManager em;
  protected String documentId;
  private int documentLength;

  public abstract OccurrencesResponse getOccurrences(int steps, 
                                                      double rangeStart, 
                                                      double rangeEnd);

  public List<Occurrence> covertSpansToOccurrences(List<TextSpan> textSpans) {
    List<Occurrence> occurrences = new ArrayList<>();
    int docLength = getDocumentLength();
    
    for (TextSpan span : textSpans) {
      occurrences.add(span.toOccurrence(docLength));
    }
    
    return occurrences;
  }

  public int getDocumentLength() {
    if (documentLength <= 0) {
      Document doc = readDocumentFromDatabase();
      documentLength = doc.getMetrics().getCharacterCount();
    }
    return documentLength;
  }
  
  private Document readDocumentFromDatabase() {
    TypedQuery<Document> docQuery = em.createNamedQuery("Document.findDocumentById", 
                                                        Document.class);
    docQuery.setParameter("documentId", documentId);
    return docQuery.getSingleResult();
  }

}
