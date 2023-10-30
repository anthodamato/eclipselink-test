package com.test.eclipselink;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.test.eclipselink.model.Address;
import com.test.eclipselink.model.Citizen;

public class RemoveTest {

    @Test
    public void remove() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("citizens");
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Citizen citizen = new Citizen();
        citizen.setName("Marc");
        em.persist(citizen);

        Address address = new Address();
        address.setName("Regent St");
        em.persist(address);

        tx.commit();

        tx.begin();
        em.remove(citizen);

        Citizen c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNull(c);

        c = em.find(Citizen.class, address.getId());
        Assertions.assertNull(c);

        em.remove(address);

        tx.commit();
        em.close();
        emf.close();
    }

}
