package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username_from_webpage) throws UsernameNotFoundException {
        User user_from_DB = userRepository.getUserByUsername(username_from_webpage);
        if (user_from_DB == null) {
            throw new UsernameNotFoundException("Пользователь с таким именем не найден.");
        }
//        return user_from_DB;
        return new org.springframework.security.core.userdetails.User(
                user_from_DB.getUsername(),
                user_from_DB.getPassword(),
                mapRolesToAuthorities(user_from_DB.getRole()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getRole())).collect(Collectors.toList());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        if (userRepository.getUserByUsername(username) == null) {
            throw new UsernameNotFoundException("Пользователь с таким именем не найден");
        }
        return userRepository.getUserByUsername(username);
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

    @Transactional
    public boolean deleteUserById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
