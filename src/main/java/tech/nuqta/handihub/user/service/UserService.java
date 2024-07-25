package tech.nuqta.handihub.user.service;

import org.springframework.security.core.Authentication;
import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.user.dto.UserDto;
import tech.nuqta.handihub.user.dto.request.UserPasswordUpdateRequest;
import tech.nuqta.handihub.user.dto.request.UserUpdateRequest;

public interface UserService {
    ResponseMessage updateUser(UserUpdateRequest user, Authentication connectedUser);
    ResponseMessage deleteUser(Long id, Authentication connectedUser);
    ResponseMessage getUser(Long id, Authentication connectedUser);
    PageResponse<UserDto> getUsers(int page, int size);
    ResponseMessage makeVendor(Long id, Authentication connectedUser);
    ResponseMessage updatePassword(UserPasswordUpdateRequest request, Authentication connectedUser);
}
