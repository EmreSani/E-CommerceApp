package com.project.ecommerce.service.helper;


import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.BadRequestException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;

    public User isUserExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,
                        userId)));
    }

    public void checkBuiltIn(User user){
        if(Boolean.TRUE.equals(user.getBuilt_in())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }
}
