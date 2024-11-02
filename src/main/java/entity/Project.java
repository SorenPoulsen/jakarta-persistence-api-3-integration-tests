package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

/**
 * @author Søren Thalbitzer Poulsen
 */
@Entity
public class Project {

    @Id
    int id;

    @NotNull
    String name;

    /*
     * The Employee-Project relation is bidirectional ManyToMany. From both perspective it is a ManyToMany relation.
     * None of them have a join column instead a join table is used, but we must pick one as the owning side, and set
     * a mappedBy property on the non-owning side.
     */
    @ManyToMany(mappedBy = "projects")
    Collection<Employee> employees;
}
