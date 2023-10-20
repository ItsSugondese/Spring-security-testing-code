package com.example.oatuh.model.jwt;

import com.example.oatuh.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtResponse {

    private User user;
    private String jwtToken;
}
