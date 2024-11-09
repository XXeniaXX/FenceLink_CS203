package com.example.FenceLink.token;

import org.springframework.stereotype.Service;

import com.example.FenceLink.user.UserLoginDTO;


@Service
public class UserAuthService {

    public String loginUser(UserLoginDTO loginDTO) {
        String jwtToken = loginDTO.getJwtToken();
        String validationMessage = CognitoJWTValidator.validateToken(jwtToken);

        if ("Token is valid".equals(validationMessage)) {
            return "Login successful";
        } else {
            return "Login failed: " + validationMessage;
        }
    }
}
