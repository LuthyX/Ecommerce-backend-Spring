package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.PasswordResetToken;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends ListCrudRepository<PasswordResetToken, Long> {

    void deleteByUser(AppUser user);

}
