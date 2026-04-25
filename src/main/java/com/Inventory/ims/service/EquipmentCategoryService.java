package com.Inventory.ims.service;

import com.Inventory.ims.model.EquipmentCategory;
import com.Inventory.ims.repository.EquipmentCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipmentCategoryService {
    
    @Autowired
    private EquipmentCategoryRepository categoryRepository;
    
    public List<EquipmentCategory> findAllCategories() {
        return categoryRepository.findAllActiveOrderedByName();
    }
    
    public Optional<EquipmentCategory> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    public Optional<EquipmentCategory> findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    public EquipmentCategory createCategory(EquipmentCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category name already exists: " + category.getName());
        }
        category.setActive(true);
        return categoryRepository.save(category);
    }
    
    public EquipmentCategory updateCategory(Long id, EquipmentCategory categoryDetails) {
        EquipmentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        if (!category.getName().equals(categoryDetails.getName()) && 
            categoryRepository.existsByName(categoryDetails.getName())) {
            throw new RuntimeException("Category name already exists: " + categoryDetails.getName());
        }
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) {
        EquipmentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }
    
    public List<EquipmentCategory> searchCategories(String keyword) {
        return categoryRepository.searchCategories(keyword);
    }
    
    public long getTotalCategoriesCount() {
        return categoryRepository.count();
    }
}
