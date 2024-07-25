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
     * Updates the user information based on the provided user update request.
     *
     * @param request         The user update request containing the new user information.
     * @param connectedUser   The authenticated user who is performing the update.
     * @return A response message indicating the success of the update.
     * @throws AppBadRequestException        If the user to update is not found.
     * @throws OperationNotPermittedException If the connected user is not authorized to update the user.
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
     * Deletes a user with the given ID.
     *
     * @param id              the ID of the user to be deleted
     * @param connectedUser   the authenticated user performing the operation
     * @return a ResponseMessage indicating the success of the operation
     * @throws OperationNotPermittedException if the connected user is not authorized to delete the user
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
     * This method retrieves a user by their ID.
     *
     * @param id             The ID of the user to retrieve.
     * @param connectedUser  The currently authenticated user.
     * @return A ResponseMessage object containing the retrieved user as a DTO and a success message.
     * @throws OperationNotPermittedException  If the authenticated user is not authorized to retrieve the user.
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
     * @param page The page number (starting from 1) to retrieve.
     * @param size The number of users to retrieve per page.
     * @return A PageResponse containing the list of UserDto objects, as well as pagination information.
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
     * Makes a user a vendor.
     *
     * @param id the ID of the user to be made a vendor
     * @param connectedUser the currently authenticated user
     * @return the response message indicating the result of the operation
     * @throws OperationNotPermittedException if the authenticated user is not authorized to make the specified user a vendor
     * @throws AppBadRequestException if the vendor role is not found
     * @throws AppConflictException if the specified user is already a vendor
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
     * @param request         the UserPasswordUpdateRequest containing the old and new password
     * @param connectedUser   the current authenticated user
     * @return a ResponseMessage indicating whether the password was updated successfully
     * @throws AppBadRequestException       if the user is not found
     * @throws OperationNotPermittedException  if the authenticated user is not authorized to update the password
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
     * Authenticates the user with the old password and updates the user's password to the new password.
     *
     * @param oldPassword the old password of the user
     * @param newPassword the new password to update
     * @param user the user whose password needs to be updated
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

    /**
     * Retrieves a User object by its ID.
     *
     * @param id the ID of the User to retrieve
     * @return the User object identified by the given ID
     * @throws AppBadRequestException if the User with the given ID does not exist
     */
    private User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppBadRequestException("User not found"));
    }
}
