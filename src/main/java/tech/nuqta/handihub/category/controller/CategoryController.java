package tech.nuqta.handihub.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.nuqta.handihub.category.dto.CategoryDto;
import tech.nuqta.handihub.category.dto.request.CategoryCreateRequest;
import tech.nuqta.handihub.category.dto.request.CategoryUpdateRequest;
import tech.nuqta.handihub.category.service.CategoryService;
import tech.nuqta.handihub.common.ResponseMessage;

import java.util.List;

/**
 * The CategoryController class is responsible for handling HTTP requests related to categories.
 * It provides RESTful API endpoints for creating, updating, deleting, and retrieving category information.
 * <p>
 * This class requires an instance of the CategoryService interface for performing category-related operations.
 * <p>
 * The CategoryController class has the following API endpoints:
 * - POST /api/v1/categories/create: Creates a new category.
 * - POST /api/v1/categories/create-sub-category: Creates a new sub-category.
 * - PUT /api/v1/categories/update: Updates an existing category.
 * - DELETE /api/v1/categories/delete: Deletes a category by its ID.
 * - GET /api/v1/categories/get: Retrieves a category by its ID.
 * - GET /api/v1/categories/get-all: Retrieves all categories.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PostMapping("/create-sub-category")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> createSubCategory(@RequestBody @Valid CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.createSubCategory(request));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> updateCategory(@RequestBody @Valid CategoryUpdateRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(request));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> deleteCategory(@RequestParam Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }

    @GetMapping("/get")
    public ResponseEntity<CategoryDto> getCategory(@RequestParam Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }
}
