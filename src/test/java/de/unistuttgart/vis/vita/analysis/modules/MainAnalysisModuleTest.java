package de.unistuttgart.vis.vita.analysis.modules;

import static com.jayway.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import com.jayway.awaitility.Duration;

import de.unistuttgart.vis.vita.analysis.AnalysisExecutor;
import de.unistuttgart.vis.vita.analysis.AnalysisExecutorFactory;
import de.unistuttgart.vis.vita.analysis.AnalysisObserver;
import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.analysis.DefaultAnalysisExecutorFactory;
import de.unistuttgart.vis.vita.analysis.ModuleRegistry;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Integration test for the analysis
 */
public class MainAnalysisModuleTest {
  private Document document;
  private Model model;
  private EntityManager em;
  private AnalysisExecutor executor;
  
  @Before
  public void setUp() throws URISyntaxException {
    UnitTestModel.startNewSession();
    model = new UnitTestModel();
    document = new Document();
    em = model.getEntityManager();
    
    em.getTransaction().begin();
    em.persist(document);
    em.getTransaction().commit();
    
    ModuleRegistry registry = ModuleRegistry.getDefaultRegistry();
    AnalysisExecutorFactory factory = new DefaultAnalysisExecutorFactory(model, registry);
    Path docPath = Paths.get(getClass().getResource("LOTR_CP1.txt").toURI());
    executor = factory.createExecutor(document.getId(), docPath);
  }

  @Test
  public void test() {
    execute();
    System.out.println(document.getContent().getPersons());

    Person bilbo = getPerson("Bilbo");
    assertThat(bilbo.getOccurrences().size(), greaterThanOrEqualTo(10));
  }

  private void execute() {
    AnalysisObserver observer = mock(AnalysisObserver.class);
    executor.addObserver(observer);
    
    executor.start();
    
    await().atMost(Duration.ONE_MINUTE).until(executorStatus(), is(not(AnalysisStatus.RUNNING)));
    assertThat(executor.getStatus(), is(AnalysisStatus.FINISHED));
    
    em.refresh(document);
  }

  private Callable<AnalysisStatus> executorStatus() {
    return new Callable<AnalysisStatus>() {
      @Override
      public AnalysisStatus call() throws Exception {
        return executor.getStatus();
      }
    };
  }

  private Person getPerson(String name) {
    for (Person person : document.getContent().getPersons()) {
      for (Attribute attr : person.getAttributes()) {
        if (attr.getType() == AttributeType.NAME && attr.getContent().equals(name))
          return person;
      }
    }
    throw new AssertionError("Person " + name + " does not exist");
  }
}
