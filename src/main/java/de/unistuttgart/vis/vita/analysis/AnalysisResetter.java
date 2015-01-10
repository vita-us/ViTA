package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.dao.AttributeDao;
import de.unistuttgart.vis.vita.model.dao.ChapterDao;
import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.dao.DocumentPartDao;
import de.unistuttgart.vis.vita.model.dao.EntityDao;
import de.unistuttgart.vis.vita.model.dao.EntityRelationDao;
import de.unistuttgart.vis.vita.model.dao.TextSpanDao;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

@ManagedBean
public class AnalysisResetter {
  
  @Inject DocumentDao documentDao;
  @Inject EntityDao entityDao;
  @Inject TextSpanDao textSpanDao;
  @Inject DocumentPartDao partDao;
  @Inject ChapterDao chapterDao;
  @Inject EntityRelationDao entityRelationDao;
  @Inject AttributeDao attributeDao;

  public AnalysisResetter() {
    // needs zero argument constructor
  }
  
  public AnalysisResetter(Model model) {
    this.documentDao = model.getDaoFactory().getDocumentDao();
    this.entityDao = model.getDaoFactory().getEntityDao();
    this.textSpanDao = model.getDaoFactory().getTextSpanDao();
    this.partDao = model.getDaoFactory().getDocumentPartDao();
    this.chapterDao = model.getDaoFactory().getChapterDao();
    this.entityRelationDao = model.getDaoFactory().getEntityRelationDao();
    this.attributeDao = model.getDaoFactory().getAttributeDao();
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
