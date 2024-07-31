package tech.nuqta.handihub.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.category.dto.CategoryDto;
import tech.nuqta.handihub.category.dto.request.CategoryCreateRequest;
import tech.nuqta.handihub.category.dto.request.CategoryUpdateRequest;
import tech.nuqta.handihub.category.entity.CategoryEntity;
import tech.nuqta.handihub.category.repository.CategoryRepository;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.exception.AppBadRequestException;
import tech.nuqta.handihub.mapper.CategoryMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseMessage createCategory(CategoryCreateRequest request) {
        checkIfCategoryNameExists(request.getName());
        createAndSaveEntity(request, null);
        return new ResponseMessage("Category created successfully");
    }
    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseMessage createSubCategory(CategoryCreateRequest request) {
        var parentCategory = categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new AppBadRequestException("Parent category not found"));
        checkIfCategoryNameExists(request.getName());
        createAndSaveEntity(request, parentCategory);
        return new ResponseMessage("Sub category created successfully");
    }

    @Override
    @Cacheable("categories")
    public CategoryDto getCategory(Long id) {
        var entity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppBadRequestException("Category not found"));
        return CategoryMapper.toDto(entity);
    }

    @Override
    @Cacheable("categories")
    public List<CategoryDto> getCategories() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        List<CategoryEntity> entities = categoryRepository.findByParentCategoryIsNull(sort);
        return entities.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseMessage updateCategory(CategoryUpdateRequest request) {
        var entity = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new AppBadRequestException("Category not found"));
        checkIfCategoryNameExists(request.getName());

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setParentCategory(request.getParentCategoryId() != null ?
                categoryRepository.findById(request.getParentCategoryId())
                        .orElseThrow(() -> new AppBadRequestException("Parent category not found")) : null);
        categoryRepository.save(entity);
        return new ResponseMessage("Category updated successfully");
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseMessage deleteCategory(Long id) {
        var entity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppBadRequestException("Category not found"));
        entity.setIsDeleted(true);
        categoryRepository.save(entity);
        return new ResponseMessage("Category deleted successfully");
    }

    private void checkIfCategoryNameExists(String categoryName){
        categoryRepository.findByName(categoryName)
                .ifPresent(categoryEntity -> {
                    throw new AppBadRequestException("Category with name " + categoryName + " already exists");
                });
    }

    private void createAndSaveEntity(CategoryCreateRequest request, CategoryEntity parentCategory){
        var entity = new CategoryEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setParentCategory(parentCategory);
        categoryRepository.save(entity);
    }
}
