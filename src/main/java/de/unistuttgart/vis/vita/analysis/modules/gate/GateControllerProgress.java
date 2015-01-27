/*
 * GateControllerProgress.java
 *
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
   * @param progressListener The module progress listener which should be update if the analysis makes progress.
   * @param maxDocuments The maximum amount of documents the controller corpus has.
   */
  public GateControllerProgress(de.unistuttgart.vis.vita.analysis.ProgressListener progressListener,
                                int maxDocuments) {
    this.progressListener = progressListener;
    this.maxDocuments = maxDocuments;

    if (maxDocuments > 0) {
      progressSteps = 1 / maxDocuments;
    } else {
      progressSteps = 0;
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

  }

  private void calcProgress(int i) {
    if (oldProgress - PROGRESS_RESET_SPAN > i) {
      documentsFinished++;
    }

    double finishFactor = (double) documentsFinished / (double) maxDocuments;
    double progChapt = progressSteps * ((double) i / 100);
    oldProgress = i;
    double currentProgress = finishFactor + progChapt;

    progressListener.observeProgress(currentProgress);
  }
}
