package entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Søren Thalbitzer Poulsen
 */
public class EmployeeIT {

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
    void testEmployeeById() {
        Employee employee = em.find(Employee.class, 1);
        assertNotNull(employee);
    }

    @Test
    void testEmployeeByName() {
        TypedQuery<Employee> employee = em.createQuery("select e from Employee e where e.name = 'Cruse Dwayne'", Employee.class);
        List<Employee> resultList = employee.getResultList();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("Cruse Dwayne", resultList.get(0).getName());
    }

    @Test
    void testEmployeeByDepartmentName() {
        TypedQuery<Employee> employee = em.createQuery("select e from Employee e join e.department d where d.name = :name",
                Employee.class).setParameter("name", "Dwayne Enterprises Leadership");
        List<Employee> resultList = employee.getResultList();
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
    }
}
