package tech.nuqta.handihub.common;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.nuqta.handihub.enums.RoleName;
import tech.nuqta.handihub.role.Role;
import tech.nuqta.handihub.role.RoleRepository;
import tech.nuqta.handihub.user.entity.User;
import tech.nuqta.handihub.user.repository.UserRepository;

import java.util.List;

@Component
public class SetupDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Sets up initial data in the application by creating default roles and users if they don't already exist.
     *
     * @param userRepository   the user repository
     * @param roleRepository   the role repository
     * @param passwordEncoder  the password encoder
     */
    public SetupDataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * This method is responsible for running the setup data loading process.
     * It checks if the role and user repositories are empty, and if so, it creates and saves initial role and user entities.
     * The roles "USER", "ADMIN" and "VENDOR" are created and saved in the role repository.
     * Then, the user "Adminjon" and "Userbek" are created and saved in the user repository, with their respective roles.
     *
     * @param args The command line arguments passed to the application
     */
    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            var user = new Role();
            user.setName(RoleName.USER);
            roleRepository.save(user);

            var admin = new Role();
            admin.setName(RoleName.ADMIN);
            roleRepository.save(admin);

            var vendor = new Role();
            vendor.setName(RoleName.VENDOR);
            roleRepository.save(vendor);
        }
        if (userRepository.count() == 0) {
            var userRole = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
            var adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow(
                    () -> new IllegalStateException("ROLE ADMIN was not initiated"));
            var vendorRole = roleRepository.findByName(RoleName.VENDOR).orElseThrow(
                    () -> new IllegalStateException("ROLE VENDOR was not initiated"));

            var admin = new User();
            admin.setFirstname("Adminjon");
            admin.setLastname("Adminbekov");
            admin.setEmail("admin@handihub.uz");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(List.of(adminRole));
            admin.setEnabled(true);
            admin.setAccountLocked(false);
            userRepository.save(admin);

            var user = new User();
            user.setFirstname("Userbek");
            user.setLastname("Userjonov");
            user.setEmail("user@handihub.uz");
            user.setRoles(List.of(userRole, vendorRole));
            user.setEnabled(true);
            user.setAccountLocked(false);
            user.setPassword(passwordEncoder.encode("user"));
            user.setRoles(List.of(userRole));
            userRepository.save(user);

        }
    }
}
