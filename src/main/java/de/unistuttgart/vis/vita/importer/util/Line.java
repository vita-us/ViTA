package de.unistuttgart.vis.vita.importer.util;


/**
 * A Line contains a text-line of the imported file. The Line class can compute a type for the line
 * for further analysis of the text.
 */
public interface Line {
  /**
   * Gets the text of the Line.
   *
   * @return The text of the Line.
   */
  public String getText();

  /**
   * Sets the text for the Line. When the automated type computation is activated, the type can be
   * changed too.
   *
   * @param text The text of the Line.
   */
  public void setText(String text);

  /**
   * Gets the type of the set text.
   *
   * @return The type of the text.
   */
  public LineType getType();

  /**
   * Sets the type of the text. Should only be used if automated type computation is deactivated. To
   * set the type manually should be an exception, if it is impossible for the line to compute its
   * type itself.
   *
   * @param type The type of the text.
   */
  public void setType(LineType type);

  /**
   * Checks if the automated type computation is activated.
   *
   * @return true: is activated. false: is deactivated.
   */
  public boolean isAutomatedTypeComputation();
  /**
   * Activates/Deactivates the automated type computation. If activated, will compute the current
   * type instantly.
   *
   * @param automatedTypeComputation true: activate. false: deactivate.
   */
  public void setAutomatedTypeComputation(boolean automatedTypeComputation);


  /**
   * Computes the type for the text.
   */
  public void computeType();

}
