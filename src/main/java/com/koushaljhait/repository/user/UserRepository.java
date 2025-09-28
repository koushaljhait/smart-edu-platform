package com.koushaljhait.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.koushaljhait.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if username exists
    Boolean existsByUsername(String username);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find users by role
    List<User> findByRole(String role);
    
    // Find enabled users
    List<User> findByEnabledTrue();
    
    // Find disabled users
    List<User> findByEnabledFalse();
    
    // Find users by role and enabled status
    List<User> findByRoleAndEnabledTrue(String role);
    
    // Custom query: Find users created after a certain date
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);
    
    // Custom query: Count users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") String role);
    
    // Simple name search using method naming
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    List<User> findByLastNameContainingIgnoreCase(String lastName);
    
    // FIXED: Find maximum username number using a native query
    @Query(value = "SELECT MAX(CAST(username AS BIGINT)) FROM users WHERE username ~ '^[0-9]{10}$'", nativeQuery = true)
    Long findMaxUsernameNumber();
    
    // Alternative method: Find all numeric usernames and process in service
    @Query("SELECT u.username FROM User u WHERE LENGTH(u.username) = 10")
    List<String> findAll10DigitUsernames();
}