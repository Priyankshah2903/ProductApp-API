package com.priyank.api.services;





import com.priyank.api.payload.*;
import com.priyank.api.respositery.UserRepository;
import com.priyank.api.security.AuthenticationFacade;
import com.priyank.api.entites.*;
import com.priyank.api.exceptions.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationFacade authenticationFacade; 

    @Autowired
    private ModelMapper modelMapper; 

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return (user != null) ? modelMapper.map(user, UserDTO.class) : null;
    }

    public UserDTO registerUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User registeredUser = userRepository.save(user);
        return modelMapper.map(registeredUser, UserDTO.class);
    }

    
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            modelMapper.map(userDTO, existingUser);
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            User updatedUser = userRepository.save(existingUser);
            return modelMapper.map(updatedUser, UserDTO.class);
        } else {
            throw new CustomException("User not found with id " + id);
        }
    }
    public UserDTO createUserByAdmin(UserDTO userDTO) {
        if (authenticationFacade.isAdminUser()) {
            User user = modelMapper.map(userDTO, User.class);
           
            User createdUser = userRepository.save(user);
            return modelMapper.map(createdUser, UserDTO.class);
        } else {
            throw new CustomException("Only admin users are allowed to create users.");
        }
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
