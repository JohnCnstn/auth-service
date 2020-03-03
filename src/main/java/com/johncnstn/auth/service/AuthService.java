package com.johncnstn.auth.service;

import com.johncnstn.auth.generated.model.RefreshTokenRequest;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.SignUpRequest;
import com.johncnstn.auth.generated.model.Token;
import com.johncnstn.auth.generated.model.User;

public interface AuthService {

    User signUp(SignUpRequest request);

    Token signIn(SignInRequest request);

    Token refreshToken(RefreshTokenRequest refreshTokenRequest);
}
