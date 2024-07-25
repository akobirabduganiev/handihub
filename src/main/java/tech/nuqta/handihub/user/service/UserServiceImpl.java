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

    /**
     * Updates the user information with the provided details.
     *
     * @param request The object containing the user update request details.
     * @return The response message indicating the status of the update operation.
     * @throws AppBadRequestException If the user specified in the request is not found.
     */
    @Override
    public ResponseMessage updateUser(UserUpdateRequest request, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var userToUpdate = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));
        if (!user.getId().equals(userToUpdate.getId()) &&
                user.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ADMIN))) {
            throw new OperationNotPermittedException("You are not authorized to update this user");
        }
        userToUpdate.setFirstname(request.getFirstname());
        userToUpdate.setLastname(request.getLastname());
        Optional.ofNullable(request.getDateOfBirth()).ifPresent(userToUpdate::setDateOfBirth);
        Optional.ofNullable(request.getGender()).ifPresent(userToUpdate::setGender);
        userRepository.save(userToUpdate);
        log.info("User with id: {} updated", request.getId());
        return new ResponseMessage("User updated successfully");
    }

    /**
     * Deletes a user by id.
     *
     * @param id The id of the user to be deleted.
     * @return A ResponseMessage indicating the result of the operation.
     */
    @Override
    public ResponseMessage deleteUser(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var foundUser = getById(id);
        if (!user.getId().equals(foundUser.getId()) &&
                user.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ADMIN))) {
            throw new OperationNotPermittedException("You are not authorized to delete this user");
        }
        foundUser.setDeleted(true);
        foundUser.setEnabled(false);
        userRepository.save(foundUser);
        log.info("User with id: {} deleted", id);
        return new ResponseMessage("User deleted successfully");
    }

    /**
     * Gets a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A ResponseMessage object containing the retrieved user and a success message.
     */
    @Override
    public ResponseMessage getUser(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var retrievedUser = getById(id);
        if (!user.getId().equals(retrievedUser.getId()) &&
                user.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ADMIN))) {
            throw new OperationNotPermittedException("You are not authorized to retrieve this user");
        }
        return new ResponseMessage(UserMapper.toDto(retrievedUser), "User retrieved successfully");
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param page The page number to retrieve (starting from 1).
     * @param size The number of users to retrieve per page.
     * @return A {@link PageResponse} containing the list of {@link UserDto} objects, along with pagination information.
     */
    @Override
    public PageResponse<UserDto> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
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

    /**
     * Makes a user a vendor by updating the user's role to 'VENDOR' and saving the changes.
     *
     * @param id the ID of the user to make a vendor
     * @return a {@code ResponseMessage} indicating that the user is now a vendor
     */
    @Override
    public ResponseMessage makeVendor(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var currentUser = getById(id);
        if (!user.getId().equals(currentUser.getId()) &&
                user.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ADMIN))) {
            throw new OperationNotPermittedException("You are not authorized to make this user a vendor");
        }
        currentUser.setVendor(true);

        var vendor = roleRepository.findByName(RoleName.VENDOR).orElseThrow(() -> new AppBadRequestException("Role not found"));

        // Only add the vendor role if the current user doesn't already have it
        if (currentUser.getRoles().stream().noneMatch(role -> role.getId().equals(vendor.getId()))) { // ensure you compare with the relevant unique identifier
            currentUser.getRoles().add(vendor);
            userRepository.save(currentUser);
            log.info("User with id: {} is now a vendor", id);
            return new ResponseMessage("User is now a vendor");
        } else {
            log.info("User with id: {} is already a vendor", id);
            throw new AppConflictException("User is already a vendor");
        }

    }

    /**
     * Updates the password of a user.
     *
     * @param request the UserPasswordUpdateRequest object containing request details
     * @return a ResponseMessage object indicating the status of the password update
     * @throws AppBadRequestException if the user is not found
     */
    @Override
    public ResponseMessage updatePassword(UserPasswordUpdateRequest request, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var currentUser = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));
        if (!user.getId().equals(currentUser.getId())) {
            throw new OperationNotPermittedException("You are not authorized to update this user's password");
        }

        authenticateAndUpdateUserPassword(request.getOldPassword(), request.getNewPassword(), currentUser);
        log.info("User with id: {} password updated", request.getId());
        return new ResponseMessage("Password updated successfully");
    }

    /**
     * Authenticates the user using the given old password and updates the user's password to the given new password.
     *
     * @param oldPassword The old password of the user.
     * @param newPassword The new password to update for the user.
     * @param user The user for whom the password needs to be updated.
     */
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
}
