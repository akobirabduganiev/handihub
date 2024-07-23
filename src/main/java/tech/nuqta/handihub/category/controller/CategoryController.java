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
