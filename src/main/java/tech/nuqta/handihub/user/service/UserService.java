package tech.nuqta.handihub.user.service;

import tech.nuqta.handihub.common.PageResponse;
import tech.nuqta.handihub.common.ResponseMessage;
import tech.nuqta.handihub.user.dto.UserDto;
import tech.nuqta.handihub.user.dto.request.UserPasswordUpdateRequest;
import tech.nuqta.handihub.user.dto.request.UserUpdateRequest;

public interface UserService {
    ResponseMessage updateUser(UserUpdateRequest user);
    ResponseMessage deleteUser(Long id);
    ResponseMessage getUser(Long id);
    PageResponse<UserDto> getUsers(int page, int size);
    ResponseMessage makeVendor(Long id);
    ResponseMessage updatePassword(UserPasswordUpdateRequest userPasswordUpdateRequest);
}
