package de.unistuttgart.vis.vita.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DaoFactory {
  
  private EntityManager em;
  private DocumentDao documentDao;
  private EntityDao entityDao;
  private OccurrenceDao occurrenceDao;
  private DocumentPartDao documentPartDao;
  private ChapterDao chapterDao;
  private EntityRelationDao entityRelationDao;
  private AttributeDao attributeDao;
  private PersonDao personDao;
  private PlaceDao placeDao;

  public DaoFactory(EntityManager em) {
    this.em = em;
  }
  
  public DocumentDao getDocumentDao() {
    if (documentDao == null) {
      documentDao = new DocumentDao(em);
    }
    
    return documentDao;
  }

  public EntityDao getEntityDao() {
    if (entityDao == null) {
      entityDao = new EntityDao(em);
    }
    return entityDao;
  }

  public OccurrenceDao getOccurrenceDao() {
    if (occurrenceDao == null) {
      occurrenceDao = new OccurrenceDao(em);
    }
    return occurrenceDao;
  }

  public DocumentPartDao getDocumentPartDao() {
    if (documentPartDao == null) {
      documentPartDao = new DocumentPartDao(em);
    }
    return documentPartDao;
  }

  public ChapterDao getChapterDao() {
    if (chapterDao == null) {
      chapterDao = new ChapterDao(em);
    }
    return chapterDao;
  }

  public EntityRelationDao getEntityRelationDao() {
    if (entityRelationDao == null) {
      entityRelationDao = new EntityRelationDao(em);
    }
    return entityRelationDao;
  }

  public AttributeDao getAttributeDao() {
    if (attributeDao == null) {
      attributeDao = new AttributeDao(em);
    }
    return attributeDao;
  }

  public PersonDao getPersonDao() {
    if (personDao == null) {
      personDao = new PersonDao(em);
    }
    return personDao;
  }

  public PlaceDao getPlaceDao() {
    if (personDao == null) {
      placeDao = new PlaceDao(em);
    }
    return placeDao;
  }
}
