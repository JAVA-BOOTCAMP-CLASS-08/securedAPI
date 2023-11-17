package com.sicos.secured.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @EqualsAndHashCode.Include()
    private Long id;

    private String nombre;

    private String enabled;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @JsonIgnore
    public boolean isHabilitado() {
        return Optional.ofNullable(enabled)
                .filter(e -> !e.isEmpty())
                .map(e -> e.equalsIgnoreCase("Y"))
                .orElse(false);
    }
}
