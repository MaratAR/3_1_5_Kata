package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String showAllUsers(@ModelAttribute ("user") User user, Principal principal, Model model) {
        User authenticatedUser = userService.getUserByUsername(principal.getName());
        model.addAttribute ("authenticatedUser", authenticatedUser);
        model.addAttribute ("roleOfAuthenticatedUser", authenticatedUser.getRole());
        model.addAttribute("users", userService.findAll());
        model.addAttribute( "AllRoles", roleService.findAll());
        return "admin-page";
    }

    @GetMapping("/user-profile/{id}")
    public String findUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("AllRoles", user.getRole());
        return "user-page";
    }

    @GetMapping("/edit/{id}")
    public String editUser(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("AllRoles", roleService.findAll());
        return "redirect:/admin";
    }

    @PatchMapping("/update/{id}")
    public String updateUser(@ModelAttribute("user") User updateUser, @PathVariable("id") Long id) {
        userService.editUser(updateUser, id);
        return "redirect:/admin";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/new")
    public String form_for_create_user(Model model, User user) {
        model.addAttribute("user", new User());
        List<Role> roles = roleService.findAll();
        model.addAttribute("AllRoles", roles);
        return "redirect:/admin";
    }

    @PostMapping("/create")
    public String addUser(@ModelAttribute("user") User user) {
        userService.addUser(user);
        return "redirect:/admin";
    }

}
