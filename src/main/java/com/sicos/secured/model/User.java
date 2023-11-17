package com.sicos.secured.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @EqualsAndHashCode.Include()
    @Schema(name = "id", description = "ID de Usuario", example = "1")
    private Long id;

    @Schema(name = "nombre", description = "Nombrew de Usuario", example = "User1")
    private String nombre;

    @Schema(name = "password", description = "Password de Usuario", example = "1234")
    private String password;

    @Schema(name = "enabled", description = "Status del usuario", example = "Y", anyOf = {Status.class})
    private String enabled;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "user_rol_rel",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    public void addRol(Rol rol) {
        roles.add(rol);
        rol.getUsers().add(this);
    }

    public void removeRol(Rol rol) {
        roles.remove(rol);
        rol.getUsers().remove(this);
    }

    @JsonIgnore
    public boolean isHabilitado() {
        return Optional.ofNullable(enabled)
                .filter(e -> !e.isEmpty())
                .map(e -> e.equalsIgnoreCase("Y"))
                .orElse(false);
    }
}
