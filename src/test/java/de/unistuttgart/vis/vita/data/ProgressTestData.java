package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.*;

import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;
import de.unistuttgart.vis.vita.model.progress.FeatureProgress;

/**
 * Holds test analysis progress data.
 */
public class ProgressTestData {
  
  public static final double GRAPH_VIEW_PROGRESS = 0.7123;
  public static final boolean GRAPH_VIEW_READY = false;
  
  public static final double WORD_CLOUD_PROGRESS = 1.0;
  public static final boolean WORD_CLOUD_READY = true;
  
  public static final double PLACES_PROGRESS = 0.6123;
  public static final boolean PLACES_READY = false;
  
  public static final double PERSONS_PROGRESS = 0.001;
  public static final boolean PERSONS_READY = false;
  
  public static final double FINGER_PRINT_PROGRESS = 0.81238;
  public static final boolean FINGER_PRINT_READY = false;
  
  public static final boolean TEXT_READY = true;
  public static final double TEXT_PROGRESS = 1.0;
  
  private static final double DELTA = 0.001;
  
  /** 
   * Creates test analysis progress data and returns it.
   * 
   * @return analysis progress
   */
  public AnalysisProgress createTestProgress() {
    AnalysisProgress testProgress = new AnalysisProgress();
    
    testProgress.setGraphViewProgress(new FeatureProgress(GRAPH_VIEW_PROGRESS, GRAPH_VIEW_READY));
    testProgress.setWordCloudProgress(new FeatureProgress(WORD_CLOUD_PROGRESS, WORD_CLOUD_READY));
    testProgress.setPlacesProgress(new FeatureProgress(PLACES_PROGRESS, PLACES_READY));
    testProgress.setPersonsProgress(new FeatureProgress(PERSONS_PROGRESS, PERSONS_READY));
    testProgress.setFingerPrintProgress(new FeatureProgress(FINGER_PRINT_PROGRESS, FINGER_PRINT_READY));
    testProgress.setTextProgress(new FeatureProgress(TEXT_PROGRESS, TEXT_READY));
    
    return testProgress;
  }

  /**
   * Checks whether given analysis progress matches the test data.
   * 
   * @param actualProgress - the analysis progress data to be checked
   */
  public void checkData(AnalysisProgress actualProgress) {
    assertNotNull(actualProgress);
    
    assertEquals(GRAPH_VIEW_PROGRESS, actualProgress.getGraphViewProgress().getProgress(), DELTA);
    assertEquals(GRAPH_VIEW_READY, actualProgress.getGraphViewProgress().isReady());
    
    assertEquals(WORD_CLOUD_PROGRESS, actualProgress.getWordCloudProgress().getProgress(), DELTA);
    assertEquals(WORD_CLOUD_READY, actualProgress.getWordCloudProgress().isReady());
    
    assertEquals(PLACES_PROGRESS, actualProgress.getPlacesProgress().getProgress(), DELTA);
    assertEquals(PLACES_READY, actualProgress.getPlacesProgress().isReady());
    
    assertEquals(PERSONS_PROGRESS, actualProgress.getPersonsProgress().getProgress(), DELTA);
    assertEquals(PERSONS_READY, actualProgress.getPersonsProgress().isReady());
    
    assertEquals(FINGER_PRINT_PROGRESS, actualProgress.getFingerPrintProgress().getProgress(), DELTA);
    assertEquals(FINGER_PRINT_READY, actualProgress.getFingerPrintProgress().isReady());
    
    assertEquals(TEXT_PROGRESS, actualProgress.getTextProgress().getProgress(), DELTA);
    assertEquals(TEXT_READY, actualProgress.getTextProgress().isReady());
  }

}
