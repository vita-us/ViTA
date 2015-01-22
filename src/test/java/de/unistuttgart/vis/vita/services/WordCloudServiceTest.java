package de.unistuttgart.vis.vita.services;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

/**
 * Checks whether word clouds can be caught via REST service.
 */
public class WordCloudServiceTest extends ServiceTest {

  private String entityId;
  private String path;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    EntityManager em = getModel().getEntityManager();

    // set up document parts test data
    Document testDoc = new DocumentTestData().createTestDocument(1);

    WordCloud globalWordCloud = new WordCloud(Arrays.asList(
        new WordCloudItem("peril", 3), new WordCloudItem("comfortable", 1)));
    testDoc.getContent().setGlobalWordCloud(globalWordCloud);

    WordCloud entityWordCloud = new WordCloud(Arrays.asList(
        new WordCloudItem("peril", 2), new WordCloudItem("comfortable", 1)));

    Person person = new Person();
    person.setWordCloud(entityWordCloud);
    testDoc.getContent().getPersons().add(person);

    String docId = testDoc.getId();
    entityId = person.getId();

    path = "documents/" + docId + "/wordcloud";

    // persist test data in database
    em.getTransaction().begin();
    em.persist(globalWordCloud);
    em.persist(entityWordCloud);
    em.persist(person);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(WordCloudService.class);
  }

  @Test
  public void testGetGlobalWordCloud() {
    WordCloud actualResponse = target(path).request().get(WordCloud.class);

    assertThat(actualResponse, is(not(nullValue())));
    assertThat(actualResponse.getItems(), contains(
        new WordCloudItem("peril", 3), new WordCloudItem("comfortable", 1)));
  }

  @Test
  public void testGetEntityWordCloud() {
    WordCloud actualResponse = target(path)
        .queryParam("entityId", entityId)
        .request()
        .get(WordCloud.class);

    assertThat(actualResponse, is(not(nullValue())));
    assertThat(actualResponse.getItems(), contains(
        new WordCloudItem("peril", 2), new WordCloudItem("comfortable", 1)));
  }
}
