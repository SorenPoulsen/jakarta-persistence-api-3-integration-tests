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
    void testDepartmentOfDwayne() {
        TypedQuery<Department> departmentQuery = em.createQuery("select d from Employee e join e.department d where e.name = :name",
                Department.class).setParameter("name", "Cruse Dwayne");
        Department department = departmentQuery.getSingleResult();
        assertNotNull(department);
        assertEquals("Dwayne Enterprises Leadership", department.getName());
    }

}
