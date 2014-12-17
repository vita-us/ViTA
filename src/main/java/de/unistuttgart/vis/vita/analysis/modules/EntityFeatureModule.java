package de.unistuttgart.vis.vita.analysis.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.EntityAttributes;
import de.unistuttgart.vis.vita.analysis.results.EntityRanking;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.analysis.results.EntityWordCloudResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
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
@AnalysisModule(dependencies = {EntityAttributes.class, EntityRanking.class, EntityRelations.class,
                                BasicEntityCollection.class, DocumentPersistenceContext.class,
                                Model.class, TextFeatureModule.class, EntityWordCloudResult.class},
                                weight = 0.1)
public class EntityFeatureModule extends AbstractFeatureModule<EntityFeatureModule> {
  @Override
  public EntityFeatureModule storeResults(ModuleResultProvider result, Document document,
      EntityManager em) throws Exception {
    List<BasicEntity> basicEntities =
        result.getResultFor(EntityRanking.class).getRankedEntities();
    EntityRelations relations = result.getResultFor(EntityRelations.class);
    EntityAttributes entityAttributes = result.getResultFor(EntityAttributes.class);
    Map<BasicEntity, Entity> realEntities = new HashMap<>();
    EntityWordCloudResult wordClouds = result.getResultFor(EntityWordCloudResult.class);

    int currentPersonRanking = 1;
    int currentPlaceRanking = 1;
    for (BasicEntity basicEntity : basicEntities) {
      Entity entity;
      switch (basicEntity.getType()) {
        case PERSON:
          Person person = new Person();
          document.getContent().getPersons().add(person);
          entity = person;
          entity.setRankingValue(currentPersonRanking);
          currentPersonRanking++;
          break;
        case PLACE:
          Place place = new Place();
          document.getContent().getPlaces().add(place);
          entity = place;
          entity.setRankingValue(currentPlaceRanking);
          currentPlaceRanking++;
          break;
        default:
          continue;
      }
      
      entity.setFrequency(basicEntity.getOccurences().size());
      entity.setDisplayName(basicEntity.getDisplayName());
      entity.getAttributes().addAll(basicEntity.getNameAttributes());
      entity.getAttributes().addAll(entityAttributes.getAttributesForEntity(basicEntity));
      entity.getOccurrences().addAll(basicEntity.getOccurences());
      entity.setWordCloud(wordClouds.getWordCloudForEntity(basicEntity));

      DocumentMetrics metrics = document.getMetrics();
      metrics.setPersonCount(document.getContent().getPersons().size());
      metrics.setPlaceCount(document.getContent().getPlaces().size());

      em.persist(entity);
      em.persist(entity.getWordCloud());
      realEntities.put(basicEntity, entity);
    }

    for (BasicEntity basicEntity : basicEntities) {
      Entity source = realEntities.get(basicEntity);
      Map<BasicEntity, Double> weights = relations.getRelatedEntities(basicEntity);

      if (weights == null) {
        continue;
      }

      for (Map.Entry<BasicEntity, Double> entry : weights.entrySet()) {
        Entity target = realEntities.get(entry.getKey());
        EntityRelation relation = new EntityRelation();
        relation.setOriginEntity(source);
        relation.setRelatedEntity(target);
        relation.setWeight(entry.getValue());
        relation.setWeightOverTime(relations.getWeightOverTime(basicEntity, entry.getKey()));
        em.persist(relation);
      }
    }

    return this;
  }

  @Override
  protected Iterable<FeatureProgress> getProgresses(AnalysisProgress progress) {
    return Arrays.asList(progress.getPersonsProgress(), progress.getPlacesProgress(),
        progress.getGraphViewProgress(), progress.getFingerPrintProgress());
  }
}
