package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

/**
 * @author Søren Thalbitzer Poulsen
 */
@Entity
public class Employee {

    @Id
    int id;

    @NotNull
    String name;

    /*
     * The Department-Employee relation is bidirectional. The Many side is the owning side and has the join column.
     * The One side is the non-owning (inverse) side. The non-owning side must declare what field on the owning side it
     * is mapped by. From the perspective of the owning side it's a ManyToOne relation, but for the non-owning's
     * perspective it's a OneToMany relation.
     * If the JoinColumn is not set explicitly on the owning side, then it is generated.
     */
    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;

    /*
     * The Employee-Project relation is bidirectional ManyToMany. From both perspective it is a ManyTOMany relation.
     * None of them have a join column instead a join table is used, but we must pick one as the owning side, and set
     * a mappedBy property on the non-owning side.
     * A join table can be declared on the owning side, if not then the table is generated.
     */
    @ManyToMany
    @JoinTable(name = "employee_project", joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    Collection<Project> projects;

    public Employee() {
    }

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }
}
