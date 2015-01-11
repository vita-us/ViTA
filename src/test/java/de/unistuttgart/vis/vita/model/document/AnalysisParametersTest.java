package de.unistuttgart.vis.vita.model.document;

import static com.jayway.awaitility.Awaitility.await;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.hamcrest.Matchers.*;
import gate.creole.Parameter;

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
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.modules.MainAnalysisModule;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;

public class AnalysisParametersTest {
  private Document doc;
  private Model model;
  private EntityManager em;
  private AnalysisExecutor executor;
  
  private AnalysisParameters parameters;
  
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  
  @Before
  public void setUp() throws URISyntaxException {   

    UnitTestModel.startNewSession();
    model = new UnitTestModel();
    doc = new Document();
    em = model.getEntityManager();
    
    resultProvider = mock(ModuleResultProvider.class);
    progressListener = mock(ProgressListener.class);
    parameters = new AnalysisParameters();
    
    em.getTransaction().begin();
    em.persist(doc);
    em.getTransaction().commit();
    
    ModuleRegistry registry = ModuleRegistry.getDefaultRegistry();
    AnalysisExecutorFactory factory = new DefaultAnalysisExecutorFactory(model, registry);
    Path docPath = Paths.get(MainAnalysisModule.class.getResource("LOTR_CP1.txt").toURI());
    executor = factory.createExecutor(doc.getId(), docPath, parameters);
    
  }
  
  private void execute() {
    AnalysisObserver observer = mock(AnalysisObserver.class);
    executor.addObserver(observer);
    
    executor.start();
    
    await().atMost(Duration.ONE_MINUTE).until(executorStatus(), is(not(AnalysisStatus.RUNNING)));
    assertThat(executor.getStatus(), is(AnalysisStatus.FINISHED));
    
    em.refresh(doc);
    
  }

  private Callable<AnalysisStatus> executorStatus() {
    return new Callable<AnalysisStatus>() {
      @Override
      public AnalysisStatus call() throws Exception {
        return executor.getStatus();
      }
    };
  }

  @Test
  public void testMinWordCloudItemsCount() {
    parameters.setWordCloudItemsCount(10);
    assertThat(parameters.getWordCloudItemsCount(), is(10));
    execute();
  }
  
  @Test
  public void testMaxWordCloudItemsCount() {
    parameters.setWordCloudItemsCount(100);
    assertThat(parameters.getWordCloudItemsCount(), is(100));
    execute();
  }
  
  @Test
  public void testIllegalWordCloudItemsCount() {
    parameters.setWordCloudItemsCount(-100);
    execute();
  }
  
  @Test
  public void testMinRelationTimeStepCount() {
    parameters.setRelationTimeStepCount(1);
    assertThat(parameters.getRelationTimeStepCount(), is(1));
    execute();
  }
  
  @Test
  public void testMaxRelationTimeStepCount() {
    parameters.setRelationTimeStepCount(1000);
    assertThat(parameters.getRelationTimeStepCount(), is(1000));
    execute();
  }
  
  @Test
  public void testIllegalRelationTimeStepCount() {
    parameters.setRelationTimeStepCount(-10);
    execute();
  }
  
  @Test
  public void testStopWordListEnabledTrue() {
    parameters.setStopWordListEnabled(true);
    assertEquals(parameters.isStopWordListEnabled(), true);
    execute();
  }
  
  @Test
  public void testStopWordListEnabledFalse() {
    parameters.setStopWordListEnabled(false);
    assertEquals(parameters.isStopWordListEnabled(), false);
    execute();
  }
  
}
