package com.example.Ecommerce.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.repository.AppUserRepository;
import com.example.Ecommerce.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFIlter extends OncePerRequestFilter {

    private JWTService jwtService;
    private AppUserRepository appUserRepository;

    public JWTRequestFIlter(JWTService jwtService, AppUserRepository appUserRepository) {
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            String token  = tokenHeader.substring(7);
            try{String username = jwtService.getUsername(token);
                Optional<AppUser> opapp = appUserRepository.findByUsername(username);
                if(
                        opapp.isPresent()
                ){
                    AppUser appUser = opapp.get();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(appUser, null, new ArrayList<>());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            catch (JWTDecodeException ex){

            }

        }
        filterChain.doFilter(request, response);
    }
}
