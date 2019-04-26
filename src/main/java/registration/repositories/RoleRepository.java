package registration.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import registration.model.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String name);
}
