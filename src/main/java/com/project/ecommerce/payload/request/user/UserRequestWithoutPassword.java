package com.project.ecommerce.payload.request.user;

import com.project.ecommerce.payload.request.abstracts.UserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserRequestWithoutPassword extends UserRequest {
}
