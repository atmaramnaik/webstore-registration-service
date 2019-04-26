package registration.service;

import org.hibernate.exception.DataException;

import org.junit.Test;

import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import registration.dto.RegistrationDTO;
import registration.dto.UserDTO;
import registration.exceptions.InputError;
import registration.model.User;
import registration.repositories.UserRepository;

import org.hibernate.exception.ConstraintViolationException;
import static org.assertj.core.api.Assertions.*;





import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UserManagerTest {
    @Configuration
    static class UserManagerImplTestContextConfiguration{
        @Bean
        public UserManager userManager(){
            return new UserManagerImpl();
        }
        @Bean
        public UserRepository userRepository(){
            return mock(UserRepository.class);
        }
        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void should_get_all_users_return_list_of_userdto(){
        User first=new User();
        first.setName("Atmaram");
        first.setEmail("naik.atmaram@gmail.com");
        first.setId(new Integer(1));
        first.setPassword("hello");

        User second=new User();
        second.setName("Anuradha");
        second.setEmail("anuradhanaik16@gmail.com");
        second.setId(new Integer(2));
        second.setPassword("hello");
        doReturn(Arrays.asList(first,second)).when(userRepository).findAll();
        Iterable<User> response=userManager.getAllUsers();

        assertThat(response).contains(first,second);

    }
    @Test
    public void should_create_user_and_return_userdto(){
        User user=new User();
        user.setName("Atmaram");
        user.setEmail("naik.atmaram@gmail.com");
        user.setId(new Integer(1));
        user.setPassword("hello");

        doReturn(user).when(userRepository).save(any(User.class));

        User response= null;
        try {
            response = userManager.createUser(user);
        } catch (InputError inputError) {
            inputError.printStackTrace();
        }
        assertThat(response).isEqualTo(user);

    }

    @Test
    public void should_thow_exception_when_duplicate_email(){
        User user=new User();
        user.setName("Atmaram");
        user.setEmail("naik.atmaram@gmail.com");
        user.setId(new Integer(1));
        user.setPassword("hello");

        doThrow(new DataIntegrityViolationException("Test", Mockito.mock(ConstraintViolationException.class))).when(userRepository).save(any(User.class));
        assertThatThrownBy(()->{userManager.createUser(user);}).isEqualTo(new InputError(InputError.DUPLICATE_EMAIL));

    }
    @Test
    public void should_thow_exception_when_data_too_long(){
        User user=new User();
        user.setName("Atmaram");
        user.setEmail("naik.atmaram@gmail.com");
        user.setId(new Integer(1));
        user.setPassword("hello");

        doThrow(new DataIntegrityViolationException("Test", Mockito.mock(DataException.class))).when(userRepository).save(any(User.class));

        assertThatThrownBy(()->{userManager.createUser(user);}).isEqualTo(new InputError(InputError.DATA_TOO_LONG));

    }
    @Test
    public void should_thow_exception_when_unknown_exception(){
        User user=new User();
        user.setName("Atmaram");
        user.setEmail("naik.atmaram@gmail.com");
        user.setId(new Integer(1));
        user.setPassword("hello");

        doThrow(new DataIntegrityViolationException("Test", Mockito.mock(Exception.class))).when(userRepository).save(any(User.class));

        assertThatThrownBy(()->{userManager.createUser(user);}).isEqualTo(new InputError(InputError.UNKOWN));

    }

}
