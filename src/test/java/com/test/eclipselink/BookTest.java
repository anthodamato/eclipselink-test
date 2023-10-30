package com.test.eclipselink;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.test.eclipselink.model.BookSample;

/**
 * java -jar $DERBY_HOME/lib/derbyrun.jar server start
 * 
 * connect 'jdbc:derby://localhost:1527/test';
 * 
 * @author adamato
 *
 */
public class BookTest {

	@Test
	public void persist() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("books");
		final EntityManager em = emf.createEntityManager();
		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		BookSample book = new BookSample();
		book.setTitle("Marc");
		em.persist(book);
		em.flush();

		System.out.println("BookTest.persist: book.getId()=" + book.getId());

		Assertions.assertNotNull(book.getId());
		tx.commit();

		tx.begin();
		book.setTitle("The Guardian");

		BookSample b = em.find(BookSample.class, book.getId());
		Assertions.assertNotNull(b);
		em.remove(b);

		tx.commit();
		em.close();
		emf.close();
	}

}
