package com.johncnstn.auth.mapper;

import static org.mapstruct.factory.Mappers.getMapper;

import com.johncnstn.auth.generated.model.Token;
import com.johncnstn.auth.security.JwtTokens;
import org.mapstruct.Mapper;

@Mapper
public interface TokenMapper {

    TokenMapper TOKEN_MAPPER = getMapper(TokenMapper.class);

    Token toModel(JwtTokens source);
}
