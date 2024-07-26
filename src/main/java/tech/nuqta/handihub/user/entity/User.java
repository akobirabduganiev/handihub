package tech.nuqta.handihub.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tech.nuqta.handihub.enums.Gender;
import tech.nuqta.handihub.role.Role;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The User class represents a user in the application.
 * <p>
 * It is an entity class mapped to the "users" table in the database.
 * It implements the UserDetails and Principal interfaces for authentication and authorization purposes.
 * <p>
 * It contains fields representing user information such as first name, last name, date of birth, email, password, gender,
 * whether the user is deleted or active, whether the user is a vendor, and other details.
 * <p>
 * It also defines relationships with the Role class to represent the roles associated with the user.
 * It provides methods to get user authorities, username, account status, and other details required for authentication and authorization.
 * <p>
 * It includes annotations such as @Entity, @Table, @Getter, @Setter, @SuperBuilder, @NoArgsConstructor, @AllArgsConstructor, @Override, and others
 * to define the entity mapping, getters and setters, builder pattern, default constructor, all args constructor, and method overrides.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    @Column
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private boolean isDeleted = false;
    private boolean isVendor = false;
    private boolean accountLocked;
    private boolean enabled;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;
    @LastModifiedBy
    @Column(insertable = false)
    private Long modifiedBy;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getName().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return email;
    }

    public String getFullName() {
        return firstname + " " + lastname;
    }
}
