package de.unistuttgart.vis.vita.analysis.modules;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * The feature module that stores entities.
 * 
 * "Feature module" means that it interacts with the data base. It stores the progress as well as
 * the result.
 */
@AnalysisModule(dependencies = {BasicEntityCollection.class,
    DocumentPersistenceContextModule.class, Model.class})
public class EntityFeatureModule implements Module<EntityFeatureModule> {
  private Model model;
  private String documentId;
  private EntityManager em;
  
  @Override
  public void observeProgress(double progress) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public EntityFeatureModule execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    model = result.getResultFor(Model.class);
    em = model.getEntityManager();
    em.getTransaction().begin();
    documentId = result.getResultFor(DocumentPersistenceContext.class).getDocumentId();
    Document document = getDocument();
    
    Collection<BasicEntity> basicEntities = result.getResultFor(BasicEntityCollection.class).getEntities();
    
    for (BasicEntity basicEntity : basicEntities) {
      Entity entity;
      switch (basicEntity.getType()) {
        case PERSON:
          Person person = new Person();
          document.getContent().getPersons().add(person);
          entity = person;
          break;
        case PLACE:
          Place place = new Place();
          document.getContent().getPlaces().add(place);
          entity = place;
          break;
        default:
          continue;
      }
     
      entity.setDisplayName(basicEntity.getDisplayName());
      entity.getAttributes().addAll(basicEntity.getNameAttributes());
      entity.getOccurrences().addAll(basicEntity.getOccurences());
      // TODO: relations, rankings, more attributes
      
      em.persist(entity);
    }
    em.merge(document);
    
    em.getTransaction().commit();
    
    return this;
  }
  
  protected Document getDocument() {
    TypedQuery<Document> query = em.createNamedQuery("Document.findDocumentById", Document.class);
    query.setParameter("documentId", documentId);
    return query.getSingleResult();
  }

}
