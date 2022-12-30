package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {


    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    @Modifying
    @Query("update AppUser set role = :role where username = :username")
    void updateUserRole(@Param("username")String username, @Param("role") Role role);
}
