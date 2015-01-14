package de.unistuttgart.vis.vita.analysis;

import javax.persistence.EntityManager;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

public class AnalysisResetter {
  /**
   * Reverts the complete analysis
   * @param document
   */
  public static void resetDocument(EntityManager em, Document document) {
    em.getTransaction().begin();
    for (Person person : document.getContent().getPersons()) {
      removeEntity(em, person);
    }
    for (Place place : document.getContent().getPlaces()) {
      removeEntity(em, place);
    }
    for (DocumentPart part : document.getContent().getParts()) {
      for (Chapter chapter : part.getChapters()) {
        em.remove(chapter.getRange());
        em.remove(chapter);
      }
      em.remove(part);
    }
    document.getContent().getParts().clear();
    document.getContent().getPersons().clear();
    document.getContent().getPlaces().clear();
    DocumentMetadata newMetadata = new DocumentMetadata();
    newMetadata.setTitle(document.getMetadata().getTitle());
    document.setMetadata(newMetadata);
    document.setProgress(new AnalysisProgress());
    document.getProgress().setStatus(AnalysisStatus.READY);
    document.setMetrics(new DocumentMetrics());
    em.getTransaction().commit();
  }
  
  /**
   * Removes the complete entity
   */
  private static void removeEntity(EntityManager em, Entity entity) {
    em.remove(entity);
    for (Attribute attr : entity.getAttributes()) {
      em.remove(attr);
    }
    for (EntityRelation rel : entity.getEntityRelations()) {
      em.remove(rel);
    }
    for (Occurence span : entity.getOccurrences()) {
      em.remove(span);
    }
  }
}
