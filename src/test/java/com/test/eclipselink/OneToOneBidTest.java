package com.test.eclipselink;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.test.eclipselink.model.onetoone.Fingerprint;
import com.test.eclipselink.model.onetoone.Person;

/**
 * java -jar $DERBY_HOME/lib/derbyrun.jar server start
 * <p>
 * connect 'jdbc:derby://localhost:1527/test';
 *
 * @author adamato
 */
public class OneToOneBidTest {

    @Test
    public void persist() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("onetoone_bid");
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Person person = new Person();
        person.setName("John Smith");

        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setType("arch");
        fingerprint.setPerson(person);
        person.setFingerprint(fingerprint);

        em.persist(person);
        em.persist(fingerprint);

        tx.commit();

        tx.begin();
        em.detach(person);

        Person p = em.find(Person.class, person.getId());
        Assertions.assertNotNull(p);
        Assertions.assertFalse(p == person);
        Assertions.assertEquals(person.getId(), p.getId());
        Assertions.assertNotNull(p.getFingerprint());
        Assertions.assertEquals("John Smith", p.getName());
        Assertions.assertEquals("arch", p.getFingerprint().getType());
        em.remove(p.getFingerprint());
        em.remove(p);
        tx.commit();

        em.close();
        emf.close();
    }

}
