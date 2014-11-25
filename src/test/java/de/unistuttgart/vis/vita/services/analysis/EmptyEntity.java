package de.unistuttgart.vis.vita.services.analysis;

import javax.xml.bind.annotation.XmlElement;

/**
 * This class' only purpose is to send empty PUT or POST requests
 */
public class EmptyEntity {
  @XmlElement(name = "ignored")
  private String dummy;
}
