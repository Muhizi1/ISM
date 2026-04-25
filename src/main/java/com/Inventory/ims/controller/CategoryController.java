package com.Inventory.ims.controller;

import com.Inventory.ims.model.EquipmentCategory;
import com.Inventory.ims.service.EquipmentCategoryService;
import com.Inventory.ims.service.AuditTrailService;
import com.Inventory.ims.model.User;
import com.Inventory.ims.service.UserService;
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
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private EquipmentCategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditTrailService auditTrailService;
    
    @GetMapping
    public String listCategories(Model model, @RequestParam(required = false) String search) {
        List<EquipmentCategory> categories;
        if (search != null && !search.trim().isEmpty()) {
            categories = categoryService.searchCategories(search);
            model.addAttribute("search", search);
        } else {
            categories = categoryService.findAllCategories();
        }
        
        model.addAttribute("categories", categories);
        return "categories/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new EquipmentCategory());
        return "categories/add";
    }
    
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") EquipmentCategory category,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/add";
        }
        
        try {
            EquipmentCategory savedCategory = categoryService.createCategory(category);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logUserAction(
                null, 
                "CATEGORY_CREATE", 
                "New category created: " + savedCategory.getName(),
                null,
                "Category: " + savedCategory.getName() + ", Description: " + savedCategory.getDescription(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Category added successfully!");
            return "redirect:/categories";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "categories/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        EquipmentCategory category = categoryService.findCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        model.addAttribute("category", category);
        return "categories/edit";
    }
    
    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id, @Valid @ModelAttribute("category") EquipmentCategory category,
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/edit";
        }
        
        try {
            // Get old values for audit trail
            EquipmentCategory oldCategory = categoryService.findCategoryById(id).orElse(null);
            
            EquipmentCategory updatedCategory = categoryService.updateCategory(id, category);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logUserAction(
                null, 
                "CATEGORY_UPDATE", 
                "Category updated: " + updatedCategory.getName(),
                oldCategory != null ? "Name: " + oldCategory.getName() + ", Description: " + oldCategory.getDescription() : null,
                "Name: " + updatedCategory.getName() + ", Description: " + updatedCategory.getDescription(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Category updated successfully!");
            return "redirect:/categories";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "categories/edit";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            EquipmentCategory category = categoryService.findCategoryById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            
            categoryService.deleteCategory(id);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logUserAction(
                null, 
                "CATEGORY_DELETE", 
                "Category deleted: " + category.getName(),
                "Category: " + category.getName() + ", Description: " + category.getDescription(),
                null,
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
    
    @GetMapping("/view/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {
        EquipmentCategory category = categoryService.findCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        model.addAttribute("category", category);
        return "categories/view";
    }
}
