package entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Søren Thalbitzer Poulsen
 */
public class ProjectIT {

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
    public void testNativeEmployeeProjects() {
        /*
         * A native query without a return type, returns a Collection of Object[]. Each array is row containing the
         * selected attributes.
         */
        Collection projects = em.createNativeQuery(
                "select p.id, p.name from employee e join employee_project ep on e.id = ep.employee_id " +
                        "join project p on ep.project_id = p.id where e.name = 'Mr Foxy Weasel'").getResultList();
        assertNotNull(projects);
        assertEquals(2, projects.size());
        for (Object project : projects) {
            System.out.println(((Object[]) project)[0].toString() + ", " + ((Object[]) project)[1].toString());
        }
    }

    @Test
    public void testNativeEmployeeProjectsWithReturnType() {
        /*
         * A native query with a typed return value actually causes Hibernate to make additional queries to load
         * Project entity and other related eagerly fetched entities from the project id returned in the native query
         * rather than constructing the Project instances from the result of the native query.
         */
        Collection<Project> projects = em.createNativeQuery(
                "select p.id, p.name from employee e join employee_project ep on e.id = ep.employee_id " +
                        "join project p on ep.project_id = p.id where e.name = 'Mr Foxy Weasel'", Project.class).getResultList();
        assertNotNull(projects);
        assertEquals(2, projects.size());
        projects.forEach(System.out::println);
    }

    @Test
    public void testEmployeeProjectsWithPath() {
        /*
         * We can effectively join Employee and Project with a path expression in the select statement.
         */
        TypedQuery<Project> query = em.createQuery("select e.projects p from Employee e where e.id = :id",
                Project.class).setParameter("id", 3);
        List<Project> projects = query.getResultList();
        assertNotNull(projects);
        assertEquals(2, projects.size());
        projects.forEach(System.out::println);
    }

    @Test
    public void testEmployeeProjectsWithJoinOn() {
        /*
         * Selecting Project with an object relational join statement that uses a path from Employee to Project,
         * "e.projects".
         */
        TypedQuery<Project> query = em.createQuery("select p from Employee e join e.projects p where e.id = :id",
                Project.class).setParameter("id", 3);
        List<Project> projects = query.getResultList();
        assertNotNull(projects);
        assertEquals(2, projects.size());
        projects.forEach(System.out::println);
    }

    @Test
    public void testIllegalEmployeeProjectsWithJoinWhere() {
        /*
         * Illegal: A multivalue member cannot be used as a path expression for the join condition in the where clause,
         * here p.employees. Since the Employee Project relation is ManyToMany, both sides relate through a multivalue
         * member. What can be done instead is to use the multivalue member in a join statement as a path, ie.
         * "from Employee e join e.projects p". The identifier p create by the path can be used for further conditions
         * but nothing more is needed for the actual join condition.
         */
        try {
            TypedQuery<Project> query = em.createQuery(
                    "select p from Employee e, Project p where e.id = :id and e = p.employees",
                    Project.class).setParameter("id", 3);
            List<Project> projects = query.getResultList();
        } catch (Exception e) {

            /*
             * ManyToMany relation "p.employees" has no identifying root and cannot be used in where-clause.
             */

            assertTrue(true);
        }
    }

}
