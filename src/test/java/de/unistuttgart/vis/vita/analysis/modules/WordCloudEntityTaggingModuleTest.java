package de.unistuttgart.vis.vita.analysis.modules;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.GlobalWordCloudResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.model.document.Document;
import static org.junit.Assert.assertThat;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

/**
 * 
 * Junit test on WordCloudEntityModule
 *
 */
public class WordCloudEntityTaggingModuleTest {

  private WordCloudEntityTaggingModule module;
  private EntityManager em;
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private Document document;

  @Before
  public void setUp() {
    
    document = new Document();
    UnitTestModel.startNewSession();
    Model model = new UnitTestModel();
    em = model.getEntityManager();
    
    WordCloud wordCloud = createWordCloud();
    document.getContent().setGlobalWordCloud(wordCloud);
    em.getTransaction().begin();
    em.persist(document);
    em.persist(wordCloud);
    em.getTransaction().commit();
    
    GlobalWordCloudResult result = getGlobalWordCloud(wordCloud);
    BasicEntityCollection collection = getBasicEntities();
    DocumentPersistenceContext context = mock(DocumentPersistenceContext.class);
    
    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(Model.class)).thenReturn(model);
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(context);
    when(resultProvider.getResultFor(GlobalWordCloudResult.class)).thenReturn(result);
    when(resultProvider.getResultFor(BasicEntityCollection.class)).thenReturn(collection);
   
    progressListener = mock(ProgressListener.class);
    
    module = new WordCloudEntityTaggingModule();
  }

  private WordCloud createWordCloud() {
   
    List<WordCloudItem> wordCloudItems = new ArrayList<WordCloudItem>();
    wordCloudItems.add(new WordCloudItem("Frodo", 7));
    wordCloudItems.add(new WordCloudItem("king", 2));
    wordCloudItems.add(new WordCloudItem("power", 4));
    wordCloudItems.add(new WordCloudItem("Mordor", 3));
    WordCloud wordCloud = new WordCloud(wordCloudItems);
    return wordCloud;
  }
  
  private void createBasicEntities(List<BasicEntity> basicEntities, String content){
    BasicEntity entity = new BasicEntity();
    Set<Attribute> set = new HashSet<Attribute>();
    Attribute attribute = new Attribute();
    attribute.setContent(content);
    set.add(attribute);
    entity.setNameAttributes(set);
    basicEntities.add(entity);
  }
  
  @Test
  public void testEntitiesIdsPersistence() throws Exception{
    
    module.storeResults(resultProvider, document, em);
    List<WordCloudItem> items = new ArrayList<WordCloudItem>(getPersistedWordCloud().getItems());
    assertThat(items.get(0).getEntityId(), not(isEmptyString()));
    assertThat(items.get(1).getEntityId(), nullValue());
    assertThat(items.get(2).getEntityId(), not(isEmptyString()));
    assertThat(items.get(3).getEntityId(), nullValue());

  }
  
  private GlobalWordCloudResult getGlobalWordCloud(WordCloud wordCloud){
    
    GlobalWordCloudResult result = mock(GlobalWordCloudResult.class);
    when(result.getGlobalWordCloud()).thenReturn(wordCloud);
    
    return result;
  }
  
  private BasicEntityCollection getBasicEntities(){
    
    List<BasicEntity> basicEntities = new ArrayList<BasicEntity>();
    createBasicEntities(basicEntities, "Frodo");
    createBasicEntities(basicEntities, "Mordor");
    BasicEntityCollection collection = mock(BasicEntityCollection.class);
    when(collection.getEntities()).thenReturn(basicEntities);
    
    return collection;
  }
  
  private WordCloud getPersistedWordCloud(){
    
    em.refresh(document);
    TypedQuery<WordCloud> query = em.createNamedQuery("WordCloud.getGlobal",
        WordCloud.class);
    query.setParameter("documentId", document.getId());
    return query.getSingleResult();
    
  }
}
