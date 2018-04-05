package registration.service;

import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import registration.dto.RegistrationDTO;
import registration.dto.UserDTO;
import registration.exceptions.InputError;
import registration.model.User;
import registration.repositories.UserRepository;


import org.hibernate.exception.ConstraintViolationException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service("userManager")
public class UserManagerImpl implements UserManager {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) throws InputError {
        try{
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex){
            if(ex.getCause() instanceof ConstraintViolationException)
                throw new InputError(InputError.DUPLICATE_EMAIL);
            else if(ex.getCause() instanceof DataException)
                throw new InputError(InputError.DATA_TOO_LONG);
            else
                throw new InputError(InputError.UNKOWN);
        }
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
