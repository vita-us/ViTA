package de.unistuttgart.vis.vita.analysis.modules;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.progress.FeatureProgress;

/**
 * The feature module that stores document outline.
 * 
 * "Feature module" means that it interacts with the data base. It stores the progress as well as
 * the result.
 */
@AnalysisModule(dependencies = {ImportResult.class, DocumentPersistenceContext.class,
    Model.class})
public class TextFeatureModule extends Module<TextFeatureModule> {
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
    if (documentId == null || model == null)
      return; // cannot report progress yet

    EntityManager em = model.getEntityManager();
    Document doc = getDocument(em);
    if (!doc.getProgress().getPersonsProgress().isReady()) {
      em.getTransaction().begin();
      doc.getProgress().setTextProgress(new FeatureProgress(progress, false));
      em.getTransaction().commit();
    }
  }

  @Override
  public TextFeatureModule execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    model = result.getResultFor(Model.class);
    EntityManager em = model.getEntityManager();
    em.getTransaction().begin();
    documentId = result.getResultFor(DocumentPersistenceContext.class).getDocumentId();
    Document document = getDocument(em);
    
    ImportResult importResult = result.getResultFor(ImportResult.class);
    
    document.getContent().getParts().addAll(importResult.getParts());
    for (DocumentPart part : importResult.getParts()) {
      em.persist(part);
      for (Chapter chapter : part.getChapters()) {
        em.persist(chapter);
      }
    }
    em.merge(document);
    
    document.getProgress().setTextProgress(new FeatureProgress(1, true));
    em.getTransaction().commit();
    
    return this;
  }
  
  protected Document getDocument(EntityManager em) {
    TypedQuery<Document> query = em.createNamedQuery("Document.findDocumentById", Document.class);
    query.setParameter("documentId", documentId);
    return query.getSingleResult();
  }

}
