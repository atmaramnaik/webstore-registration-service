package registration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
public class ApllicationConfig {


    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }
    @Bean
    public SecurityContextPersistenceFilter securityContextPersistenceFilter(){
        SecurityContextPersistenceFilter filter=new SecurityContextPersistenceFilter();
        filter.setForceEagerSessionCreation(true);
        return filter;
    }
}
