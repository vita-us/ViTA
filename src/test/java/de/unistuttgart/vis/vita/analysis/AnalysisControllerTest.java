package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;

public class AnalysisControllerTest {
  private Model model;
  private EntityManager entityManager;
  private AnalysisController controller;
  private AnalysisExecutorFactory executorFactory;
  private AnalysisExecutor executor;
  private AnalysisObserver analysisObserver;

  @Before
  public void setUp() throws Exception {
    entityManager = mock(EntityManager.class);
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
    verifyExecutorCreated(path);

    assertThat(controller.documentsInQueue(), is(0)); // nothing queued, just one working
    assertThat(controller.isWorking(), is(true));
    verify(entityManager).persist(documentCaptor.capture());
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
  public void testStartQueuedAnalysisWhenFirstOneFinishes() {
    Path path1 = Paths.get("path/to/file.name");
    Path path2 = Paths.get("path/to/file2.name");
    prepareExecutor(path1);

    controller.scheduleDocumentAnalysis(path1);
    controller.scheduleDocumentAnalysis(path2);

    verifyExecutorCreated(path1);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(1));
    assertThat(controller.isWorking(), is(true));

    AnalysisExecutor executor1 = executor;
    prepareExecutor(path2);
    analysisObserver.onFinish(executor1);

    verifyExecutorCreated(path2);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(0));
    assertThat(controller.isWorking(), is(true));
  }

  private void prepareExecutor(Path path) {
    executor = mock(AnalysisExecutor.class);
    when(executorFactory.createExecutor(path)).thenReturn(executor);
  }

  private void verifyExecutorCreated(Path path) {
    verify(executorFactory).createExecutor(path);
    ArgumentCaptor<AnalysisObserver> observerCaptor;
    observerCaptor = ArgumentCaptor.forClass(AnalysisObserver.class);
    verify(executor).addObserver(observerCaptor.capture());
    analysisObserver = observerCaptor.getValue();
  }

}
