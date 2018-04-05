package registration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security
        .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import registration.model.User;

import static java.util.Collections.emptyList;

public class TokenAuthenticationService {
    static final long EXPIRATIONTIME = 864_000_000; // 10 days
    static final String SECRET = "ThisIsASecret";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";
    public static String getToken(User user){
        Claims claims=Jwts.claims().setSubject(user.getEmail());
        if(user.getRoles()!=null){
            claims.put("scopes",user.getRoles().stream().map(s->s.getName()).collect(Collectors.toList()));
        }
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    static void addAuthentication(HttpServletResponse res, User user) {

        String JWT = getToken(user);
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
    }

    public static List<String> getRoles(String token){
        if (token != null) {
            // parse the token.
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""));
            String email = claimsJws.getBody().getSubject();
            return (List<String>) claimsJws.getBody().get("scopes");

        }
        return null;
    }

    static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            Jws<Claims> claimsJws= Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""));
            String email=claimsJws.getBody().getSubject();
            List<String> scopes = (List<String>) claimsJws.getBody().get("scopes");
            List<GrantedAuthority> authorities;
            if(scopes!=null) {
                authorities = scopes.stream()
                        .map(authority -> new SimpleGrantedAuthority(authority))
                        .collect(Collectors.toList());
            } else {
                authorities=new ArrayList<>();
            }
            return email != null ?
                    new UsernamePasswordAuthenticationToken(email, null, authorities) :
                    null;
        }
        return null;
    }
}
