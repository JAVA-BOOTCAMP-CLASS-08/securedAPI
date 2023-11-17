package com.sicos.secured.repository;

import com.sicos.secured.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolRepository extends JpaRepository<Rol, Long> {

    List<Rol> findByNombre(String nombre);
}
