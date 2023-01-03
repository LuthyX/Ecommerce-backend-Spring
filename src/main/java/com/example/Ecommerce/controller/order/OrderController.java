package com.example.Ecommerce.controller.order;

import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.WebOrder;
import com.example.Ecommerce.service.WebOrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private WebOrderService webOrderService;

    public OrderController(WebOrderService webOrderService) {
        this.webOrderService = webOrderService;
    }

    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal AppUser appUser){
        return webOrderService.getOrders(appUser);
    }
}
