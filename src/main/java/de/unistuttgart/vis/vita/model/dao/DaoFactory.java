package de.unistuttgart.vis.vita.model.dao;

import javax.persistence.EntityManager;

/**
 * Represents a Factory for data access objects (dao), ensuring that there is only one dao for each
 * type of persisted entity.
 */
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
  private WordCloudDao wordCloudDao;

  /**
   * Creates a new instance of DaoFactory, using the given EntityManager.
   *
   * @param em - the EntityManager to be used in the DAOs created by this factory
   */
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
    if (placeDao == null) {
      placeDao = new PlaceDao(em);
    }
    return placeDao;
  }
  
  public WordCloudDao getWordCloudDao() {
    if (wordCloudDao == null) {
      wordCloudDao = new WordCloudDao(em);
    }
    return wordCloudDao;
  }

}
