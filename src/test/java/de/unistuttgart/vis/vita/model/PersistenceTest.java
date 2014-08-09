package de.unistuttgart.vis.vita.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Person;

public class PersistenceTest {
  @Test
  public void test() {
    Model model = new Model();
    EntityManager entityManager = model.getEntityManager();
    entityManager.getTransaction().begin();
    
    Person p = new Person();
    p.setDisplayName("Frodo Baggins");
    entityManager.persist(p);

    entityManager.getTransaction().commit();
    entityManager.close();
    
    entityManager = model.getEntityManager();
    entityManager.getTransaction().begin();
    List<Person> result = entityManager.createQuery( "from Person", Person.class ).getResultList();
    assertThat(result, equalTo(Arrays.asList(p)));
    entityManager.getTransaction().commit();
    entityManager.close();
  }
}
