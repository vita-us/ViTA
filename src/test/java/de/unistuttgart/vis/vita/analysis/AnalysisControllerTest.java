package de.unistuttgart.vis.vita.analysis;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.Model;

public class AnalysisControllerTest {

  private AnalysisController controller;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    de.unistuttgart.vis.vita.model.Model testModel = new Model();
    controller = new AnalysisController(testModel);
  }

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void testScheduleDocumentAnalysis() {
    controller.scheduleDocumentAnalysis(new File("correctFilePath"));
    assertThat(controller.documentsInQueue(), is(1));
  }
  
  @Test
  public void testStartAnalysis() {
    controller.scheduleDocumentAnalysis(new File("correctFilePath"));
    controller.startAnalysis();
    assertThat(controller.isWorking(), is(true));
  }
  
  @Test
  public void testCancelAnalysis() {
    String documentId = controller.scheduleDocumentAnalysis(new File("correctFilePath"));
    controller.startAnalysis();
    assertThat(controller.isWorking(), is(true));
    controller.cancelAnalysis(documentId);
    assertThat(controller.isWorking(), is(false));
  }
  
  @Test
  public void testRestartAnalysis() {
    fail("Not yet implemented");
  }

}
