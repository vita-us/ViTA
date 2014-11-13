package de.unistuttgart.vis.vita.analysis.modules;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;
import de.unistuttgart.vis.vita.model.progress.FeatureProgress;

/**
 * The base class for modules that store analysis results in the data base and report their progress
 */
public abstract class AbstractFeatureModule<T> extends Module<T> {
  private Model model;
  private String documentId;

  @Override
  public void dependencyFinished(Class<?> resultClass, Object result) {
    if (resultClass == DocumentPersistenceContext.class) {
      documentId = ((DocumentPersistenceContext) result).getDocumentId();
    } else if (resultClass == Model.class) {
      model = (Model) result;
    }
  }

  @Override
  public void observeProgress(double progress) {
    if (documentId == null || model == null) {
      // cannot report progress yet
      return;
    }

    EntityManager em = model.getEntityManager();
    Document doc = getDocument(em);
    em.getTransaction().begin();
    for (FeatureProgress featureProgress : getProgresses(doc.getProgress())) {
      if (!featureProgress.isReady()) {
        featureProgress.setProgress(progress);
      }
    }
    em.getTransaction().commit();
  }

  @Override
  public final T execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    model = results.getResultFor(Model.class);
    EntityManager em = model.getEntityManager();
    em.getTransaction().begin();
    documentId = results.getResultFor(DocumentPersistenceContext.class).getDocumentId();
    Document document = getDocument(em);

    T result = storeResults(results, document, em);

    em.merge(document);

    for (FeatureProgress featureProgress : getProgresses(document.getProgress())) {
      featureProgress.setProgress(1);
      featureProgress.setReady(true);
    }
    em.getTransaction().commit();

    return result;
  }

  /**
   * Does the actual work of storing the feature results in the document
   * 
   * @param results the results of the dependencies
   * @param document will be merged automatically afterwards
   * @param em the entity manager, in case other entities need to be persisted
   * @return the desired result for this module
   */
  protected abstract T storeResults(ModuleResultProvider results, Document document,
      EntityManager em) throws Exception;

  /**
   * Returns the {@link FeatureProgress} objects of the given {@link AnalysisProgress} that should
   * be updated to the current progress
   */
  protected abstract Iterable<FeatureProgress> getProgresses(AnalysisProgress progress);

  private Document getDocument(EntityManager em) {
    TypedQuery<Document> query = em.createNamedQuery("Document.findDocumentById", Document.class);
    query.setParameter("documentId", documentId);
    return query.getSingleResult();
  }

}
