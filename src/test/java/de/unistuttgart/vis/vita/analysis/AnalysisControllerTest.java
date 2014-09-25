package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;

public class AnalysisControllerTest {
  private Model model;
  private EntityManager entityManager;
  private EntityTransaction entityTransaction;
  private AnalysisController controller;
  private AnalysisExecutorFactory executorFactory;
  private AnalysisExecutor executor;
  private AnalysisObserver analysisObserver;

  @Before
  public void setUp() throws Exception {
    entityManager = mock(EntityManager.class);
    entityTransaction = mock(EntityTransaction.class);
    when(entityManager.getTransaction()).thenReturn(entityTransaction);
    model = mock(Model.class);
    when(model.getEntityManager()).thenReturn(entityManager);
    executorFactory = mock(AnalysisExecutorFactory.class);
    controller = new AnalysisController(model, executorFactory);
  }

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void testScheduleDocumentAnalysis() {
    ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
    
    Path path = Paths.get("path/to/file.name");
    prepareExecutor(path);
    String id = controller.scheduleDocumentAnalysis(path);
    verifyExecutorCreated(id, path);

    assertThat(controller.documentsInQueue(), is(0)); // nothing queued, just one working
    assertThat(controller.isWorking(), is(true));
    verify(entityManager).persist(documentCaptor.capture());
    verify(entityTransaction).commit();
    Document document = documentCaptor.getValue();
    assertThat(document.getId(), is(id));
    assertThat(document.getMetadata().getTitle(), is("file.name"));

    analysisObserver.onFinish(executor);

    assertThat(controller.documentsInQueue(), is(0));
    assertThat(controller.isWorking(), is(false));
  }
  
  @Test
  public void testFurtherDocumentAreQueued() {
    Path path1 = Paths.get("path/to/file.name");
    Path path2 = Paths.get("path/to/file2.name");
    prepareExecutor(path1);

    controller.scheduleDocumentAnalysis(path1);
    controller.scheduleDocumentAnalysis(path2);

    assertThat(controller.documentsInQueue(), is(1));
  }
  
  @Test
  public void testCancelAnalysis() {
    Path path = Paths.get("correctFilePath");
    prepareExecutor(path);

    String documentId = controller.scheduleDocumentAnalysis(path);

    assertThat(controller.isWorking(), is(true));

    controller.cancelAnalysis(documentId);

    assertThat(controller.isWorking(), is(false));
  }

  @Test
  public void testCancelScheduledAnalysis() {
    Path path1 = Paths.get("correctFilePath");
    Path path2 = Paths.get("correctFilePath2");
    prepareExecutor(path1);
    controller.scheduleDocumentAnalysis(path1);
    prepareExecutor(path2);
    String id2 = controller.scheduleDocumentAnalysis(path2);

    assertThat(controller.documentsInQueue(), is(1));

    controller.cancelAnalysis(id2);

    assertThat(controller.documentsInQueue(), is(0));
  }

  @Test
  public void testCancelUnknownDocumentIsIgnored() {
    Path path1 = Paths.get("correctFilePath");
    Path path2 = Paths.get("correctFilePath2");
    prepareExecutor(path1);
    controller.scheduleDocumentAnalysis(path1);
    prepareExecutor(path2);
    controller.scheduleDocumentAnalysis(path2);

    assertThat(controller.documentsInQueue(), is(1));

    controller.cancelAnalysis("some-unkonw-id");

    assertThat(controller.documentsInQueue(), is(1));
  }

  @Test
  public void testStartsQueuedAnalysisWhenFirstOneFinishes() {
    Path path1 = Paths.get("path/to/file.name");
    Path path2 = Paths.get("path/to/file2.name");
    prepareExecutor(path1);

    String id1 = controller.scheduleDocumentAnalysis(path1);
    String id2 = controller.scheduleDocumentAnalysis(path2);

    verifyExecutorCreated(id1, path1);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(1));
    assertThat(controller.isWorking(), is(true));

    AnalysisExecutor executor1 = executor;
    prepareExecutor(path2);
    analysisObserver.onFinish(executor1);

    verifyExecutorCreated(id2, path2);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(0));
    assertThat(controller.isWorking(), is(true));
  }

  @Test
  public void testStartsQueuedAnalysisWhenFirstOneFails() {
    Path path1 = Paths.get("path/to/file.name");
    Path path2 = Paths.get("path/to/file2.name");
    prepareExecutor(path1);
    String id1 = controller.scheduleDocumentAnalysis(path1);
    String id2 = controller.scheduleDocumentAnalysis(path2);
    verifyExecutorCreated(id1, path1);
    AnalysisExecutor executor1 = executor;
    prepareExecutor(path2);
    analysisObserver.onFail(executor1);

    verifyExecutorCreated(id2, path2);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(0));
    assertThat(controller.isWorking(), is(true));
  }

  @Test
  public void testRestartDocumentAnalysis() {
    // We need a realy database backend here
    model = Model.createUnitTestModel();
    controller = new AnalysisController(model, executorFactory);

    Path path = Paths.get("path/to/file.name");
    prepareExecutor(path);
    String id = controller.scheduleDocumentAnalysis(path);
    verifyExecutorCreated(id, path);

    controller.cancelAnalysis(id);

    prepareExecutor(path);
    controller.restartAnalysis(id);

    // Make sure it has been called the second time
    verify(executorFactory, times(2)).createExecutor(id, path);
  }

  private void prepareExecutor(Path path) {
    executor = mock(AnalysisExecutor.class);
    when(executorFactory.createExecutor(anyString(), eq(path))).thenReturn(executor);
  }

  private void verifyExecutorCreated(String id, Path path) {
    verify(executorFactory).createExecutor(id, path);
    ArgumentCaptor<AnalysisObserver> observerCaptor;
    observerCaptor = ArgumentCaptor.forClass(AnalysisObserver.class);
    verify(executor).addObserver(observerCaptor.capture());
    analysisObserver = observerCaptor.getValue();
  }

}
