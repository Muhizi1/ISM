package com.Inventory.ims.repository;

import com.Inventory.ims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByDepartment(String department);
    
    List<User> findByActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.active = true AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchUsers(@Param("search") String search);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.active = true")
    long countByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.fullName")
    List<User> findAllActiveUsersOrderedByName();
}
