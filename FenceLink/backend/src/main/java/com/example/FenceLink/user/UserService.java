package com.example.FenceLink.user;

import java.util.*;

public interface UserService {

    User findById(Long id);
    User findByEmail(String email);
    List<User> findAll();
    User registerUser(UserDTO userDto);
    User updateUser(Long id, UserDTO userDto);
    void deleteUserById(Long id);
}
