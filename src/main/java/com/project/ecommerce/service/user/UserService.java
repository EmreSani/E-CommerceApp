package com.project.ecommerce.service.user;

import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.payload.response.user.UserResponse;
import com.project.ecommerce.repository.user.UserRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private final MethodHelper methodHelper;

    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {



    }
}
