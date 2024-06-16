package com.project.ecommerce.repository.user;

import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameEquals(String username);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phone);

    boolean existsByEmail(String email);

    @Query(value = "SELECT COUNT(u) FROM User u WHERE u.userRole.roleType = ?1")
    long countAdmin(RoleType roleType);

    User findByName(String name);

    @Query(value = "SELECT u FROM User u WHERE u.name = :name AND u.surname = :lastname")
    List<User> findByNameAndLastName(String name, String lastname);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameContains(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:letters% OR u.surname LIKE %:letters%")
    List<User> findByNameOrLastNameContains(@Param("letters") String letters);

}
