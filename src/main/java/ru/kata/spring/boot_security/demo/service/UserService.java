package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        if (userRepository.findByUsername(username).isEmpty()) {
            throw new UsernameNotFoundException("Пользователь с таким именем не найден");
        }
        return userRepository.findByUsername(username).get();
    }

    public User getUserById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UsernameNotFoundException("Пользователь с таким ID не найден");
        }
        return userRepository.findById(id).get();
    }

    @Transactional
    public void editUser(User updateUser, Long id) {
        User user_from_DB = userRepository.findById(id).get();
        user_from_DB.setUsername(updateUser.getUsername());
        user_from_DB.setFirstName(updateUser.getFirstName());
        user_from_DB.setLastName(updateUser.getLastName());
        user_from_DB.setEmail(updateUser.getEmail());
        user_from_DB.setRole(updateUser.getRole());

        if (user_from_DB.getPassword().equals(updateUser.getPassword())) {
            userRepository.save(user_from_DB);
        } else {
            user_from_DB.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            userRepository.save(user_from_DB);
        }

        userRepository.save(user_from_DB);
    }

    @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean deleteUserById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
