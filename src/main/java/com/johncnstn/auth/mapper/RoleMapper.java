package com.johncnstn.auth.mapper;

import static org.mapstruct.factory.Mappers.getMapper;

import com.johncnstn.auth.entity.enums.UserRoleType;
import com.johncnstn.auth.generated.model.UserRole;
import com.johncnstn.auth.security.DomainGrantedAuthority;
import org.mapstruct.Mapper;

@Mapper
public interface RoleMapper {

    RoleMapper ROLE_MAPPER = getMapper(RoleMapper.class);

    DomainGrantedAuthority toGrantedAuthority(UserRoleType source);

    UserRoleType toType(UserRole userRole);
}
