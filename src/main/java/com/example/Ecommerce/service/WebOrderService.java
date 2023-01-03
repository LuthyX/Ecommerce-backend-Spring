package com.example.Ecommerce.service;

import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.WebOrder;
import com.example.Ecommerce.repository.WebOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebOrderService {
    private WebOrderRepository webOrderRepository;

    public WebOrderService(WebOrderRepository webOrderRepository) {
        this.webOrderRepository = webOrderRepository;
    }

    public List<WebOrder> getOrders(AppUser appUser){
        return webOrderRepository.findByUser(appUser);
    }

}
