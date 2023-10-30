package com.test.eclipselink;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.*;

import com.test.eclipselink.model.manytoone.Department;
import com.test.eclipselink.model.manytoone.Employee;

/**
 * java -jar $DERBY_HOME/lib/derbyrun.jar server start
 * <p>
 * connect 'jdbc:derby://localhost:1527/test';
 *
 * @author adamato
 */
public class ManyToOneBidTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("manytoone_bid");
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Disabled
    @Test
    public void persist() throws Exception {
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Department department = new Department();
        department.setName("Research");

        Employee employee = new Employee();
        employee.setName("John Smith");
        employee.setSalary(new BigDecimal(130000));
        employee.setDepartment(department);

        Employee emp = new Employee();
        emp.setName("Margaret White");
        emp.setSalary(new BigDecimal(170000));
        emp.setDepartment(department);

        em.persist(employee);
        em.persist(emp);
        em.persist(department);
        em.flush();

        tx.commit();

        Assertions.assertTrue(department.getEmployees().isEmpty());

        tx.begin();
        em.detach(department);

        Department d = em.find(Department.class, department.getId());
        Assertions.assertTrue(!d.getEmployees().isEmpty());
        Assertions.assertEquals(2, d.getEmployees().size());
        Assertions.assertFalse(d == department);

        em.remove(d);
        em.remove(employee);
        em.remove(emp);
        tx.commit();

        em.close();
    }

    @Test
    public void max() {
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Department department = new Department();
        department.setName("Research");

        Employee employee1 = new Employee();
        employee1.setName("John Smith");
        employee1.setSalary(new BigDecimal(130000f));
        employee1.setDepartment(department);

        Employee employee2 = new Employee();
        employee2.setName("Margaret White");
        BigDecimal salary = new BigDecimal(170000);
        salary.setScale(2);
        employee2.setSalary(salary);
        employee2.setDepartment(department);

        Employee employee3 = new Employee();
        employee3.setName("Joshua Bann");
        employee3.setSalary(new BigDecimal(140000f));
        employee3.setDepartment(department);

        em.persist(employee1);
        em.persist(employee2);
        em.persist(employee3);
        em.persist(department);

        tx.commit();

        Assertions.assertTrue(department.getEmployees().isEmpty());

        // max
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Employee> root = criteriaQuery.from(Employee.class);
        criteriaQuery.select(cb.max(root.get("salary")));
        Query query = em.createQuery(criteriaQuery);
        BigDecimal s = (BigDecimal) query.getSingleResult();

        Assertions.assertNotNull(s);
        salary = new BigDecimal(new BigInteger("17000000"), 2);
        Assertions.assertEquals(salary, s);

        // min
        criteriaQuery.select(cb.min(root.get("salary")));
        query = em.createQuery(criteriaQuery);
        s = (BigDecimal) query.getSingleResult();

        Assertions.assertNotNull(s);
        Assertions.assertEquals(130000l, s.longValue());

        tx.begin();
        em.remove(employee1);
        em.remove(employee2);
        em.remove(employee3);
        tx.commit();

        em.close();
    }

    @Test
    public void multiSelect() {
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Department department = new Department();
        department.setName("Research");

        Employee employee1 = new Employee();
        employee1.setName("John Smith");
        employee1.setSalary(new BigDecimal(130000f));
        employee1.setDepartment(department);

        Employee employee2 = new Employee();
        employee2.setName("Margaret White");
        BigDecimal salary = new BigDecimal(170000);
        salary.setScale(2);
        employee2.setSalary(salary);
        employee2.setDepartment(department);

        Employee employee3 = new Employee();
        employee3.setName("Joshua Bann");
        employee3.setSalary(new BigDecimal(140000f));
        employee3.setDepartment(department);

        em.persist(employee1);
        em.persist(employee2);
        em.persist(employee3);
        em.persist(department);

        tx.commit();

        Assertions.assertTrue(department.getEmployees().isEmpty());

        // max
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Employee> root = criteriaQuery.from(Employee.class);
        criteriaQuery.multiselect(root.get("name"), root.get("salary")).orderBy(cb.asc(root.get("name")));
        Query query = em.createQuery(criteriaQuery);
        List<Object[]> result = query.getResultList();
        Assertions.assertEquals(3, result.size());
        Object[] r1 = result.get(0);
        Assertions.assertEquals("John Smith", r1[0]);
        salary = new BigDecimal(new BigInteger("13000000"), 2);
        Assertions.assertEquals(salary, r1[1]);
        Object[] r2 = result.get(1);
        Assertions.assertEquals("Joshua Bann", r2[0]);
        salary = new BigDecimal(new BigInteger("14000000"), 2);
        Assertions.assertEquals(salary, r2[1]);
        Object[] r3 = result.get(2);
        Assertions.assertEquals("Margaret White", r3[0]);
        salary = new BigDecimal(new BigInteger("17000000"), 2);
        Assertions.assertEquals(salary, r3[1]);

        Assertions.assertTrue(criteriaQuery.getSelection().isCompoundSelection());
        Assertions.assertEquals(Object.class, criteriaQuery.getSelection().getJavaType());

        tx.begin();
        em.remove(department);
        em.remove(employee1);
        em.remove(employee2);
        em.remove(employee3);
        tx.commit();

        em.close();
    }

    @Test
    public void like() {
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Department department = new Department();
        department.setName("Research");

        Employee employee1 = new Employee();
        employee1.setName("John Smith");
        employee1.setSalary(new BigDecimal(130000f));
        employee1.setDepartment(department);

        Employee employee2 = new Employee();
        employee2.setName("Margaret White");
        BigDecimal salary = new BigDecimal(170000);
        salary.setScale(2);
        employee2.setSalary(salary);
        employee2.setDepartment(department);

        Employee employee3 = new Employee();
        employee3.setName("Joshua Bann");
        employee3.setSalary(new BigDecimal(140000f));
        employee3.setDepartment(department);

        em.persist(employee1);
        em.persist(employee2);
        em.persist(employee3);
        em.persist(department);
        tx.commit();
        // like
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Employee> root = criteriaQuery.from(Employee.class);
        Predicate predicate = cb.like(root.get("name"), "Jo%");
//			criteriaQuery.select(cb.like(root.get("name"), "Jo%"));
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        Query query = em.createQuery(criteriaQuery);
        List<Employee> list = query.getResultList();

        Assertions.assertEquals(2, list.size());

        // not like
        predicate = cb.notLike(root.get("name"), "Jo%");
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        query = em.createQuery(criteriaQuery);
        list = query.getResultList();

        Assertions.assertEquals(1, list.size());

        tx.begin();
        em.remove(employee1);
        em.remove(employee2);
        em.remove(employee3);
        tx.commit();

        em.close();
    }
}
