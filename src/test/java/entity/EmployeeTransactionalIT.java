package entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Begins a new transaction before each test with @BeforeEach, and rolls it back after each test with @AfterEach, to
 * isolate test from each other.
 *
 * @author Søren Thalbitzer Poulsen
 */
public class EmployeeTransactionalIT {

    static EntityManager em;

    @BeforeAll
    static void setUpBeforeClass() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testunit");
        em = emf.createEntityManager();
    }

    @BeforeEach
    void setUp() {
        em.getTransaction().begin();
    }

    @AfterAll
    static void tearDownAfterClass() {
        em.close();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().rollback();
        em.clear();
    }

    @Test
    public void testPersistNewEmployeeWithDepartment() {
        /*
         * Persist a new Employee in the existing Department 'Dwayne Enterprises R&D'
         */

        Employee employee = new Employee("Celina Lyle");
        Department department = em.createQuery("select d from Department d where d.name = 'Dwayne Enterprises R&D'",
                Department.class).getSingleResult();
        employee.setDepartment(department);
        em.persist(employee);

        /*
         * Test that the new Employee is in the list of Employees of the Department 'Dwayne Enterprises R&D'.
         */

        List<Employee> employees = em.createQuery(
                "select e from Department d join d.employees e where d.name = 'Dwayne Enterprises R&D'",
                Employee.class).getResultList();
        assertNotNull(employees);
        assertTrue(employees.contains(employee));
    }

    @Test
    public void testNumberOfEmployeesInDepartment() {

        Object numberOfEmployees = em.createQuery(
                "select count(e) from Department d join d.employees e where d.name = 'Dwayne Enterprises R&D'").getSingleResult();

        assertNotNull(numberOfEmployees);
        assertEquals(1, (Long)numberOfEmployees);
    }
}