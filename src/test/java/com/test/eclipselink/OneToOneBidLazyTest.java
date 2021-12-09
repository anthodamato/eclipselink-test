package com.test.eclipselink;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.test.eclipselink.model.onetoone.lazy.Capital;
import com.test.eclipselink.model.onetoone.lazy.State;

/**
 * java -jar $DERBY_HOME/lib/derbyrun.jar server start
 * 
 * connect 'jdbc:derby://localhost:1527/test';
 * 
 * @author adamato
 *
 */
public class OneToOneBidLazyTest {
//	private Logger LOG = LoggerFactory.getLogger(OneToOneBidLazyTest.class);

	@Test
	public void persist() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("onetoone_bid_lazy");
		final EntityManager em = emf.createEntityManager();
		try {
			final EntityTransaction tx = em.getTransaction();
			tx.begin();

			State state = new State();
			state.setName("England");

			Capital capital = new Capital();
			capital.setName("London");

			state.setCapital(capital);

			em.persist(capital);
			em.persist(state);

			tx.commit();

			em.detach(capital);
			em.detach(state);
			State s = em.find(State.class, state.getId());

			Assertions.assertFalse(s == state);
			Assertions.assertEquals("England", state.getName());
//			LOG.info("Loading s.getCapital()");
			Capital c = s.getCapital();
			Assertions.assertNotNull(c);
			Assertions.assertEquals("London", c.getName());
		} finally {
			em.close();
			emf.close();
		}
	}

}
