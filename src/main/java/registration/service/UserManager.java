package registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import registration.dto.RegistrationDTO;
import registration.dto.UserDTO;
import registration.exceptions.InputError;
import registration.model.User;
import registration.repositories.UserRepository;

public interface UserManager {

    public User createUser(User user) throws InputError;
    public Iterable<User> getAllUsers();
    public User findByEmail(String email);
}
