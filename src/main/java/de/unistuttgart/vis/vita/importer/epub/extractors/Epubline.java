package de.unistuttgart.vis.vita.importer.epub.extractors;

/**
 * Utility class for Epub2Extraction which contains the mode, the text/line and the id if available
 * 
 *
 */
public class Epubline {
  private String mode;
  private String epubline;
  private String id;

  public Epubline(String newMode, String newEpubline, String newId) {

    this.mode = newMode;
    this.epubline = newEpubline;
    this.id = newId;
  }

  public String getMode() {
    return mode;
  }

  public String getEpubline() {
    return epubline;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public void setEpubline(String epubline) {
    this.epubline = epubline;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


}
