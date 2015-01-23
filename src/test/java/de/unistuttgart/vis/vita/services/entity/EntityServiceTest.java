package de.unistuttgart.vis.vita.services.entity;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.dao.EntityDao;
import de.unistuttgart.vis.vita.model.dao.EntityRelationDao;
import de.unistuttgart.vis.vita.model.dao.WordCloudDao;
import de.unistuttgart.vis.vita.model.document.DocumentContent;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;


public class EntityServiceTest extends AbstractEntityServiceTest {

  private Entity testPerson;
  private Entity relatedPerson;
  private WordCloud entityWordCloud;
  private WordCloud globalWordCloud;
  private EntityDao entityDao;
  private EntityRelationDao entityRelationDao;
  private WordCloudDao wordCloudDao;

  @Override
  protected Application configure() {
    return new ResourceConfig(EntityService.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    testPerson = personTestData.createTestPerson(1);
    relatedPerson = personTestData.createTestPerson(2);

    entityId = "frodo";
    docId = "document";
    testPerson.setId("frodo");
    relatedPerson.setId("gandalf");
    testDoc.setId("document");

    EntityRelation testRelation = relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(testRelation);

    Attribute testAttribute = attributeTestData.createTestAttribute();
    testPerson.getAttributes().add(testAttribute);

    createEntityWordCloud();
    createGlobalWordCloud();
    DocumentContent content = new DocumentContent();
    content.setGlobalWordCloud(globalWordCloud);

    testDoc.setContent(content);
    testPerson.setWordCloud(entityWordCloud);

    em.getTransaction().begin();
    em.persist(testAttribute);
    em.persist(testPerson);
    em.persist(testRelation);
    em.persist(relatedPerson);
    em.persist(testDoc);
    em.persist(entityWordCloud);
    em.persist(globalWordCloud);
    em.getTransaction().commit();

  }

  private void createEntityWordCloud() {
    entityWordCloud = new WordCloud();
    Set<WordCloudItem> items = new HashSet<WordCloudItem>();
    items.add(new WordCloudItem("brave", 2));
    items.add(new WordCloudItem("beutlin", 1));
    entityWordCloud.getItems().addAll(items);
  }

  private void createGlobalWordCloud() {
    globalWordCloud = new WordCloud();
    Set<WordCloudItem> items = new HashSet<WordCloudItem>();
    items.add(new WordCloudItem("Frodo Baggins", 1));
    items.add(new WordCloudItem("beutlin", 1));
    items.add(new WordCloudItem("Gandalf", 1));

    globalWordCloud.getItems().addAll(items);
  }

  @Override
  public void testGetEntity() {
    Entity responseEntity = target(getPath("entities")).request().get(Entity.class);
    assertThat(responseEntity.getDisplayName(), is("Frodo Baggins"));
    assertThat(responseEntity.getAttributes().iterator().next().getContent(), is("Bilbo Baggins"));
  }

  @Test
  public void testDeletion() {

    entityDao = new EntityDao(em);
    entityRelationDao = new EntityRelationDao(em);
    wordCloudDao = new WordCloudDao(em);
    Response actualResponse = target("documents/document/entities/frodo").request().delete();
    assertEquals(204, actualResponse.getStatus());

    em.refresh(testDoc);

    assertThat(entityDao.findAll().size(), is(1));
    assertThat(entityRelationDao.findAll().size(), is(0));

    assertThat(wordCloudDao.findAll().size(), is(1));

    for (WordCloudItem item : wordCloudDao.getGlobalWordCloud(docId).getItems()) {
      assertThat(item.getEntityId(), nullValue());
    }
    em.close();
  }
}
