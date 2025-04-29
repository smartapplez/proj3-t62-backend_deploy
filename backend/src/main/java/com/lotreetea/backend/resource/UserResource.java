package com.lotreetea.backend.resource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.lotreetea.backend.model.User;
import com.lotreetea.backend.model.UserRole;
import com.lotreetea.backend.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class UserResource {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        return ResponseEntity.created(URI.create("/users/" + saved.getUserId())).body(saved);
    }

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(userService.getAllUsers(page, size));
    }

    @GetMapping("/role/{role}")
public ResponseEntity<Page<User>> getUsersByRole( // i made role a string so that "ALL" is allowed
        @PathVariable String role,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    if ("ALL".equalsIgnoreCase(role)) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    try {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        return ResponseEntity.ok(userService.getUsersByRole(userRole, page, size));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok().body(userService.getUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> getUsersByLastName(@RequestParam String lastName) {
        List<User> result = userService.getUserByLastName(lastName);
        return ResponseEntity.ok(result);
    }

   // TODO: Add a DELETE mapping and append functionality to UserResource to delete the User from database
   @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        User user = userService.getUser(id);
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }
}
