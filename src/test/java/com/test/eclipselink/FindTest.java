package com.test.eclipselink;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.eclipselink.model.Address;
import com.test.eclipselink.model.Citizen;

import java.util.Set;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;

/**
 * @author adamato
 */
public class FindTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("citizens", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void find() throws Exception {
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Citizen citizen = new Citizen();
        citizen.setName("Anthony");
        em.persist(citizen);

        Assertions.assertNotNull(citizen.getId());

//		em.detach(citizen);
        Citizen c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(c);
        Assertions.assertTrue(citizen == c);
        Assertions.assertEquals("Anthony", c.getName());

        em.remove(c);
        tx.commit();
        em.close();
    }

    @Test
    public void rollback() throws Exception {
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Citizen citizen = new Citizen();
        citizen.setName("Anthony");
        em.persist(citizen);

        em.flush();
        tx.rollback();

        tx.begin();
        Citizen c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNull(c);
        tx.commit();
        em.close();
    }

    @Test
    public void lockEntity() throws Exception {
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Citizen citizen = new Citizen();
        citizen.setName("Anthony");
        em.persist(citizen);
        em.flush();
        tx.commit();

        tx.begin();
        em.lock(citizen, LockModeType.PESSIMISTIC_WRITE);
        citizen.setLastName("Smith");
        em.persist(citizen);
        em.flush();
        em.refresh(citizen);
        Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(citizen));

//	// new transaction, it should throws a lock timeout exception
//	final EntityManager em2 = emf.createEntityManager();
//	EntityTransaction tx2 = em2.getTransaction();
//	tx2.begin();
//	Citizen citizen2 = em2.find(Citizen.class, citizen.getId());
//	citizen2.setLastName("Gould");
//	tx2.commit();
//
//	// previous transaction
        citizen = em.find(Citizen.class, citizen.getId());
        Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(citizen));
        Assertions.assertEquals("Smith", citizen.getLastName());
        em.flush();
        Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, em.getLockMode(citizen));
        tx.commit();

        tx.begin();
        Assertions.assertEquals(LockModeType.NONE, em.getLockMode(citizen));
        em.remove(citizen);
        tx.commit();

        em.close();
    }

    private Citizen createCitizenAnthonySmith() {
        Citizen citizen = new Citizen();
        citizen.setName("Anthony");
        citizen.setLastName("Smith");
        return citizen;
    }

    private Citizen createCitizenLucySmith() {
        Citizen citizen = new Citizen();
        citizen.setName("Lucy");
        citizen.setLastName("Smith");
        return citizen;
    }

    private Citizen createCitizenCrown() {
        Citizen citizen = new Citizen();
        citizen.setName("Bill");
        citizen.setLastName("Crown");
        return citizen;
    }

    private Citizen createCitizenWolf() {
        Citizen citizen = new Citizen();
        citizen.setName("David");
        citizen.setLastName("Wolf");
        return citizen;
    }

    @Test
    public void criteria() {
//		org.apache.log4j.Logger.getLogger("org.hibernate.SQL").setLevel(org.apache.log4j.Level.OFF);
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Citizen citizen = createCitizenAnthonySmith();
        em.persist(citizen);
        Citizen c_Smith = em.find(Citizen.class, citizen.getId());

        citizen = createCitizenCrown();
        em.persist(citizen);
        Citizen c_Crown = em.find(Citizen.class, citizen.getId());

        Assertions.assertNotNull(citizen.getId());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Citizen> cq = cb.createQuery(Citizen.class);
        Root<Citizen> root = cq.from(Citizen.class);
        cq.select(root);

        TypedQuery<Citizen> typedQuery = em.createQuery(cq);
        List<Citizen> citizens = typedQuery.getResultList();

        Assertions.assertEquals(2, citizens.size());

        // check the references
        int counter = 0;
        for (Citizen ct : citizens) {
            if (ct.getId() == c_Crown.getId()) {
                ++counter;
                Assertions.assertTrue(ct == c_Crown);
            }

            if (ct.getId() == c_Smith.getId()) {
                ++counter;
                Assertions.assertTrue(ct == c_Smith);
            }
        }

        Assertions.assertEquals(2, counter);

        em.remove(c_Crown);
        em.remove(c_Smith);
        tx.commit();
        em.close();
    }

    @Test
    public void equalCriteria() {
//		org.apache.log4j.Logger.getLogger("org.hibernate.SQL").setLevel(org.apache.log4j.Level.OFF);
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Citizen citizen = createCitizenAnthonySmith();
        em.persist(citizen);
        Citizen c_Smith = em.find(Citizen.class, citizen.getId());

        citizen = createCitizenCrown();
        em.persist(citizen);
        Citizen c_Crown = em.find(Citizen.class, citizen.getId());

        Assertions.assertNotNull(citizen.getId());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Citizen> cq = cb.createQuery(Citizen.class);
        Root<Citizen> root = cq.from(Citizen.class);

        Predicate predicate = cb.equal(root.get("lastName"), "Smith");
        Assertions.assertEquals(BooleanOperator.AND, predicate.getOperator());
        Assertions.assertEquals(Boolean.class, predicate.getJavaType());
        Assertions.assertFalse(predicate.isCompoundSelection());

        cq.where(predicate);
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        CriteriaQuery<Citizen> cqCitizen = cq.select(root);

        TypedQuery<Citizen> typedQuery = em.createQuery(cqCitizen);
        List<Citizen> citizens = typedQuery.getResultList();

        Assertions.assertEquals(1, citizens.size());

        // check the references
        int counter = 0;
        for (Citizen ct : citizens) {
            if (ct.getId() == c_Smith.getId()) {
                ++counter;
                Assertions.assertTrue(ct == c_Smith);
            }
        }

        Assertions.assertEquals(1, counter);

        em.remove(c_Crown);
        em.remove(c_Smith);

        tx.commit();
        em.close();
    }

    @Test
    public void equalCriteriaParameter() {
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Citizen citizen = createCitizenAnthonySmith();
        em.persist(citizen);
        Citizen c_Smith = em.find(Citizen.class, citizen.getId());

        citizen = createCitizenCrown();
        em.persist(citizen);
        Citizen c_Crown = em.find(Citizen.class, citizen.getId());

        Assertions.assertNotNull(citizen.getId());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Citizen> cq = cb.createQuery(Citizen.class);
        Root<Citizen> root = cq.from(Citizen.class);

        Predicate predicate = cb.equal(root.get("lastName"), cb.parameter(String.class, "last_name"));
        Assertions.assertEquals(BooleanOperator.AND, predicate.getOperator());
        Assertions.assertEquals(Boolean.class, predicate.getJavaType());
        Assertions.assertFalse(predicate.isCompoundSelection());

        cq.where(predicate);

        Set<ParameterExpression<?>> parameterExpressions = cq.getParameters();
        Assertions.assertNotNull(parameterExpressions);
        Assertions.assertFalse(parameterExpressions.isEmpty());
        Assertions.assertEquals(1, parameterExpressions.size());
        ParameterExpression<?> parameterExpression = parameterExpressions.iterator().next();
        Assertions.assertEquals("last_name", parameterExpression.getName());
        Assertions.assertEquals(String.class, parameterExpression.getJavaType());
        Assertions.assertEquals(String.class, parameterExpression.getParameterType());
        Assertions.assertNull(parameterExpression.getPosition());

        Query query = em.createQuery(cq);
        String lastName = "Smith";
        query.setParameter("last_name", lastName);
        Citizen c = (Citizen) query.getSingleResult();

        Assertions.assertNotNull(c);

        em.remove(c_Crown);
        em.remove(c_Smith);

        tx.commit();
        em.close();
    }

    @Test
    public void orCriteria() {
        org.apache.log4j.Logger.getLogger("org.hibernate.SQL").setLevel(org.apache.log4j.Level.ALL);
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Citizen citizen = createCitizenAnthonySmith();
        em.persist(citizen);
        Citizen c_AnthonySmith = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_AnthonySmith);

        citizen = createCitizenLucySmith();
        em.persist(citizen);
        Citizen c_LucySmith = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_LucySmith);

        citizen = createCitizenCrown();
        em.persist(citizen);
        Citizen c_Crown = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_Crown);

        citizen = createCitizenWolf();
        em.persist(citizen);
        Citizen c_Wolf = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_Wolf);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Citizen> cq = cb.createQuery(Citizen.class);
        Root<Citizen> root = cq.from(Citizen.class);

        // lastname=='Smith' or 'lastName'=='Wolf'
        Predicate lastNameSmith = cb.equal(root.get("lastName"), "Smith");
        Assertions.assertEquals(BooleanOperator.AND, lastNameSmith.getOperator());
        Assertions.assertEquals(Boolean.class, lastNameSmith.getJavaType());
        Assertions.assertFalse(lastNameSmith.isCompoundSelection());
        Assertions.assertTrue(lastNameSmith.getExpressions().isEmpty());

        Predicate lastNameWolf = cb.equal(root.get("lastName"), "Wolf");

        Predicate orLastName = cb.or(lastNameSmith, lastNameWolf);
        Assertions.assertFalse(orLastName.isCompoundSelection());
        Assertions.assertEquals(BooleanOperator.OR, orLastName.getOperator());
        Assertions.assertFalse(orLastName.getExpressions().isEmpty());
        Assertions.assertEquals(2, orLastName.getExpressions().size());

        cq.where(orLastName);
        CriteriaQuery<Citizen> cqCitizen = cq.select(root);

        TypedQuery<Citizen> typedQuery = em.createQuery(cqCitizen);
        List<Citizen> citizens = typedQuery.getResultList();

        Assertions.assertEquals(3, citizens.size());

        // two 'or', one 'and'
        Predicate nameDavid = cb.equal(root.get("name"), "David");
        Predicate nameAnthony = cb.equal(root.get("name"), "Anthony");
        Predicate orName = cb.or(nameDavid, nameAnthony);

        Predicate and = cb.and(orLastName, orName);

        cq.where(and);

        cqCitizen = cq.select(root);
        typedQuery = em.createQuery(cqCitizen);
        citizens = typedQuery.getResultList();

        Assertions.assertEquals(2, citizens.size());

        em.remove(c_Crown);
        em.remove(c_AnthonySmith);
        em.remove(c_LucySmith);
        em.remove(c_Wolf);

        tx.commit();
        em.close();
    }

    @Test
    public void notCriteria() {
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Citizen citizen = createCitizenAnthonySmith();
        em.persist(citizen);
        Citizen c_AnthonySmith = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_AnthonySmith);

        citizen = createCitizenLucySmith();
        em.persist(citizen);
        Citizen c_LucySmith = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_LucySmith);

        citizen = createCitizenCrown();
        em.persist(citizen);
        Citizen c_Crown = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_Crown);

        citizen = createCitizenWolf();
        em.persist(citizen);
        Citizen c_Wolf = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(citizen.getId());
        Assertions.assertTrue(citizen == c_Wolf);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Citizen> cq = cb.createQuery(Citizen.class);
        Root<Citizen> root = cq.from(Citizen.class);

        // not lastname=='Smith'
        Predicate lastNameSmith = cb.equal(root.get("lastName"), "Smith");
        Predicate not = cb.not(lastNameSmith);
        Assertions.assertTrue(not.isNegated());

        cq.where(not);
        CriteriaQuery<Citizen> cqCitizen = cq.select(root);

        TypedQuery<Citizen> typedQuery = em.createQuery(cqCitizen);
        List<Citizen> citizens = typedQuery.getResultList();

        Assertions.assertEquals(2, citizens.size());
        CollectionUtils.containsAll(citizens, Arrays.asList(c_Wolf, c_Crown));

        em.remove(c_Crown);
        em.remove(c_AnthonySmith);
        em.remove(c_LucySmith);
        em.remove(c_Wolf);

        tx.commit();
        em.close();
    }

    private Address createRegentStAddress() {
        Address address = new Address();
        address.setName("Regent St");
        address.setPostcode("W1B4EA");
        return address;
    }

    private Address createRomfordRdAddress() {
        Address address = new Address();
        address.setName("Romford");
        return address;
    }

    @Test
    public void isNullCriteria() throws Exception {
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Address address = createRegentStAddress();
        em.persist(address);
        Address a_RegentSt = em.find(Address.class, address.getId());
        Assertions.assertTrue(address == a_RegentSt);

        address = createRomfordRdAddress();
        em.persist(address);
        Address a_RomfordRd = em.find(Address.class, address.getId());
        Assertions.assertTrue(address == a_RomfordRd);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = cb.createQuery(Address.class);
        Root<Address> root = cq.from(Address.class);

        // postcode is null
        Predicate isNull = cb.isNull(root.get("postcode"));
        cq.where(isNull);

        cq.select(root);

        TypedQuery<Address> typedQuery = em.createQuery(cq);
        List<Address> citizens = typedQuery.getResultList();

        Assertions.assertEquals(1, citizens.size());
        Assertions.assertTrue(CollectionUtils.containsAll(citizens, Arrays.asList(a_RomfordRd)));
        // postcode is not null
        Predicate isNotNull = cb.isNull(root.get("postcode"));
        cq.where(isNotNull);

        cq.select(root);

        typedQuery = em.createQuery(cq);
        citizens = typedQuery.getResultList();

        Assertions.assertEquals(1, citizens.size());
        Assertions.assertTrue(CollectionUtils.containsAll(citizens, Arrays.asList(a_RomfordRd)));
        em.remove(a_RegentSt);
        em.remove(a_RomfordRd);

        tx.commit();
        em.close();
    }

//    @Test
//    public void isNullCriteria2() {
//	final EntityManager em = emf.createEntityManager();
//	EntityTransaction tx = em.getTransaction();
//	try {
//	    tx.begin();
//	    Citizen citizen = createCitizenAnthonySmith();
//	    em.persist(citizen);
//	    Citizen c_Smith = em.find(Citizen.class, citizen.getId());
//
//	    citizen = createCitizenCrown();
//	    em.persist(citizen);
//	    Citizen c_Crown = em.find(Citizen.class, citizen.getId());
//
//	    Assertions.assertNotNull(citizen.getId());
//
//	    CriteriaBuilder cb = em.getCriteriaBuilder();
//	    CriteriaQuery<Citizen> cq = cb.createQuery(Citizen.class);
//	    Root<Citizen> root = cq.from(Citizen.class);
//
//	    Predicate predicate = cb.equal(root.get("lastName"), "Smith");
//	    Assertions.assertEquals(BooleanOperator.AND, predicate.getOperator());
//	    Assertions.assertEquals(Boolean.class, predicate.getJavaType());
//	    Assertions.assertFalse(predicate.isCompoundSelection());
//	    Predicate isNullPredicate = predicate.isNull();
//
//	    cq.where(isNullPredicate);
//	    List<Expression<Boolean>> expressions = predicate.getExpressions();
//	    Assertions.assertTrue(expressions.isEmpty());
//	    CriteriaQuery<Citizen> cqCitizen = cq.select(root);
//
//	    TypedQuery<Citizen> typedQuery = em.createQuery(cqCitizen);
//	    List<Citizen> citizens = typedQuery.getResultList();
//
//	    Assertions.assertEquals(1, citizens.size());
//
//	    em.remove(c_Crown);
//	    em.remove(c_Smith);
//	} finally {
//	    tx.commit();
//	    em.close();
//	}
//    }
}
