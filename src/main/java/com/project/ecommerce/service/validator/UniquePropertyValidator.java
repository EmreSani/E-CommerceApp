package com.project.ecommerce.service.validator;


import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.ConflictException;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.request.abstracts.UserRequest;
import com.project.ecommerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final UserRepository userRepository;

    public void checkDuplicate(String username, String phone, String email){


        if(userRepository.existsByUsername(username)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME, username));
        }

        if(userRepository.existsByPhoneNumber(phone)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE, phone));
        }

        if(userRepository.existsByEmail(email)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL, email));
        }

    }

    public void checkUniqueProperties(User user, UserRequest userRequest){
        String updatedUsername = "";
        String updatedPhone = "";
        String updatedEmail = "";
        boolean isChanced = false;
        if(!user.getUsername().equalsIgnoreCase(userRequest.getUsername())){
            updatedUsername = userRequest.getUsername();
            isChanced = true;
        }
        if(!user.getPhoneNumber().equalsIgnoreCase(userRequest.getPhoneNumber())){
            updatedPhone = userRequest.getPhoneNumber();
            isChanced = true;
        }
        if(!user.getEmail().equalsIgnoreCase(userRequest.getEmail())){
            updatedEmail = userRequest.getEmail();
            isChanced = true;
        }

        if(isChanced) {
            checkDuplicate(updatedUsername, updatedPhone, updatedEmail);
        }

    }
}
