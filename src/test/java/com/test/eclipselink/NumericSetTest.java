package com.test.eclipselink;

import com.test.eclipselink.model.NumericSet;
import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NumericSetTest {

	private static EntityManagerFactory emf;

	@BeforeAll
	public static void beforeAll() {
		emf = Persistence.createEntityManagerFactory("numeric_set", PersistenceUnitProperties.getProperties());
	}

	@AfterAll
	public static void afterAll() {
		emf.close();
	}

	@Test
	public void persist() throws Exception {
		final EntityManager em = emf.createEntityManager();
		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		NumericSet numericSet1 = new NumericSet();
		numericSet1.setDoubleValue(10.1);
		numericSet1.setBdValue(new BigDecimal(10.2));
		em.persist(numericSet1);

		NumericSet numericSet2 = new NumericSet();
		numericSet2.setDoubleValue(10.3);
		numericSet2.setBdValue(new BigDecimal(10.5));
		em.persist(numericSet2);

		Query query = em.createQuery("select sum(ns.doubleValue) from NumericSet ns");
		Object result = query.getSingleResult();
		Assertions.assertTrue(result instanceof Double);
		Assertions.assertEquals(20.4d, result);

		tx.rollback();
		em.close();
	}

}
