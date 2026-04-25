package com.Inventory.ims.controller;

import com.Inventory.ims.model.User;
import com.Inventory.ims.service.UserService;
import com.Inventory.ims.service.AuditTrailService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditTrailService auditTrailService;
    
    @GetMapping
    public String listUsers(Model model, @RequestParam(required = false) String search) {
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search);
            model.addAttribute("search", search);
        } else {
            users = userService.findAllUsers();
        }
        
        model.addAttribute("users", users);
        model.addAttribute("roles", User.UserRole.values());
        return "users/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.UserRole.values());
        return "users/add";
    }
    
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("user") User user, 
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.UserRole.values());
            return "users/add";
        }
        
        try {
            User savedUser = userService.createUser(user);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logUserAction(
                savedUser, 
                "CREATE", 
                "New user created", 
                null, 
                "Username: " + savedUser.getUsername() + ", Role: " + savedUser.getRole(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "User added successfully!");
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", User.UserRole.values());
            return "users/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Don't send password to the form
        user.setPassword("");
        
        model.addAttribute("user", user);
        model.addAttribute("roles", User.UserRole.values());
        return "users/edit";
    }
    
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") User user, 
                            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.UserRole.values());
            return "users/edit";
        }
        
        try {
            // Get old values for audit trail
            User oldUser = userService.findUserById(id).orElse(null);
            
            User updatedUser = userService.updateUser(id, user);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logUserAction(
                updatedUser, 
                "UPDATE", 
                "User updated", 
                oldUser != null ? "Username: " + oldUser.getUsername() + ", Role: " + oldUser.getRole() : null,
                "Username: " + updatedUser.getUsername() + ", Role: " + updatedUser.getRole(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", User.UserRole.values());
            return "users/edit";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            userService.deleteUser(id);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logUserAction(
                user, 
                "DELETE", 
                "User deleted", 
                "Username: " + user.getUsername(),
                null,
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }
    
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        return "users/view";
    }
    
    @GetMapping("/filter")
    public String filterUsers(@RequestParam(required = false) String role,
                            @RequestParam(required = false) String department,
                            Model model) {
        List<User> users;
        
        if (role != null && !role.isEmpty()) {
            users = userService.findUsersByRole(User.UserRole.valueOf(role));
        } else if (department != null && !department.isEmpty()) {
            users = userService.findUsersByDepartment(department);
        } else {
            users = userService.findAllUsers();
        }
        
        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("roles", User.UserRole.values());
        return "users/list";
    }
}
