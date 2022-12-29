package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

public interface VerificationTokenRepostiory extends ListCrudRepository<VerificationToken, Long> {
}
