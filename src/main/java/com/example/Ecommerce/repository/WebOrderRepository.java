package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder>findByUser(AppUser appUser);
}
