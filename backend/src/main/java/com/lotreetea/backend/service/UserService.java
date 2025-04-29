package com.lotreetea.backend.service;

import com.lotreetea.backend.model.User;
import com.lotreetea.backend.model.UserRole;
import com.lotreetea.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j //logging
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo; //DAO

    public Page<User> getAllUsers(int page, int size) {
        return userRepo.findAll(PageRequest.of(page, size, Sort.by("lastName")));
        
    }

    public User getUser(Integer id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found by id"));
    }

    public List<User> getUserByLastName(String lastName) {
        return userRepo.findByLastName(lastName);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepo.findByEmail(email);
    }

    public Page<User> getUsersByRole(UserRole role, int page, int size) {
        return userRepo.findByRole(role, PageRequest.of(page, size));
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        User existing = getUser(id); 
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setEmail(updatedUser.getEmail());
        existing.setRole(updatedUser.getRole());
        existing.setSource(updatedUser.getSource());
    
        return userRepo.save(existing);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }
}
