package com.example.oatuh.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.oatuh.model.User;
import com.example.oatuh.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class CustomUserDetailsService implements UserDetailsService{



    @Autowired
    private UserRepo userRepo;


    public Map<String, Object> customLoadUserByUsername(String email){
        Map<String, Object> map = new HashMap<>();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User with email doesn't exist"));

        map.put("userDetails", loadUserByUsername(email));
        map.put("user", user);

        return map;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email).get();

        if(user != null) {
            return  new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    getAuthority(user)
            );
        }else {
            throw new UsernameNotFoundException("User with that email '" + email + "' doesn't exist");
        }
    }

    private Set getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet();
        user.getRole().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
        });

        return authorities;
    }




}
