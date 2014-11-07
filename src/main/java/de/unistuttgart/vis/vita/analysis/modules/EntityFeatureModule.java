package de.unistuttgart.vis.vita.analysis.modules;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.EntityManager;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;
import de.unistuttgart.vis.vita.model.progress.FeatureProgress;

/**
 * The feature module that stores entities.
 * 
 * "Feature module" means that it interacts with the data base. It stores the progress as well as
 * the result.
 * 
 * This depends on the text feature module because the chapters must have been stored for the
 * TextSpans to be persistable
 */
@AnalysisModule(dependencies = {BasicEntityCollection.class, DocumentPersistenceContext.class,
    Model.class, TextFeatureModule.class})
public class EntityFeatureModule extends AbstractFeatureModule<EntityFeatureModule> {
  @Override
  public EntityFeatureModule storeResults(ModuleResultProvider result, Document document,
      EntityManager em) throws Exception {
    Collection<BasicEntity> basicEntities =
        result.getResultFor(BasicEntityCollection.class).getEntities();
    
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
    
    return this;
  }

  @Override
  protected Iterable<FeatureProgress> getProgresses(AnalysisProgress progress) {
    return Arrays.asList(progress.getPersonsProgress(), progress.getPlacesProgress());
  }
}
