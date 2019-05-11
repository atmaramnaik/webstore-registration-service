package registration.service;

import org.springframework.security.core.Authentication;
import registration.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TokenAuthenticationService {
    String getToken(User user);

    void addAuthentication(HttpServletResponse res, User user) ;

    List<String> getRoles(String token);

    Authentication getAuthentication(HttpServletRequest request);
}
