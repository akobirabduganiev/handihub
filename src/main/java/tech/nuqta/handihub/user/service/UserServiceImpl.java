package tech.nuqta.handihub.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.enums.RoleName;
import tech.nuqta.handihub.exception.AppBadRequestException;
import tech.nuqta.handihub.exception.AppConflictException;
import tech.nuqta.handihub.exception.OperationNotPermittedException;
import tech.nuqta.handihub.mapper.UserMapper;
import tech.nuqta.handihub.role.RoleRepository;
import tech.nuqta.handihub.user.dto.UserDto;
import tech.nuqta.handihub.user.dto.request.UserPasswordUpdateRequest;
import tech.nuqta.handihub.user.dto.request.UserUpdateRequest;
import tech.nuqta.handihub.user.entity.User;
import tech.nuqta.handihub.user.repository.UserRepository;

import java.util.Optional;

/***
 * Implementation of the UserService interface that provides methods for interacting with user entities.
 * It is responsible for updating, deleting, retrieving, and managing user data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseMessage updateUser(UserUpdateRequest request, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var userToUpdate = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));
        if (!user.getId().equals(userToUpdate.getId()) &&
                !user.isAdmin()) {
            throw new OperationNotPermittedException("You are not authorized to update this user");
        }
        userToUpdate.setFirstname(request.getFirstname());
        userToUpdate.setLastname(request.getLastname());
        Optional.ofNullable(request.getDateOfBirth()).ifPresent(userToUpdate::setDateOfBirth);
        Optional.ofNullable(request.getGender()).ifPresent(userToUpdate::setGender);
        userRepository.save(userToUpdate);
        updateUserAsVendorLog(request.getId(), user);
        return new ResponseMessage("User updated successfully");
    }

    @Override
    public ResponseMessage deleteUser(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var foundUser = getById(id);
        if (!user.getId().equals(foundUser.getId()) &&
                !user.isAdmin()) {
            throw new OperationNotPermittedException("You are not authorized to delete this user");
        }
        foundUser.setDeleted(true);
        foundUser.setEnabled(false);
        userRepository.save(foundUser);
        log.info("User with id: {} deleted by user with id: {}", id, user.getId());
        return new ResponseMessage("User deleted successfully");
    }

    @Override
    public ResponseMessage getUser(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var retrievedUser = getById(id);
        if (!user.getId().equals(retrievedUser.getId()) && !user.isAdmin()) {
            throw new OperationNotPermittedException("You are not authorized to retrieve this user");
        }
        log.info("User with id: {} retrieved by user with id: {}", id, user.getId());
        return new ResponseMessage(UserMapper.toDto(retrievedUser), "User retrieved successfully");
    }

    @Override
    public PageResponse<UserDto> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
        log.info("Page {} of users retrieved with page size {}", page, size);
        return new PageResponse<>(
                UserMapper.toDtoList(users.getContent()),
                users.getNumber() + 1,
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }

    @Override
    public ResponseMessage makeVendor(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var currentUser = getById(id);
        if (!user.getId().equals(currentUser.getId()) && !user.isAdmin()) {
            throw new OperationNotPermittedException("You are not authorized to make this user a vendor");
        }
        currentUser.setVendor(true);

        var vendor = roleRepository.findByName(RoleName.VENDOR).orElseThrow(() -> new AppBadRequestException("Role not found"));

        if (currentUser.getRoles().stream().noneMatch(role -> role.getId().equals(vendor.getId()))) {
            currentUser.getRoles().add(vendor);
            userRepository.save(currentUser);
            updateUserAsVendorLog(id, user);
            return new ResponseMessage("User is now a vendor");
        } else {
            log.info("User with id: {} is already a vendor, action attempted by user with id: {}", id, user.getId());
            throw new AppConflictException("User is already a vendor");
        }
    }

    @Override
    public ResponseMessage updatePassword(UserPasswordUpdateRequest request, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var currentUser = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));
        if (!user.getId().equals(currentUser.getId())) {
            throw new OperationNotPermittedException("You are not authorized to update this user's password");
        }

        authenticateAndUpdateUserPassword(request.getOldPassword(), request.getNewPassword(), currentUser);
        log.info("Password for user with id: {} updated, action performed by user with id: {}", request.getId(), user.getId());
        return new ResponseMessage("Password updated successfully");
    }

    private void authenticateAndUpdateUserPassword(String oldPassword, String newPassword, User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        oldPassword
                )
        );
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppBadRequestException("User not found"));
    }

    private static void updateUserAsVendorLog(Long id, User user) {
        log.info("User with id: {} is now a vendor, updated by user with id: {}", id, user.getId());
    }
}