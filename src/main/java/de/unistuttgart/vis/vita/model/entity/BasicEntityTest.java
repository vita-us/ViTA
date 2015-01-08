package de.unistuttgart.vis.vita.model.entity;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.TextSpan;


public class BasicEntityTest {
  @Test
  public void testMerge() {
    Chapter chapter = new Chapter();
    BasicEntity e1 = new BasicEntity();
    e1.setDisplayName("Frodo");
    e1.setType(EntityType.PERSON);
    Attribute nameAttr1 = new Attribute(AttributeType.NAME, "Frodo");
    nameAttr1.getOccurrences().add(new TextSpan(chapter, 0, 10));
    nameAttr1.getOccurrences().add(new TextSpan(chapter, 30, 40));
    e1.getNameAttributes().add(nameAttr1);

    BasicEntity e2 = new BasicEntity();
    e2.setDisplayName("Mr. Frodo");
    e2.setType(EntityType.PERSON);
    Attribute nameAttr2 = new Attribute(AttributeType.NAME, "Mr. Frodo");
    nameAttr2.getOccurrences().add(new TextSpan(chapter, 100, 110));
    e2.getNameAttributes().add(nameAttr2);
    Attribute nameAttr3 = new Attribute(AttributeType.NAME, "Frodo");
    nameAttr3.getOccurrences().add(new TextSpan(chapter, 130, 140));
    e2.getNameAttributes().add(nameAttr3);

    e1.merge(e2);
    assertThat(e1.getDisplayName(), is("Frodo"));
    assertThat(e1.getType(), is(EntityType.PERSON));
    assertThat(e1.getNameAttributes(), hasSize(2));

    Attribute firstAttr = e1.getNameAttributes().first();
    assertThat(firstAttr.getType(), is(AttributeType.NAME));
    assertThat(firstAttr.getContent(), is("Frodo")); // occurs most often
    assertThat(firstAttr.getOccurrences(), contains(
        new TextSpan(chapter, 0, 10), new TextSpan(chapter, 30, 40),
        new TextSpan(chapter, 130, 140)));

    Attribute secondAttr = e1.getNameAttributes().last();
    assertThat(secondAttr.getType(), is(AttributeType.NAME));
    assertThat(secondAttr.getContent(), is("Mr. Frodo"));
    assertThat(secondAttr.getOccurrences(), contains(
        new TextSpan(chapter, 100, 110)));
  }
}
