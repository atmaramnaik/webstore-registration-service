package registration.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import registration.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege,Long> {
    Privilege findByName(String name);
}
