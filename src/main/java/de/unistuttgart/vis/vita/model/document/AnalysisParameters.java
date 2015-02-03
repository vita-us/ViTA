package de.unistuttgart.vis.vita.model.document;

import de.unistuttgart.vis.vita.analysis.annotations.Default;
import de.unistuttgart.vis.vita.analysis.annotations.Description;
import de.unistuttgart.vis.vita.analysis.annotations.Label;
import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents objects holding all parameters of the analysis.
 */
@XmlRootElement
@Entity
public class AnalysisParameters extends AbstractEntityBase {

  @XmlElement
  @Description("The granularity of the time slider in the graph network")
  @Label("Temporal granularity in graph network")
  @Default("20")
  @Min(1)
  @Max(1000)
  private int relationTimeStepCount = 20;

  @XmlElement
  @Description("The number of items visualized in the word cloud.")
  @Label("Word Cloud items")
  @Default("100")
  @Min(10)
  @Max(100)
  private int wordCloudItemsCount = 100;

  @XmlElement
  @Description("Check to hide the most common words in the word cloud to focus on more special words")
  @Label("Enable stop word list")
  @Default("true")
  private boolean stopWordListEnabled = true;
  
  @XmlElement
  @Description("Check to remove person and place names that start with a lowercase letter")
  @Label("Remove unlikely person and place names")
  private boolean stopEntityFilter = true;

  /**
   * @return true if entities which names start with a lowercase letter should be removed, false
   * otherwise
   */
  public boolean getStopEntityFilter() {
    return stopEntityFilter;
  }

  /**
   * Set whether entities which names start with a lower case letter should be removed or not.
   *
   * @param stopEntityFilter - true for removing these entities, false otherwise
   */
  public void setStopEntityFilter(boolean stopEntityFilter) {
    this.stopEntityFilter = stopEntityFilter;
  }

  @XmlElement
  @Description("Decide which NLP tool should be used for named entity recognition.")
  @Label("Choose NLP tool")
  @Default("annie")
  private EnumNLP nlpTool = EnumNLP.ANNIE;

  /**
   * @return the NLP tool to be used
   */
  public EnumNLP getNlpTool() {
    return nlpTool;
  }

  /**
   * Sets the NLP tool to be used in the analysis.
   *
   * @param nlpTool - the NLP tool to be used during analysis
   */
  public void setNlpTool(EnumNLP nlpTool) {
    this.nlpTool = nlpTool;
  }

  /**
   * @return the number of time steps for the entity relations
   */
  public int getRelationTimeStepCount() {
    return relationTimeStepCount;
  }

  /**
   * Sets the number of time steps for the entity relations.
   *
   * @param relationTimeStepCount - the granularity of the graph network as an amount of steps
   */
  public void setRelationTimeStepCount(int relationTimeStepCount) {
    this.relationTimeStepCount = relationTimeStepCount;
  }

  /**
   * @return the number of items in the word cloud
   */
  public int getWordCloudItemsCount() {
    return wordCloudItemsCount;
  }

  /**
   * Sets the amount of items in the word clouds.
   *
   * @param wordCloudItemsCount - the amount of items in the word clouds
   */
  public void setWordCloudItemsCount(int wordCloudItemsCount) {
    this.wordCloudItemsCount = wordCloudItemsCount;
  }

  /**
   * @return true if the stop word list is enabled
   */
  public boolean getStopWordListEnabled() {
    return stopWordListEnabled;
  }

  /**
   * Sets whether the stop word list should be enabled.
   *
   * @param stopWordListEnabled - whether the stop word list should be enabled or not
   */
  public void setStopWordListEnabled(boolean stopWordListEnabled) {
    this.stopWordListEnabled = stopWordListEnabled;
  }

}
