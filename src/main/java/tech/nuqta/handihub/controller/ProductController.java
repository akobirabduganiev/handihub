package tech.nuqta.handihub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.file.ImageUploadService;
import tech.nuqta.handihub.dto.ProductDTO;
import tech.nuqta.handihub.product.dto.request.ProductCreateRequest;
import tech.nuqta.handihub.product.dto.request.ProductUpdateRequest;
import tech.nuqta.handihub.product.service.ProductsService;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductsService productsService;
    private final ImageUploadService imageUploadService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ResponseEntity<ResponseMessage> addProduct(@RequestBody @Valid ProductCreateRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(productsService.addProduct(request, connectedUser));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ResponseEntity<ResponseMessage> updateProduct(@RequestBody @Valid ProductUpdateRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(productsService.updateProduct(request, connectedUser));
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ResponseEntity<ResponseMessage> addImages(
            @PathVariable Long id,
            @RequestParam("images") MultipartFile[] images,
            Authentication authentication) {
        return ResponseEntity.ok(productsService.addImages(id, images, authentication));
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = imageUploadService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<ResponseMessage> deleteProduct(@RequestParam Long id, Authentication connectedUser) {
        return ResponseEntity.ok(productsService.deleteProduct(id, connectedUser));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseMessage> getProduct(@RequestParam Long id) {
        return ResponseEntity.ok(productsService.getProduct(id));
    }

    @GetMapping("/get-all")
    public ResponseEntity<PageResponse<ProductDTO>> getProducts(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productsService.getProducts(page, size));
    }
}
