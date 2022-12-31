package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface VerificationTokenRepostiory extends ListCrudRepository<VerificationToken, Long> {


    Optional<VerificationToken> findByToken(String Token);

    void deleteByUser(AppUser user);


    List<VerificationToken> findByUser_IdOrderByIdDesc(Long id);
}
