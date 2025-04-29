package com.lotreetea.backend.repo;

import com.lotreetea.backend.model.User;
import com.lotreetea.backend.model.UserRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);
    List<User> findByLastName(String lastName);
    Optional<User> findByEmail(String email);
    Page<User> findByRole(UserRole role, Pageable pageable); 
}