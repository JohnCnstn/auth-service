package com.johncnstn.auth.service;

import com.johncnstn.auth.generated.model.RefreshTokenRequest;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.Token;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.generated.model.UserRole;

public interface AuthService {

    User signUp(User user, UserRole userRole);

    Token signIn(SignInRequest signInRequest);

    Token refreshToken(RefreshTokenRequest refreshTokenRequest);
}
