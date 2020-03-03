package com.johncnstn.auth.api;

import com.johncnstn.auth.generated.api.AuthApi;
import com.johncnstn.auth.generated.model.RefreshTokenRequest;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.SignUpRequest;
import com.johncnstn.auth.generated.model.Token;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.service.AuthService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<Token> refreshToken(@Valid RefreshTokenRequest request) {
        var result = authService.refreshToken(request);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Token> signIn(@Valid SignInRequest request) {
        var result = authService.signIn(request);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<User> signUp(@Valid SignUpRequest request) {
        var result = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
