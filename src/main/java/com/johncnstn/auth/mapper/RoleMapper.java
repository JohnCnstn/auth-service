package com.johncnstn.auth.mapper;

import com.johncnstn.auth.entity.enums.UserRoleEntity;
import com.johncnstn.auth.generated.model.UserRole;
import com.johncnstn.auth.security.DomainGrantedAuthority;
import org.mapstruct.Mapper;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper
public interface RoleMapper {

    RoleMapper ROLE_MAPPER = getMapper(RoleMapper.class);

    DomainGrantedAuthority toGrantedAuthority(UserRoleEntity source);


    UserRoleEntity toEntity(UserRole userRole);

}
