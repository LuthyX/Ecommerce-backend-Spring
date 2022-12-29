package com.example.Ecommerce.api.model;

import lombok.Getter;

@Getter
public class RegistrationBody {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
