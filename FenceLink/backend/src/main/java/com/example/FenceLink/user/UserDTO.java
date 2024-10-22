package com.example.FenceLink.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    @NotNull(message = "Username should not be null")
    private String username;

    @NotNull(message = "Email should not be null")
    private String email;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    public UserDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);  
        return user;
    }
}
