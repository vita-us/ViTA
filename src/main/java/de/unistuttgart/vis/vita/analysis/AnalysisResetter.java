package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.model.dao.*;
import de.unistuttgart.vis.vita.model.document.*;
import de.unistuttgart.vis.vita.model.entity.*;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

import javax.persistence.EntityManager;

public class AnalysisResetter {
  DocumentDao documentDao;
  EntityDao entityDao;
  OccurrenceDao occurrenceDao;
  DocumentPartDao partDao;
  ChapterDao chapterDao;
  EntityRelationDao entityRelationDao;
  AttributeDao attributeDao;

  public AnalysisResetter(EntityManager em) {
    DaoFactory daoFactory = new DaoFactory(em);
    this.documentDao = daoFactory.getDocumentDao();
    this.entityDao = daoFactory.getEntityDao();
    this.occurrenceDao = daoFactory.getOccurrenceDao();
    this.partDao = daoFactory.getDocumentPartDao();
    this.chapterDao = daoFactory.getChapterDao();
    this.entityRelationDao = daoFactory.getEntityRelationDao();
    this.attributeDao = daoFactory.getAttributeDao();
  }

  /**
   * Reverts the complete analysis
   * @param document
   */
  public void resetDocument(Document document) {
    for (Person person : document.getContent().getPersons()) {
      removeEntityData(person);
    }

    for (Place place : document.getContent().getPlaces()) {
      removeEntityData(place);
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
    documentDao.save(document);

    for (DocumentPart part : document.getContent().getParts()) {
      partDao.remove(part);
      for (Chapter chapter : part.getChapters()) {
        chapterDao.remove(chapter);
      }
    }

    for (Person person : document.getContent().getPersons()) {
      entityDao.remove(person);
    }
    for (Place place : document.getContent().getPlaces()) {
      entityDao.remove(place);
    }

  }

  /**
   * Resets the document and sets the status of the document to failed.
   *
   * @param document The document to be resetted.
   */
  public void resetAndFail(Document document) {
    resetDocument(document);
    document.getProgress().setStatus(AnalysisStatus.FAILED);
    documentDao.save(document);
  }

  /**
   * Removes all entity data
   */
  private void removeEntityData(Entity entity) {
    for (Attribute attr : entity.getAttributes()) {
      attributeDao.remove(attr);
    }

    for (EntityRelation rel : entity.getEntityRelations()) {
      entityRelationDao.remove(rel);
    }
  }
}
