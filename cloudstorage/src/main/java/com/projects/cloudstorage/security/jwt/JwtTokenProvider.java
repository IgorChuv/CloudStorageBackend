package com.projects.cloudstorage.security.jwt;

import com.projects.cloudstorage.model.Role;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Qualifier("JwtTokenProvider")
public class JwtTokenProvider {

    //Секретная строка для формирования токена
    @Value("${jwt.token.secret}")
    private String secret;

    //Срок действия токена
    @Value("${jwt.token.expired}")
    private long validityMilliseconds;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Кодирование секретной строки
    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }


    //Создание токена
    public String createToken(String login, List<Role> roles) {

        Claims claims = Jwts.claims().setSubject(login);
        claims.put("roles", getRoleNames(roles));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    //Извлекает токен из заголовка запроса или возвращает null
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("auth-token");

        if (bearerToken != null && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    //Проверяет валидность токена
    public boolean validateToken(String token) throws AuthenticationException {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.warn("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            log.warn("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.warn("Malformed jwt");
        } catch (SignatureException sEx) {
            log.warn("Invalid signature");
        } catch (Exception e) {
            log.warn("invalid token");
        }
        return false;
    }

    //Возвращает список названий ролей пользователя
    private List<String> getRoleNames(List<Role> userRoles) {
        List<String> result = new ArrayList<>();

        userRoles.forEach(role -> {
            result.add(role.getName());
        });
        return result;
    }
}
