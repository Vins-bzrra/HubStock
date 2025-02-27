package com.vins.hubstock.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vins.hubstock.security.BlacklistToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TokenFilter extends BasicAuthenticationFilter {
    public static final String HEADER_ATRIBUTO = "Authorization";
    public static final String ATRIBUTO_PREFIXO = "Bearer ";

    private final BlacklistToken blacklistToken;

    public TokenFilter(AuthenticationManager authenticationManager, BlacklistToken blacklistToken) {
        super(authenticationManager);
        this.blacklistToken = blacklistToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String atributo = request.getHeader(HEADER_ATRIBUTO);

        if (atributo == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!atributo.startsWith(ATRIBUTO_PREFIXO)) {
            chain.doFilter(request, response);
            return;
        }

        if (blacklistToken.isTokenInvalidated(atributo)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = atributo.replace(ATRIBUTO_PREFIXO, "");

        DecodedJWT decodedToken = null;
        try {
            decodedToken = JWT.require(Algorithm.HMAC512(AuthenticateFilter.TOKEN_SENHA)).build().verify(token);
            UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            String userId = null;
            DecodedJWT expiredToken = JWT.decode(token);
            userId = expiredToken.getSubject();

            Date expirationDate = expiredToken.getExpiresAt();
            Date currentDate = new Date();
            long timeDifferenceMinutes = TimeUnit.MILLISECONDS
                    .toMinutes(currentDate.getTime() - expirationDate.getTime());
            if (timeDifferenceMinutes <= 3) {

                String newToken = JWT.create().withSubject(userId)
                        .withExpiresAt(new Date(System.currentTimeMillis() + AuthenticateFilter.TOKEN_EXPIRACAO))
                        .sign(Algorithm.HMAC512(AuthenticateFilter.TOKEN_SENHA));

                response.setHeader("NewToken", newToken);

                // Adiciona o novo token ao cabeçalho da requisição
                final HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {

                    @Override
                    public String getHeader(String name) {
                        if (name.equalsIgnoreCase("Authorization")) {
                            return "Bearer " + newToken;
                        }
                        return super.getHeader(name);
                    }
                };
                // Atualiza o token no cabeçalho da requisição manualmente
                requestWrapper = new HttpServletRequestWrapper(requestWrapper) {
                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        if (name.equalsIgnoreCase("Authorization")) {
                            List<String> headers = Collections.list(super.getHeaders(name));
                            headers.replaceAll(header -> "Bearer " + newToken);
                            return Collections.enumeration(headers);
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public String getHeader(String name) {
                        if (name.equalsIgnoreCase("Authorization")) {
                            return "Bearer " + newToken;
                        }
                        return super.getHeader(name);
                    }
                };

                UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(newToken);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                chain.doFilter(requestWrapper, response);
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {

        String user = JWT.require(Algorithm.HMAC512(AuthenticateFilter.TOKEN_SENHA)).build().verify(token).getSubject();

        if (user == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
}
