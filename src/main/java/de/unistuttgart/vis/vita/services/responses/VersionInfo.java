package de.unistuttgart.vis.vita.services.responses;


import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class VersionInfo {
  String api;

  public String getApi() {
    return api;
  }

  public void setApi(String api) {
    this.api = api;
  }
}
