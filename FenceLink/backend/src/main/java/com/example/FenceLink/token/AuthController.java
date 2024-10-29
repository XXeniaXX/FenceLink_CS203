package com.example.FenceLink.token;

import org.springframework.web.bind.annotation.*;
import com.example.FenceLink.user.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginDTO loginDTO) {
        return userAuthService.loginUser(loginDTO);
    }

    @PostMapping("/validate-token")
    public String validateToken(@RequestBody Map<String, String> requestBody) {
        String jwtToken = requestBody.get("token");

        if (jwtToken == null || jwtToken.isEmpty()) {
            return "Token is missing in the request body";
        }

        // Call the validateToken method and capture the validation message
        String validationMessage = CognitoJWTValidator.validateToken(jwtToken);

        // Return the message to indicate the result of validation
        return validationMessage;
    }
}