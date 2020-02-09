package com.johncnstn.auth.api

import com.johncnstn.auth.generated.api.AuthApi
import com.johncnstn.auth.generated.model.*
import com.johncnstn.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class AuthApiController(val authService: AuthService) : AuthApi {

    override fun refreshToken(@Valid @RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<Token> {
        val token = authService.refreshToken(refreshTokenRequest)
        return ResponseEntity.ok(token)
    }

    override fun signIn(@Valid @RequestBody signInRequest: SignInRequest): ResponseEntity<Token> {
        val token = authService.signIn(signInRequest)
        return ResponseEntity.ok(token)
    }

    override fun signUp(@Valid @RequestBody user: User): ResponseEntity<User> {
        val created = authService.signUp(user, UserRole.USER)
        // FIXME: add location header
        return ResponseEntity(created, HttpStatus.CREATED)
    }

}