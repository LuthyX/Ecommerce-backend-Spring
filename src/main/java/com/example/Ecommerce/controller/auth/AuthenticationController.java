package com.example.Ecommerce.controller.auth;

import com.example.Ecommerce.api.model.LoginBody;
import com.example.Ecommerce.api.model.LoginResponse;
import com.example.Ecommerce.api.model.RegistrationBody;
import com.example.Ecommerce.exception.UserAlreadyExistsException;
import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private AppUserService appUserService;

    public AuthenticationController(AppUserService appUserService){
        this.appUserService = appUserService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody  RegistrationBody registrationBody){
        try{appUserService.registerUser(registrationBody);
        return ResponseEntity.ok().build();
        }
        catch (UserAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginBody loginBody){
       String jwt = appUserService.loginUser(loginBody);
        if (jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else{
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setUsername(loginBody.getUsername());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/me")
    public AppUser getLoggedInUserProfile(@AuthenticationPrincipal AppUser appUser){
        return appUser;
    }
}
