package de.unistuttgart.vis.vita.analysis.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.EntityAttributes;
import de.unistuttgart.vis.vita.analysis.results.EntityRanking;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.analysis.results.EntityWordCloudResult;
import de.unistuttgart.vis.vita.analysis.results.SentenceDetectionResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
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
 * Ranges/Occurrences to be persistable
 */
@AnalysisModule(dependencies = {EntityAttributes.class, EntityRanking.class, EntityRelations.class,
    BasicEntityCollection.class, DocumentPersistenceContext.class, Model.class,
    TextFeatureModule.class, EntityWordCloudResult.class, SentenceDetectionResult.class},
    weight = 100)
public class EntityFeatureModule extends AbstractFeatureModule<EntityFeatureModule> {
  private static final double FIRST_LOOP_DURATION_FRACTION = 0.3;
  private static final double SECOND_LOOP_DURATION_FRACTION = 0.2;

  // the rest is for committing the transaction

  @Override
  public EntityFeatureModule storeResults(ModuleResultProvider result, Document document,
      EntityManager em, ProgressListener progressListener) throws Exception {
    List<BasicEntity> basicEntities = result.getResultFor(EntityRanking.class).getRankedEntities();

    Map<BasicEntity, Entity> realEntities =
        storeEntities(document, basicEntities, result, em, progressListener);
    storeRelations(realEntities, basicEntities, result, em, progressListener);

    return this;
  }

  @Override
  protected Iterable<FeatureProgress> getProgresses(AnalysisProgress progress) {
    return Arrays.asList(progress.getPersonsProgress(), progress.getPlacesProgress(),
        progress.getGraphViewProgress(), progress.getFingerPrintProgress());
  }

  /**
   * Persists the found Entities.
   * 
   * @param document - The document the results belong to.
   * @param basicEntities - The found BasicEntities in the document.
   * @param result - The result of the other analysis.
   * @param em - The EntityManager.
   * @param progressListener - The Progress Listener of this module.
   * @return a map with the old Basic Entities as key and the stored real Entities as values.
   */
  private Map<BasicEntity, Entity> storeEntities(Document document,
      List<BasicEntity> basicEntities, ModuleResultProvider result, EntityManager em,
      ProgressListener progressListener) {
    Map<BasicEntity, Entity> realEntities = new HashMap<>();
    EntityAttributes entityAttributes = result.getResultFor(EntityAttributes.class);
    EntityWordCloudResult wordClouds = result.getResultFor(EntityWordCloudResult.class);
    SentenceDetectionResult sentences = result.getResultFor(SentenceDetectionResult.class);

    // create Entity
    int currentPersonRanking = 1;
    int currentPlaceRanking = 1;
    int currentIndex = 0;
    for (BasicEntity basicEntity : basicEntities) {
      Entity entity;
      switch (basicEntity.getType()) {
        case PERSON:
          entity = createPerson(document, currentPersonRanking);
          currentPersonRanking++;
          break;
        case PLACE:
          entity = createPlace(document, currentPlaceRanking);
          currentPlaceRanking++;
          break;
        default:
          continue;
      }

      setEntityAttributes(entity, basicEntity, entityAttributes, wordClouds);
      updateDocumentMetrics(document);
      saveSentencesIntoChapters(document, sentences);

      persistEntity(entity, em);
      realEntities.put(basicEntity, entity);

      progressListener.observeProgress(FIRST_LOOP_DURATION_FRACTION * currentIndex
          / basicEntities.size());
      currentIndex++;
    }
    return realEntities;
  }

  /**
   * Persists the found Relations.
   * 
   * @param realEntities - The new created and stored Entities, and the BasicEntites they are
   *        created from.
   * @param basicEntities - The found BasicEntities in the document.
   * @param result - The result of the other analysis.
   * @param em - The EntityManager.
   * @param progressListener - The Progress Listener of this module.
   */
  private void storeRelations(Map<BasicEntity, Entity> realEntities,
      List<BasicEntity> basicEntities, ModuleResultProvider result, EntityManager em,
      ProgressListener progressListener) {
    EntityRelations relations = result.getResultFor(EntityRelations.class);
    int currentIndex = 0;
    for (BasicEntity basicSourceEntity : basicEntities) {
      Entity source = realEntities.get(basicSourceEntity);
      Map<BasicEntity, Double> relationWeights = relations.getRelatedEntities(basicSourceEntity);

      if (relationWeights == null) {
        continue;
      }

      for (Map.Entry<BasicEntity, Double> targetEntry : relationWeights.entrySet()) {
        Entity target = realEntities.get(targetEntry.getKey());
        EntityRelation relation =
            createRelation(source, target, targetEntry.getValue(),
                relations.getWeightOverTime(basicSourceEntity, targetEntry.getKey()));

        persistRelation(relation, em);
      }

      progressListener.observeProgress(FIRST_LOOP_DURATION_FRACTION + SECOND_LOOP_DURATION_FRACTION
          * currentIndex / basicEntities.size());
    }
  }

  /**
   * Create a new Relation and sets all needed attributes.
   * 
   * @param source - The source Entity of the Relation.
   * @param target - The target Entity of the Relation.
   * @param weight - The weight of the Relation.
   * @param weightOverTime The weight over time of the Relation.
   * @return The Relation.
   */
  private EntityRelation createRelation(Entity source, Entity target, double weight,
      double[] weightOverTime) {
    EntityRelation relation = new EntityRelation();
    relation.setOriginEntity(source);
    relation.setRelatedEntity(target);
    relation.setWeight(weight);
    relation.setWeightOverTime(weightOverTime);
    return relation;
  }

  /**
   * Create a new Place which is an Entity.
   * 
   * @param document - The Document the analysis result belongs to.
   * @param currentPlaceRanking - The ranking of the Place in the document, 1 or greater.
   * @return The Place.
   */
  private Entity createPlace(Document document, int currentPlaceRanking) {
    Place place = new Place();
    document.getContent().getPlaces().add(place);
    place.setRankingValue(currentPlaceRanking);
    return place;
  }

  /**
   * Create a new Person which is an Entity.
   * 
   * @param document - The Document the analysis result belongs to.
   * @param currentPersonRanking - The ranking of the Person in the document, 1 or greater.
   * @return The Person.
   */
  private Entity createPerson(Document document, int currentPersonRanking) {
    Person person = new Person();
    document.getContent().getPersons().add(person);
    person.setRankingValue(currentPersonRanking);
    return person;
  }

  /**
   * Persists the given Entity.
   * 
   * @param entity - The Entity to persist.
   * @param em - The Entity Manager.
   */
  private void persistEntity(Entity entity, EntityManager em) {
    em.persist(entity);
    em.persist(entity.getWordCloud());
  }

  /**
   * Perists the given Relation.
   * 
   * @param relation - The Relation to persist.
   * @param em - The Entity Manager.
   */
  private void persistRelation(EntityRelation relation, EntityManager em) {
    em.persist(relation);
  }

  /**
   * Saves the Sentences in the Chapters of the Document.
   * 
   * @param document - The Document the analysis result belongs to.
   * @param sentences - The Sentences found by the analysis.
   */
  private void saveSentencesIntoChapters(Document document, SentenceDetectionResult sentences) {
    for (DocumentPart documentPart : document.getContent().getParts()) {
      for (Chapter chapter : documentPart.getChapters()) {
        chapter.setSentences(sentences.getSentencesInChapter(chapter));
      }
    }
  }

  /**
   * Set all attributes of an new created entity.
   * 
   * @param entity - The Entity.
   * @param basicEntity - The original BasicEntity, which should be transformed into the given
   *        Entity.
   * @param entityAttributes - The attributes of the Entity, found by the analysis.
   * @param wordClouds - The word cloud, found by the analysis.
   */
  private void setEntityAttributes(Entity entity, BasicEntity basicEntity,
      EntityAttributes entityAttributes, EntityWordCloudResult wordClouds) {
    entity.setId(basicEntity.getEntityId());
    entity.setFrequency(basicEntity.getOccurrences().size());
    entity.setDisplayName(basicEntity.getDisplayName());
    entity.getAttributes().addAll(basicEntity.getNameAttributes());
    entity.getAttributes().addAll(entityAttributes.getAttributesForEntity(basicEntity));
    entity.getOccurrences().addAll(basicEntity.getOccurrences());
    entity.setWordCloud(wordClouds.getWordCloudForEntity(basicEntity));
  }

  /**
   * Update the metrics of the Document, because number of Places and Persons are known now.
   * 
   * @param document - The Document the analysis result belongs to.
   */
  private void updateDocumentMetrics(Document document) {
    DocumentMetrics metrics = document.getMetrics();
    metrics.setPersonCount(document.getContent().getPersons().size());
    metrics.setPlaceCount(document.getContent().getPlaces().size());
  }
}
