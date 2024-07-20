package tech.nuqta.handihub.common;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.nuqta.handihub.role.Role;
import tech.nuqta.handihub.role.RoleRepository;
import tech.nuqta.handihub.user.User;
import tech.nuqta.handihub.user.UserRepository;

import java.util.List;

@Component
public class SetupDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public SetupDataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.findAll().isEmpty()) {
            var admin = new Role();
            admin.setName("ADMIN");
            roleRepository.save(admin);

            var superAdmin = new Role();
            superAdmin.setName("SUPER_ADMIN");
            roleRepository.save(superAdmin);

            var user = new Role();
            user.setName("USER");
            roleRepository.save(user);
        }

    }
}
