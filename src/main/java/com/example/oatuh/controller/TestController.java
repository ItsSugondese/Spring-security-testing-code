package com.example.oatuh.controller;

import com.example.oatuh.config.UserDataConfig;
import com.example.oatuh.model.User;
import com.example.oatuh.model.jwt.JwtRequest;
import com.example.oatuh.model.jwt.JwtResponse;
import com.example.oatuh.repo.UserRepo;
import com.example.oatuh.services.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@RestController
@CrossOrigin(value = "http://localhost:4200", maxAge = 3600)
@RequiredArgsConstructor
public class TestController {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;
    private final UserDataConfig userDataConfig;
    private final GoogleIdTokenVerifier tokenVerifier;
    private final String clientId = "746907184110-vm60s3kv0ofk1soqg2l5qptm60t82it0.apps.googleusercontent.com";

    @GetMapping("/oauth-authenticate")
    public Map<String, Object> getCont(OAuth2AuthenticationToken token) throws Exception {

//        SecurityContextHolder.getContext().setAuthentication(null);
        User savedUser = new User();
        String email = (String) token.getPrincipal().getAttribute("email");
        if(!userRepo.existsByEmail(email)) {
            User user = new User();
            user.setEmail((String) token.getPrincipal().getAttribute("email"));
            user.setPassword(encoder.encode((String) token.getPrincipal().getAttribute("sub")));

            savedUser = userRepo.saveAndFlush(user);
        }else {
            savedUser = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User with that email doens't exist"));
        }


        jwtService.createJwtToken(JwtRequest.builder().userEmail(email)
                .userPassword((String) token.getPrincipal().getAttribute("sub")).build());
        return token.getPrincipal().getAttributes();
    }

    @PostMapping("/authenticate")
    public JwtResponse getContdfas(@RequestBody JwtRequest request) throws Exception {
        return  jwtService.createJwtToken(request);
    }

    @GetMapping("/filter")
    public String getStr(){
        return  "Its worked";
    }

    @GetMapping("/userId")
    public Integer getUserId(HttpServletRequest request){
        final String requestTokenHeader = request.getHeader("Authorization").substring(7);

        return userDataConfig.userId(requestTokenHeader);
    }

    @PostMapping("/login-with-google")
    public String loginWithGoogle(@RequestBody String credential) throws Exception {



            GoogleIdToken idToken = tokenVerifier.verify(credential.substring(1, credential.length()-1));

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String userId = payload.getSubject();
                String email = payload.getEmail();
                String password = payload.getSubject();

                if(!userRepo.existsByEmail(email)){
                    User user = new User();
                    user.setPassword(encoder.encode(password));
                    user.setEmail(email);
                    userRepo.save(user);
                }
                // Use the user information to generate a JWT or perform other actions
                JwtResponse response = jwtService.createJwtToken(JwtRequest.builder().userEmail(email).userPassword(password).build());

                return response.getJwtToken();
            } else {
                return "Invalid credential";
            }

    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private class Json{
        String credential;
    }


}
