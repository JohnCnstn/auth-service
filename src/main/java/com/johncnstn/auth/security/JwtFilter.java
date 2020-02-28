package com.johncnstn.auth.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
@AllArgsConstructor
public class JwtFilter extends GenericFilterBean {

    public static final String TOKEN_PREFIX = "Bearer ";

    private TokensProvider tokensProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        var httpServletRequest = (HttpServletRequest) servletRequest;
        var token = resolveToken(httpServletRequest);
        if (isNotEmpty(token) && tokensProvider.validateAccessToken(token)) {
            var authentication = tokensProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        var bearerToken = trim(request.getHeader(AUTHORIZATION));
        if (isNotEmpty(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

}
