package tech.nuqta.handihub.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.user.dto.UserDto;
import tech.nuqta.handihub.user.dto.request.UserPasswordUpdateRequest;
import tech.nuqta.handihub.user.dto.request.UserUpdateRequest;
import tech.nuqta.handihub.user.service.UserService;

/**
 * The UserController class is a REST controller that handles HTTP requests related to user operations.
 * It provides endpoints for updating a user,*/
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @PutMapping("/update-password")
    public ResponseEntity<ResponseMessage> updatePassword(@RequestBody @Valid UserPasswordUpdateRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> deleteUser(@RequestParam Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseMessage> getUser(@RequestParam Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserDto>> getUsers(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(userService.getUsers(page, size));
    }
}
