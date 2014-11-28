/*
 * EntityAttributeModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityAttributes;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import gate.Annotation;

/**
 * This module is dependent for finding all possible attributes like gender,... for the entities.
 */
@AnalysisModule(dependencies = {ImportResult.class, AnnieNLPResult.class,
                                BasicEntityCollection.class}, weight = 0.1)
public class EntityAttributeModule extends Module<EntityAttributes> {

  private ImportResult importResult;
  private AnnieNLPResult annieNLPResult;
  private BasicEntityCollection entities;
  private Map<BasicEntity, Set<AttributeType>> containedTypes;

  @Override
  public EntityAttributes execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    importResult = results.getResultFor(ImportResult.class);
    annieNLPResult = results.getResultFor(AnnieNLPResult.class);
    entities = results.getResultFor(BasicEntityCollection.class);
    containedTypes = new HashMap<>();

    for (BasicEntity basicEntity : entities.getEntities()) {
      containedTypes.put(basicEntity, new HashSet<AttributeType>());
    }

    startAnalysis();

    System.out.println(entities.getEntities());

    return new EntityAttributes() {
      @Override
      public Set<Attribute> getAttributesForEntity(BasicEntity entity) {
        return entity.getAttributes();
      }
    };
  }

  /**
   * Starts the analysis through the whole annotations.
   */
  private void startAnalysis() {
    List<DocumentPart> documentParts = importResult.getParts();

    for (DocumentPart part : documentParts) {
      List<Chapter> chapters = part.getChapters();

      for (Chapter chapter : chapters) {
        Set<Annotation> annotations = filterEntityAnnotations(
            annieNLPResult.getAnnotationsForChapter(chapter));

        for (Annotation annieAnnotation : annotations) {
          if (annieAnnotation.getFeatures().containsKey("gender")) {
            String annotatedText = getAnnotatedText(chapter.getText(), annieAnnotation);
            applyGender(annotatedText, annieAnnotation);
          }
        }
      }
    }
  }

  /**
   * Applies the gender attribute to the given entity name. If a gender already exists in the entity
   * do nothing.
   *
   * @param entityName      A name of the desired entity.
   * @param annieAnnotation The current annotation.
   */
  private void applyGender(String entityName, Annotation annieAnnotation) {
    BasicEntity theEntity = getEntityForName(entityName);

    if (theEntity != null) {
      if (!containedTypes.get(theEntity).contains(AttributeType.GENDER)) {
        Object gender = annieAnnotation.getFeatures().get("gender");

        if (gender != null) {
          theEntity.getAttributes().add(new Attribute(AttributeType.GENDER, gender.toString()));
          containedTypes.get(theEntity).add(AttributeType.GENDER);
        }
      }
    }
  }

  /**
   * Searches the correct entity to a given name.
   *
   * @param entityName The name to search for.
   * @return The correct entity or null if not found.
   */
  private BasicEntity getEntityForName(String entityName) {
    for (BasicEntity basicEntity : entities.getEntities()) {
      for (Attribute attribute : basicEntity.getNameAttributes()) {
        if (attribute.getContent().equals(entityName)) {
          return basicEntity;
        }
      }
    }

    return null;
  }

  /**
   * Filters the gate annotations to only contain persons.
   *
   * @param annotations The gate annotation set.
   * @return The new filtered set.
   */
  private Set<Annotation> filterEntityAnnotations(Set<Annotation> annotations) {
    Set<Annotation> extracted = new TreeSet<>();

    for (Annotation annotation : annotations) {
      if ("Person".equals(annotation.getType())) {
        extracted.add(annotation);
      }
    }

    return extracted;
  }

  /**
   * Extract the annotated text.
   *
   * @param text          The text to cut out the annotated part.
   * @param theAnnotation The annotation to work with.
   * @return The annotated text.
   */
  private String getAnnotatedText(String text, Annotation theAnnotation) {
    int start = theAnnotation.getStartNode().getOffset().intValue();
    int end = theAnnotation.getEndNode().getOffset().intValue();
    return text.substring(start, end);
  }
}
