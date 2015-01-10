package de.unistuttgart.vis.vita.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DaoFactory {
  
  private EntityManager em;
  private DocumentDao documentDao;
  private EntityDao entityDao;
  private TextSpanDao textSpanDao;
  private DocumentPartDao documentPartDao;
  private ChapterDao chapterDao;
  private EntityRelationDao entityRelationDao;
  private AttributeDao attributeDao;
  
  public DaoFactory(EntityManagerFactory emf) {
    this.em = emf.createEntityManager();
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

  public TextSpanDao getTextSpanDao() {
    if (textSpanDao == null) {
      textSpanDao = new TextSpanDao();
    }
    return textSpanDao;
  }

  public DocumentPartDao getDocumentPartDao() {
    if (documentPartDao == null) {
      documentPartDao = new DocumentPartDao();
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

}
