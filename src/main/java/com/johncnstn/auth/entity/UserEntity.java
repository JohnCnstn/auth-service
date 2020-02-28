package com.johncnstn.auth.entity;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.AUTO;

import com.johncnstn.auth.entity.enums.UserRoleType;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = false, of = "id")
public class UserEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    protected UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash", nullable = false, unique = true)
    private String passwordHash;

    @Enumerated(STRING)
    @Type(type = "enumType")
    @Column(name = "role", nullable = false)
    private UserRoleType role;
}
