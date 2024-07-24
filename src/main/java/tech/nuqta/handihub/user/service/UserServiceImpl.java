package tech.nuqta.handihub.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.enums.RoleName;
import tech.nuqta.handihub.exception.AppBadRequestException;
import tech.nuqta.handihub.mapper.UserMapper;
import tech.nuqta.handihub.role.Role;
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
    private final AuthenticationManager authenticationManager;

    /**
     * Updates the user information with the provided details.
     *
     * @param request The object containing the user update request details.
     * @return The response message indicating the status of the update operation.
     * @throws AppBadRequestException If the user specified in the request is not found.
     */
    @Override
    public ResponseMessage updateUser(UserUpdateRequest request) {
        var userToUpdate = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));
        userToUpdate.setFirstname(request.getFirstname());
        userToUpdate.setLastname(request.getLastname());
        userToUpdate.setEmail(request.getEmail());
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
    public ResponseMessage deleteUser(Long id) {
        var foundUser = getById(id);
        foundUser.setDeleted(true);
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
    public ResponseMessage getUser(Long id) {
        var user = getById(id);
        return new ResponseMessage(user, "User retrieved successfully");
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
    public ResponseMessage makeVendor(Long id) {
        var user = getById(id);
        user.setVendor(true);
        var vendor = new Role();
        vendor.setName(RoleName.VENDOR);
        user.getRoles().add(vendor);
        userRepository.save(user);
        log.info("User with id: {} is now a vendor", id);
        return new ResponseMessage("User is now a vendor");
    }

    /**
     * Updates the password of a user.
     *
     * @param request the UserPasswordUpdateRequest object containing request details
     * @return a ResponseMessage object indicating the status of the password update
     * @throws AppBadRequestException if the user is not found
     */
    @Override
    public ResponseMessage updatePassword(UserPasswordUpdateRequest request) {
        var user = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));

        authenticateAndUpdateUserPassword(request.getOldPassword(), request.getNewPassword(), user);

        userRepository.save(user);
        log.info("User with id: {} password updated", request.getId());
        return new ResponseMessage("Password updated successfully");
    }

    /**
     * Authenticates the user with the given old password, then updates the user's password with the new password.
     *
     * @param oldPassword The old password of the user.
     * @param newPassword The new password to set for the user.
     * @param user User object representing the user.
     * @throws AppBadRequestException If the old password is incorrect.
     */
    private void authenticateAndUpdateUserPassword(String oldPassword, String newPassword, User user) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        oldPassword
                )
        );
        if (auth.isAuthenticated()) {
            user.setPassword(newPassword);
        } else {
            throw new AppBadRequestException("Old password is incorrect");
        }
    }

    private User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppBadRequestException("User not found"));
    }
}
