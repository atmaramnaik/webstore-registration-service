package registration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import registration.service.TokenAuthenticationService;

@EnableWebSecurity
public class SecurityConfig {

    @Configuration
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        JWTAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        TokenAuthenticationService tokenAuthenticationService;

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and().csrf().disable().authorizeRequests()
                    .antMatchers("/health").permitAll()
                    .antMatchers("/auth/register").permitAll()
                    .antMatchers("/auth/login").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    // We filter the api/login requests
                    .addFilterBefore(new JWTLoginFilter("/auth/login", authenticationManager(),tokenAuthenticationService),
                            UsernamePasswordAuthenticationFilter.class)
                    // And filter other requests to check the presence of JWT in header
                    .addFilterBefore(jwtAuthenticationFilter,
                            UsernamePasswordAuthenticationFilter.class);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());

        }
        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration corsConfiguration=new CorsConfiguration().applyPermitDefaultValues();
            corsConfiguration.addExposedHeader("Authorization");
            source.registerCorsConfiguration("/**", corsConfiguration);
            return source;
        }

    }
    @Order(1)
    @Configuration
    public static class ActuatorConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/health")
                    .authorizeRequests()
                    .antMatchers("/health").permitAll();
        }
    }
}
