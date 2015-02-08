/*
 * GateControllerProgress.java
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import gate.event.ProgressListener;

/**
 * A listener for the gate controllers. Updates the desired progresslistener automatically if any
 * progress update happens.
 */
public class GateControllerProgress implements ProgressListener {

  private de.unistuttgart.vis.vita.analysis.ProgressListener progressListener;
  private int oldProgress = 0;
  private int documentsFinished = 0;
  private double progressSteps;
  private int maxDocuments;
  private static final int PROGRESS_RESET_SPAN = 20;

  /**
   * Create new progress listener.
   * 
   * @param progressListener The module progress listener which should be update if the analysis
   *        makes progress.
   * @param maxDocuments The maximum amount of documents the controller corpus has.
   */
  public GateControllerProgress(
      de.unistuttgart.vis.vita.analysis.ProgressListener progressListener, int maxDocuments) {
    this.progressListener = progressListener;
    this.maxDocuments = maxDocuments;

    if (maxDocuments > 0) {
      progressSteps = 1.0 / maxDocuments;
    } else {
      progressSteps = 0.0;
    }
  }

  @Override
  public void progressChanged(int i) {
    if (Thread.currentThread().isInterrupted()) {
      throw new SoftInterruptedException("Thread was interrupted. Interrupt controller!");
    }

    calcProgress(i);
  }

  @Override
  public void processFinished() {
    // when the process is completely finished, someone outside will know (because the method
    // terminated) and set the progress to 1 when he is finished too.
  }

  /**
   * Calculates the progress for all documents (a number between 0 and 1) from the gate progress and
   * calls the {@link de.unistuttgart.vis.vita.analysis.ProgressListener}.
   * 
   * @param gateProgress - The process of gate. A number between 0 and 100. Each document will start
   *        from 0 and end at 100.
   */
  private void calcProgress(int gateProgress) {
    if (oldProgress - PROGRESS_RESET_SPAN > gateProgress) {
      documentsFinished++;
    }

    double finishFactor = (double) documentsFinished / (double) maxDocuments;
    double progChapt = progressSteps * ((double) gateProgress / 100);
    oldProgress = gateProgress;
    double currentProgress = finishFactor + progChapt;

    progressListener.observeProgress(currentProgress);
  }
}
