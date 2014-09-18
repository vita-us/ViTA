package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.entity.Attribute;

@XmlRootElement
public class AttributesResponse extends AbstractListResponse {
	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	private List<Attribute> attributes;

	public AttributesResponse() {
		// must have a non-argument constructor
	}

	public AttributesResponse(List<Attribute> attributeList) {
		super(attributeList.size());
		this.setAttributes(attributeList);
	}

	private void setAttributes(List<Attribute> attributeList) {
		// TODO Auto-generated method stub
		this.attributes = attributeList;
		this.totalCount = attributeList.size();
	}

}
