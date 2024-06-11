package com.project.ecommerce.service.user;

import com.project.ecommerce.entity.concretes.user.UserRole;
import com.project.ecommerce.entity.enums.RoleType;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole getUserRole(RoleType roleType){
        return userRoleRepository.findByEnumRoleEquals(roleType).orElseThrow(()->
                new ResourceNotFoundException(ErrorMessages.ROLE_NOT_FOUND));
    }

    public List<UserRole> getAllUserRole(){
        return userRoleRepository.findAll();
    }
}
