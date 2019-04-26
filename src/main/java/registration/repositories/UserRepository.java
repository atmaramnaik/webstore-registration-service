package registration.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import registration.model.User;

public interface UserRepository extends CrudRepository<User,Long>, JpaRepository<User,Long>{
    User findByEmail(String email);
}
