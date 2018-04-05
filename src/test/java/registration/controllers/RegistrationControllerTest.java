package registration.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import registration.ApllicationConfig;
import registration.Application;
import registration.TestUtil;
import registration.dto.RegistrationDTO;
import registration.dto.UserDTO;
import registration.exceptions.InputError;
import registration.model.Role;
import registration.model.User;
import registration.repositories.RoleRepository;
import registration.repositories.UserRepository;
import registration.security.JWTAuthenticationFilter;
import registration.security.JWTLoginFilter;
import registration.security.TokenAuthenticationService;
import registration.security.WebSecurityConfig;
import registration.service.UserDetailsServiceImpl;
import registration.service.UserManager;
import registration.service.UserManagerImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {RegistrationController.class},includeFilters = @ComponentScan.Filter(classes = EnableWebSecurity.class))
public class RegistrationControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserManager userManager;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void should_return_all_users() throws Exception{
        User first=new User();
        first.setName("Atmaram");
        first.setEmail("naik.atmaram@gmail.com");
        first.setId(new Integer(1));
        first.setPassword("hello");

        Role role=new Role("ROLE_USER");
        ArrayList<Role> roles=new ArrayList<>();
        roles.add(role);
        first.setRoles(roles);
        User second=new User();
        second.setName("Anuradha");
        second.setEmail("anuradhanaik16@gmail.com");
        second.setId(new Integer(2));

        RegistrationDTO registrationDTO=new RegistrationDTO(first);
        Set<GrantedAuthority> grantedAuthorities=new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));

        doReturn(new org.springframework.security.core.userdetails.User(registrationDTO.getEmail(),registrationDTO.getPassword(),grantedAuthorities)).when(userDetailsService).loadUserByUsername(anyString());
        doReturn(Arrays.asList(first,second)).when(userManager).getAllUsers();
        mvc.perform(get("/auth/all").header("authorization","Bearer "+TokenAuthenticationService.getToken(first)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$.[0]",hasItem(
//                        allOf(
//                                hasProperty("id", is(1L)),
//                                hasProperty("name", is("Atmaram")),
//                                hasProperty("email", is("naik.atmaram@gmail.com"))
//                        ))))
//                .andExpect(jsonPath("$",hasItem(
//                    allOf(
//                            hasProperty("id", is(2L)),
//                            hasProperty("name", is("Anuradha")),
//                            hasProperty("email", is("anuradhanaik16@gmail.com"))
//                    )
//                )
//                )
//                )
        ;
        verify(userManager,times(1)).getAllUsers();
        verifyNoMoreInteractions(userManager);
    }
    @Test
    public void should_register_user() throws Exception{
        User user=new User();
        user.setName("Atmaram");
        user.setEmail("naik.atmaram@gmail.com");
        user.setId(new Integer(1));
        user.setPassword("hello");
        Role role=new Role("ROLE_USER");
        RegistrationDTO inputUser=new RegistrationDTO(user);

        doReturn(user).when(userManager).createUser(any(User.class));
        doReturn(role).when(roleRepository).findByName(anyString());
        mvc.perform(post("/auth/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(inputUser))
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(header().string("authorization",not(isEmptyString())));
        ;

        verify(userManager,times(1)).createUser(any(User.class));
        verifyNoMoreInteractions(userManager);
    }
    @Test
    public void should_handle_input_error() throws Exception{
        RegistrationDTO registrationDTO=new RegistrationDTO();
        registrationDTO.setName("Atmaram");
        registrationDTO.setEmail("naik.atmaram@gmail.com");
        registrationDTO.setId(new Integer(1));
        registrationDTO.setPassword("hello");
        doThrow(new InputError(InputError.DUPLICATE_EMAIL)).when(userManager).createUser(any(User.class));
        mvc.perform(post("/auth/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(registrationDTO))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(InputError.DUPLICATE_EMAIL)));

        verify(userManager,times(1)).createUser(any(User.class));
        verifyNoMoreInteractions(userManager);
    }

}
