package entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @AfterEach
    public void afterEach() {
        em.clear();
    }

    @Test
    void testEmployeeById() {
        Employee employee = em.find(Employee.class, 1);
        assertNotNull(employee);
    }

    @Test
    public void testEmployeeByIdQuery() {
        TypedQuery<Employee> query = em.createQuery("select e from Employee e where e.id = :id",
                Employee.class).setParameter("id", 3);
        Employee employee = query.getSingleResult();
        assertNotNull(employee);
        assertEquals("Mr Foxy Weasel", employee.getName());
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

    @Test
    void testEmployeeByNameNamedQuery() {
        Integer id = em.createNamedQuery("Employee.findIdByName", Integer.class)
                .setParameter("name", "Cruse Dwayne").getSingleResult();
        assertNotNull(id);
        assertEquals(1, id);
    }

    @Test
    void testEmployeeByIdCriteriaAPI() {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        /*
         * The CriteriaQuery models a query around one or more Root entities. Most everything else is a path from
         * the root entities. The objects added to the CriteriaQuery are immutable, to support chained invocation
         * without having to revisit previous nodes.
         */

        CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);

        /*
         * The CriteriaQuery itself is mutable. Methods select(), from(), where() can be invoked multiple times.
         * Invoking select() again override the previous select for reuse of the model.
         * Invoking from() again adds a new root to the existing ones, creating a cartesian product unless they are
         * joined by where-conditions.
         */

        Root<Employee> root = criteriaQuery.from(Employee.class);

        /*
         * Path expressions are created with get() methods. Here the path "Employee.id" is created with root.get("id").
         */

        criteriaQuery.select(root).where(cb.equal(root.get("id"), 1));

        /*
         * To execute the query we feed the CriteriaQuery into the EntityManagers.createQuery() method, and
         * typically invoke getSingleResult() or getResultList(). Observe the confusion that the CriteriaQuery model
         * was created by a method named createQuery() on the CriteriaBuilder, and later it is fed into different,
         * but similarly named, createQuery() on the EntityManager to produce the TypedQuery that can be executed.
         */

        Employee employee = em.createQuery(criteriaQuery).getSingleResult();
        assertNotNull(employee);
        assertEquals("Cruse Dwayne", employee.getName());
    }

    @Test
    public void testEmployeeByDepartmentExplicitCriteriaAPI() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
        Root<Employee> empRoot = criteriaQuery.from(Employee.class);
        Root<Department> depRoot = criteriaQuery.from(Department.class);

        /*
         * One style of modelling the query is to generate Paths and Predicates individually, then combine them
         * together with CriteriaBuilder methods such as equals() and and(), and then finally assemble the whole query
         * with CriteriaQuery's select() and where() methods.
         */

        Path<String> depNamePath = depRoot.get("name");
        Predicate depNamePredicate = cb.equal(depNamePath, "Dwayne Enterprises Leadership");

        Path<Department> emptToDepPath = empRoot.get("department");
        Predicate empDepJoinConditionPredicate = cb.equal(depRoot, emptToDepPath);

        Predicate joinConditionAndDepNamePredicate = cb.and(empDepJoinConditionPredicate, depNamePredicate);

        criteriaQuery.select(empRoot).where(joinConditionAndDepNamePredicate);

        List<Employee> resultList = em.createQuery(criteriaQuery).getResultList();
        assertNotNull(resultList);
        assertEquals(2, resultList.size());

    }

    @Test
    public void testEmployeeByDepartmentChainedCriteriaAPI() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
        Root<Employee> empRoot = criteriaQuery.from(Employee.class);
        Root<Department> depRoot = criteriaQuery.from(Department.class);

        /*
         * Another style of building the query model is to chain the Path and Predicate expressions together. This is
         * more readable than producing a lot of intermediate Paths and Predicates.
         */

        criteriaQuery.select(empRoot).where(
                cb.and(
                    cb.equal(depRoot, empRoot.get("department")),
                    cb.equal(depRoot.get("name"), "Dwayne Enterprises Leadership")));

        List<Employee> resultList = em.createQuery(criteriaQuery).getResultList();
        assertNotNull(resultList);
        assertEquals(2, resultList.size());

    }

    @Test
    public void testEmployeeByDepartmentJoinCriteriaAPI() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
        Root<Employee> empRoot = criteriaQuery.from(Employee.class);

        /*
         * Joining the Root with a related entity voids the need for an explicit join condition in the where-clause.
         */

        Join<Employee, Department> department = empRoot.join("department");

        /*
         * A Join is also a Path that can be chained with get() methods to produce new Paths. So department.get("name")
         * is the "employee.department.name" Path.
         */

        criteriaQuery.select(empRoot).where(cb.equal(department.get("name"), "Dwayne Enterprises Leadership"));

        List<Employee> resultList = em.createQuery(criteriaQuery).getResultList();
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
    }

    @Test
    public void testEmployeeByDepartmentWithParamCriteriaAPI() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
        Root<Employee> empRoot = criteriaQuery.from(Employee.class);
        Join<Employee, Department> department = empRoot.join("department");

        /*
         * Predicates can be created with Parameters to make them more dynamic.
         */

        criteriaQuery.select(empRoot).where(cb.equal(department.get("name"), cb.parameter(String.class, "depname")));
        TypedQuery<Employee> typedQuery = em.createQuery(criteriaQuery);

        /*
         * The actual parameter value is only bound on the executable TypedQuery, making the CriteriaQuery more
         * reusable.
         */

        typedQuery.setParameter("depname", "Dwayne Enterprises Leadership");

        List<Employee> resultList = typedQuery.getResultList();
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
    }

    @Test
    public void testPersistEmployeeNoTransaction() {
        Employee emp = new Employee("Celina Lyle");

        /*
         * We are running a standalone JavaSE application, thus there are no container-managed transactions, and if we
         * don't set up an explicit "resource local" transaction either, then the entity change is not persisted.
         */

        try {
            em.persist(emp);
            Object persistedEmp = em.createQuery("select e from Employee e where e.name = 'Celina Lyle'").getSingleResult();
        }
        catch (NoResultException e) {
            assertTrue(true);
        }
        finally {
            em.remove(emp);
        }
    }

    @Test
    public void testPersistEmployeeWithResourceLocalTransaction() {

        /*
         * To persist new entities or save changes to entities when running in a standalone JavaSE application
         * with "resource local" transactions, we have to begin and end the transactions explicitly.
         */

        Employee emp = new Employee("Celina Lyle");
        Employee reloadedEmp = null;
        try {
            em.getTransaction().begin();
            em.persist(emp);
            em.getTransaction().commit();

            /*
             * Test if the entity has been persisted to the database by clearing the entity manager. This will detach
             * the emp entity, and force the entity manager to reload it on the next query.
             */

            em.clear();
            reloadedEmp = em.createQuery("select e from Employee e where e.name = 'Celina Lyle'", Employee.class).getSingleResult();
            assertNotNull(reloadedEmp);
        }
        finally {
            em.getTransaction().begin();
            em.remove(reloadedEmp);
            em.getTransaction().commit();
        }
    }

    @Test
    public void testPersistNewEmployeeWithDepartment() {

        try {
            em.getTransaction().begin();

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
        finally {
            em.getTransaction().rollback();
        }

    }

}
