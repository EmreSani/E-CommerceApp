package com.project.ecommerce.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.enums.Gender;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "t_customer")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    private String birthPlace;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(unique = true)
    private String email;

    private Boolean built_in;

    @Column(unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private Set<OrderItem> orderItems;

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Boolean isPremium;

}
