package com.study.userservice.service;

import com.study.userservice.dto.UserDTO;
import com.study.userservice.repository.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();

    UserDTO getUserDetailsByEmail(String userName);
}
