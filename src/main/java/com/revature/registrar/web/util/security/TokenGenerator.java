package com.revature.registrar.web.util.security;

import com.revature.registrar.web.dtos.Principal;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class TokenGenerator {

    private final JWTConfig jwtConfig;

    public TokenGenerator(JWTConfig jwtConfig){
        this.jwtConfig = jwtConfig;
    }

    public String createToken(Principal subject){
        long now = System.currentTimeMillis();

        JwtBuilder tokenBuilder = Jwts.builder()
                                        .setId(subject.getId())
                                        .setSubject(subject.getUsername())
                                        //.claim("faculty",subject.g)
                                        .setIssuer("revature")
                                        .setIssuedAt(new Date(now))
                                        .setExpiration(new Date(now + jwtConfig.getExpiration()))
                                        .signWith(jwtConfig.getSigAlg(), jwtConfig.getSigningKey());

        System.out.println("In create Token");
        System.out.println(jwtConfig.getPrefix());
        System.out.println(jwtConfig.getPrefix() + tokenBuilder.compact());
        return jwtConfig.getPrefix() + tokenBuilder.compact();
    }

    public JWTConfig getJwtConfig() {
        return jwtConfig;
    }

}
