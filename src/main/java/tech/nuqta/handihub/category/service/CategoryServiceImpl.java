package tech.nuqta.handihub.category.service;

import lombok.RequiredArgsConstructor;
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
    public ResponseMessage createCategory(CategoryCreateRequest request) {
        categoryRepository.findByName(request.getName())
                .ifPresent(categoryEntity -> {
                    throw new AppBadRequestException("Category with name " + request.getName() + " already exists");
                });
        var entity = new CategoryEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        categoryRepository.save(entity);
        return new ResponseMessage("Category created successfully");
    }

    @Override
    public ResponseMessage createSubCategory(CategoryCreateRequest request) {
        var parentCategory = categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new AppBadRequestException("Parent category not found"));
        categoryRepository.findByName(request.getName())
                .ifPresent(categoryEntity -> {
                    throw new AppBadRequestException("Category with name " + request.getName() + " already exists");
                });
        var entity = new CategoryEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setParentCategory(parentCategory);
        categoryRepository.save(entity);
        return new ResponseMessage("Sub category created successfully");
    }

    @Override
    public CategoryDto getCategory(Long id) {
        var entity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppBadRequestException("Category not found"));
        return CategoryMapper.toDto(entity);
    }

    public List<CategoryDto> getCategories() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        List<CategoryEntity> entities = categoryRepository.findByParentCategoryIsNull(sort);
        return entities.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseMessage updateCategory(CategoryUpdateRequest request) {
        var entity = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new AppBadRequestException("Category not found"));
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setParentCategory(request.getParentCategoryId() != null ?
                categoryRepository.findById(request.getParentCategoryId())
                        .orElseThrow(() -> new AppBadRequestException("Parent category not found")) : null);
        categoryRepository.save(entity);
        return new ResponseMessage("Category updated successfully");
    }
}
