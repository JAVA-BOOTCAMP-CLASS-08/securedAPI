package com.sicos.secured.repository;

import com.sicos.secured.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByNombre(String nombre);
}
