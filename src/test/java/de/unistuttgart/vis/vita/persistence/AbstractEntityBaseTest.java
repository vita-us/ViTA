package de.unistuttgart.vis.vita.persistence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.persistence.TypedQuery;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Person;

/*
 * This class tests the functionality implemented in AbstractEntityBase, but uses concrete classes
 * for instantiation purpose.
 */
public class AbstractEntityBaseTest extends AbstractPersistenceTest {
  @Test
  public void testHasUniqueIDAfterCreation() {
    Attribute attribute1 = new Attribute();
    assertThat(attribute1.getId(), is(not(nullValue())));
    assertEquals(attribute1.getId(), attribute1.getId());

    Attribute attribute2 = new Attribute();
    assertThat(attribute2.getId(), is(not(CoreMatchers.equalTo(attribute1.getId()))));
  }

  @Test
  public void testIdDoesNotChangeWhenPersisting() {
    Person entity = new Person();
    entity.setDisplayName("the display name");
    String before = entity.getId();
    em.persist(entity);
    assertEquals(entity.getId(), before);

    startNewTransaction();

    TypedQuery<Person> query = em.createNamedQuery("Person.findPersonById", Person.class);
    query.setParameter("personId", entity.getId());
    Person entityFromDB = query.getSingleResult();
    assertEquals(entity.getDisplayName(), entityFromDB.getDisplayName());
    assertEquals(entity.getId(), entityFromDB.getId());
    assertEquals(entity, entityFromDB);
  }

  @Test
  public void testEquals() {
    Person entity = new Person();
    Person entity2 = new Person();
    assertTrue(entity.equals(entity));
    assertFalse(entity.equals(entity2));
    assertFalse(entity.equals(null));
    assertFalse(entity.equals("not an entity"));
  }

  @Test
  public void testHashCode() {
    Person entity = new Person();
    Person entity2 = new Person();
    assertEquals(entity.hashCode(), entity.hashCode());
    assertNotEquals(entity.hashCode(), entity2.hashCode());
  }
}
