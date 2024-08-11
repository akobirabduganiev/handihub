package tech.nuqta.handihub.file;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.nuqta.handihub.exception.AppBadRequestException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final Path rootLocation;

    public ImageUploadService() {
        this.rootLocation = Paths.get("images");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public String store(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
        return filename;
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new AppBadRequestException("Could not read file: " + filename);
            }
        } catch (Exception e) {
            throw new AppBadRequestException("Could not read file: " + filename);
        }
    }
}
