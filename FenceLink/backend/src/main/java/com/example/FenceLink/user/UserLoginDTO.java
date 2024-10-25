package com.example.FenceLink.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDTO {

    @NotNull(message = "Email should not be null")
    private String email;

    @NotNull(message = "Password should not be null")
    private String password;

    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
}
