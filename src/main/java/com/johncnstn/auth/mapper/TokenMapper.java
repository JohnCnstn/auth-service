package com.johncnstn.auth.mapper;

import com.johncnstn.auth.generated.model.Token;
import com.johncnstn.auth.security.JwtTokens;
import org.mapstruct.Mapper;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper
public interface TokenMapper {

    TokenMapper TOKEN_MAPPER = getMapper(TokenMapper.class);

    Token toModel(JwtTokens source);

}
