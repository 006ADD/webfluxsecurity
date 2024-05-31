package net.proselyte.webfluxsecurity.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import net.proselyte.webfluxsecurity.exception.AuthException;
import net.proselyte.webfluxsecurity.repository.UserRepository;
import net.proselyte.webfluxsecurity.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SecurityService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenDetails generateToken(UserEntity user){
        Map<String, Object> claims = new HashMap<>(){{
            put("role", user.getRole());
            put("username", user.getUsername());
        }};

        return generateToken(claims, user.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject){
        Long expirationTimeMillis = expirationInSeconds * 1000L;
        Date expiretionDate = new Date(new Date().getTime() + expirationTimeMillis);

        return generateToken(expiretionDate, claims, subject);
    }

    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject){
        Date createDate = new Date();
        String token = Jwts.builder().
                setClaims(claims).setIssuer(issuer).setSubject(subject)
                    .setIssuedAt(createDate).setId(UUID.randomUUID().toString())
                    .setExpiration(expirationDate)
                    .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes())).
                compact();

        return TokenDetails.builder().
        token(token).issuedAt(createDate).expiresAt(expirationDate).build();
    }

    public Mono<TokenDetails> authenticate(String username, String password){
        return userRepository.findByUsername(username).
                flatMap(user -> {
                    if(!user.isEnabled()){
                        return Mono.error(new AuthException("Account disabled", "PROSELYTE_USER_ACCOUNT_DISABLED"));
                    }
                    if(!passwordEncoder.matches(password, user.getPassword())){
                        return Mono.error(new AuthException("Invalid password", "PROSELYTE_INVALID_PASSWORD"));
                    }
                    return Mono.just(generateToken(user).toBuilder().userId(user.getId()).build());
                }).switchIfEmpty(Mono.error(new AuthException("Invalid username", "PROSELYTE_INVALID_USERNAME")));
    }
}
