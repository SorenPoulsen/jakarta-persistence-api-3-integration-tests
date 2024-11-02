package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

/**
 * @author Søren Thalbitzer Poulsen
 */
@Entity
public class Department {

    @Id
    int id;

    @NotNull
    String name;

    /*
     * The Department-Employee relation is bidirectional. The Many side is the owning side and has the join column.
     * The One side is the non-owning (inverse) side. The non-owning side must declare what field on the owning side it
     * is mapped by. From the perspective of the owning side it's a ManyToOne relation, but for the non-owning's
     * perspective it's a OneToMany relation.
     */
    @OneToMany(mappedBy = "department")
    Collection<Employee> employees;

    public String getName() {
        return name;
    }

}
