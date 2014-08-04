package de.unistuttgart.vis.vita.services.responses;

/**
 * Holds version information about the REST API
 */
public class VersionInfo {
  private String api;

  /**
   * Gets the REST API version, as version string (e.g. 1.3-beta1)
   * 
   * @return the version
   */
  public String getApi() {
    return api;
  }

  /**
   * Sets the REST API version, as version string (e.g. 1.3-beta1)
   * 
   * @param api the API version
   */
  public void setApi(String api) {
    this.api = api;
  }
}
