package com.johncnstn.auth.mapper;

import com.johncnstn.auth.entity.UserEntity;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.security.DomainUserDetailsWithPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.johncnstn.auth.mapper.RoleMapper.ROLE_MAPPER;
import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(uses = {
        JavaTimeMapper.class,
})
public interface UserMapper {

    UserMapper USER_MAPPER = getMapper(UserMapper.class);

    default DomainUserDetailsWithPassword toUserDetails(UserEntity entity) {
        return entity == null ? null : new DomainUserDetailsWithPassword(
                entity.getId(),
                entity.getPasswordHash(),
                ROLE_MAPPER.toGrantedAuthority(entity.getRole())
        );
    }

    @Mapping(target = "passwordHash", ignore = true)
    UserEntity toEntity(User user);

    @Mapping(target = "password", ignore = true)
    User toModel(UserEntity user);

}
