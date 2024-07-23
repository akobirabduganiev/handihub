package tech.nuqta.handihub.category.service;

import tech.nuqta.handihub.category.dto.CategoryDto;
import tech.nuqta.handihub.category.dto.request.CategoryCreateRequest;
import tech.nuqta.handihub.category.dto.request.CategoryUpdateRequest;
import tech.nuqta.handihub.common.ResponseMessage;

import java.util.List;

public interface CategoryService {
    ResponseMessage createCategory(CategoryCreateRequest request);
    ResponseMessage createSubCategory(CategoryCreateRequest request);
    CategoryDto getCategory(Long id);
    List<CategoryDto> getCategories();
    ResponseMessage updateCategory(CategoryUpdateRequest request);
    ResponseMessage deleteCategory(Long id);
}
