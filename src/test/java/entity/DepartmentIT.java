package entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Søren Thalbitzer Poulsen
 */
public class DepartmentIT {

    static EntityManager em;

    @BeforeAll
    static void setUpBeforeClass() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testunit");
        em = emf.createEntityManager();
    }

    @AfterAll
    static void tearDownAfterClass() {
        em.close();
    }

    @Test
    void testDepartmentExists() {
        Department department = em.find(Department.class, 1);
        assertNotNull(department);
    }

    @Test
    void testDepartmentOfDwayneWithWhereClauseJoin() {
        /*
         * Entities can be joined with the join condition in the where-clause.
         */
        Department department = em.createQuery(
                "select d from Employee e, Department d where e.department = d and e.name = :name",
                Department.class).setParameter("name", "Cruse Dwayne").getSingleResult();
        assertNotNull(department);
        assertEquals("Dwayne Enterprises Leadership", department.getName());
    }

    @Test
    void testDepartmentOfDwayneWithJoinPath() {
        /*
         * Entities can also be joined with a JOIN and a PATH statement (e.department) from an entity to another
         * associated entity. No explicit join condition is needed.
         */
        Department department = em.createQuery(
                "select d from Employee e join e.department d where e.name = :name",
                Department.class).setParameter("name", "Cruse Dwayne").getSingleResult();
        assertNotNull(department);
        assertEquals("Dwayne Enterprises Leadership", department.getName());
    }

    @Test
    public void testEntityAsParameter() {
        Employee employee = em.createQuery("select e from Employee e where id = 1", Employee.class).getSingleResult();
        assertNotNull(employee);
        /*
         * An entity can be used as query parameter, in this case it's the employee entity.
         */
        Department department = em.createQuery("select d from Employee e join e.department d where e = :employee",
                Department.class).setParameter("employee", employee).getSingleResult();
        assertNotNull(department);
    }

}
