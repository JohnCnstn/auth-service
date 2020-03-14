package com.johncnstn.auth.service;

import com.johncnstn.auth.generated.model.AuthResponse;
import com.johncnstn.auth.generated.model.RefreshTokenRequest;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.SignUpRequest;
import com.johncnstn.auth.generated.model.User;

public interface AuthService {

    User signUp(SignUpRequest request);

    AuthResponse signIn(SignInRequest request);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
