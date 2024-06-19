package com.project.ecommerce.entity.concretes.user;

import com.project.ecommerce.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "roles")

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleType roleType;

    String roleName;

//    Role Type: Enumerated type RoleType specifies the role assigned to the user.
//    Role Name: Optional field to provide a descriptive name for the role.
}
