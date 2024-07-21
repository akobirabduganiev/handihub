package tech.nuqta.handihub.role;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.nuqta.handihub.enums.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
