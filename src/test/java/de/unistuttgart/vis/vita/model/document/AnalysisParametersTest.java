package de.unistuttgart.vis.vita.model.document;

import com.jayway.awaitility.Duration;

import de.unistuttgart.vis.vita.analysis.AnalysisExecutor;
import de.unistuttgart.vis.vita.analysis.AnalysisExecutorFactory;
import de.unistuttgart.vis.vita.analysis.AnalysisObserver;
import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.analysis.DefaultAnalysisExecutorFactory;
import de.unistuttgart.vis.vita.analysis.ModuleClass;
import de.unistuttgart.vis.vita.analysis.ModuleRegistry;
import de.unistuttgart.vis.vita.analysis.modules.MainAnalysisModule;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class AnalysisParametersTest {

  private Document doc;
  private EntityManager em;
  private AnalysisExecutor executor;

  private AnalysisParameters parameters;

  @Before
  public void setUp() throws URISyntaxException {

    UnitTestModel.startNewSession();
    Model model = new UnitTestModel();
    doc = new Document();
    em = model.getEntityManager();
    parameters = new AnalysisParameters();

    ModuleRegistry registry = ModuleRegistry.getDefaultRegistry();
    AnalysisExecutorFactory factory = new DefaultAnalysisExecutorFactory(model, registry);
    Path docPath = Paths.get(MainAnalysisModule.class.getResource("TestDocument.txt").toURI());
    doc.setParameters(parameters);
    doc.setFilePath(docPath);

    em.getTransaction().begin();
    em.persist(doc);
    em.getTransaction().commit();

    executor = factory.createExecutor(doc);

  }

  private void executeFinish() {
    AnalysisObserver observer = mock(AnalysisObserver.class);
    executor.addObserver(observer);

    executor.start();

    await().atMost(Duration.ONE_MINUTE).until(executorStatus(), is(not(AnalysisStatus.RUNNING)));
    assertThat(executor.getStatus(), is(AnalysisStatus.FINISHED));

    em.refresh(doc);
  }

  private Map<ModuleClass, Exception> executeFail() {
    AnalysisObserver observer = mock(AnalysisObserver.class);
    executor.addObserver(observer);

    executor.start();

    await().atMost(Duration.ONE_MINUTE).until(executorStatus(), is(not(AnalysisStatus.RUNNING)));
    assertThat(executor.getStatus(), is(AnalysisStatus.FAILED));

    em.refresh(doc);

    return executor.getFailedModules();
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
    executeFinish();
  }

  @Test
  public void testMaxWordCloudItemsCount() {
    parameters.setWordCloudItemsCount(100);
    assertThat(parameters.getWordCloudItemsCount(), is(100));
    executeFinish();
  }

  @Test
  public void testIllegalWordCloudItemsCount() {
    parameters.setWordCloudItemsCount(-100);
    Map<ModuleClass, Exception> moduleClassExceptionMap = executeFail();

    for (Exception exception : moduleClassExceptionMap.values()) {
      assertThat(exception, instanceOf(IllegalArgumentException.class));
    }
  }

  @Test
  public void testMinRelationTimeStepCount() {
    parameters.setRelationTimeStepCount(1);
    assertThat(parameters.getRelationTimeStepCount(), is(1));
    executeFinish();
  }

  @Test
  public void testMaxRelationTimeStepCount() {
    parameters.setRelationTimeStepCount(1000);
    assertThat(parameters.getRelationTimeStepCount(), is(1000));
    executeFinish();
  }

  @Test
  public void testIllegalRelationTimeStepCount() {
    parameters.setRelationTimeStepCount(-10);
    Map<ModuleClass, Exception> moduleClassExceptionMap = executeFail();

    for (Exception exception : moduleClassExceptionMap.values()) {
      assertThat(exception, instanceOf(NegativeArraySizeException.class));
    }
  }

  @Test
  public void testStopWordListEnabledTrue() {
    parameters.setStopWordListEnabled(true);
    assertEquals(parameters.getStopWordListEnabled(), true);
    executeFinish();
  }

  @Test
  public void testStopWordListEnabledFalse() {
    parameters.setStopWordListEnabled(false);
    assertEquals(parameters.getStopWordListEnabled(), false);
    executeFinish();
  }

}
