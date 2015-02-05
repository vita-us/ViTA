package de.unistuttgart.vis.vita.model.document;

import de.unistuttgart.vis.vita.analysis.annotations.Default;
import de.unistuttgart.vis.vita.analysis.annotations.Description;
import de.unistuttgart.vis.vita.analysis.annotations.Label;
import de.unistuttgart.vis.vita.analysis.modules.StopWordList;
import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class AnalysisParameters extends AbstractEntityBase {
  @XmlElement
  @Description("The granularity of the time slider in the graph network")
  @Label("Temporal granularity in graph network")
  @Min(1)
  @Max(1000)
  private int relationTimeStepCount = 20;

  @XmlElement
  @Description("The number of items visualized in the word cloud.")
  @Label("Word Cloud items")
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
  

  public boolean getStopEntityFilter() {
    return stopEntityFilter;
  }

  public void setStopEntityFilter(boolean stopEntityFilter) {
    this.stopEntityFilter = stopEntityFilter;
  }

  @XmlElement
  @Description("Decide which NLP tool should be used for named entity recognition.")
  @Label("Choose NLP tool")
  @Default("annie")
  private EnumNLP nlpTool = EnumNLP.ANNIE;

  public EnumNLP getNlpTool() {
    return nlpTool;
  }

  public void setNlpTool(EnumNLP nlpTool) {
    this.nlpTool = nlpTool;
  }

  public int getRelationTimeStepCount() {
    return relationTimeStepCount;
  }

  public void setRelationTimeStepCount(int relationTimeStepCount) {
    this.relationTimeStepCount = relationTimeStepCount;
  }

  public int getWordCloudItemsCount() {
    return wordCloudItemsCount;
  }

  public void setWordCloudItemsCount(int wordCloudItemsCount) {
    this.wordCloudItemsCount = wordCloudItemsCount;
  }

  public boolean getStopWordListEnabled() {
    return stopWordListEnabled;
  }

  public void setStopWordListEnabled(boolean stopWordListEnabled) {
    this.stopWordListEnabled = stopWordListEnabled;
  }
}
