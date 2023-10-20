package com.example.oatuh.services;

import com.example.oatuh.config.CustomUserDetailsService;
import com.example.oatuh.config.JwtUtil;
import com.example.oatuh.model.User;
import com.example.oatuh.model.jwt.JwtRequest;
import com.example.oatuh.model.jwt.JwtResponse;
import com.example.oatuh.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class JwtService {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private AuthenticationManager authenticationManager;


    public JwtResponse createJwtToken(JwtRequest jwtRequest) throws Exception {
        String userEmail = jwtRequest.getUserEmail();
        String userPassword = jwtRequest.getUserPassword();

        authenticate(userEmail, userPassword);

        Map<String, Object> map = userDetailsService.customLoadUserByUsername(userEmail);

        UserDetails userDetails = (UserDetails) map.get("userDetails");
        String newGeneratedToken = jwtUtil.generateToken(userDetails, (User) map.get("user"));

        User user = userRepo.findByEmail(userEmail).get();

        JwtResponse response = new JwtResponse();
        response.setUser(user);
        response.setJwtToken(newGeneratedToken);

        return response;
    }

    private void authenticate(String userEmail, String userPassword) throws Exception{
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userEmail, userPassword));
        }catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            System.out.println(e);
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
