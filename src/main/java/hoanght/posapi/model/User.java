package hoanght.posapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hoanght.posapi.common.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "provider", nullable = false)
    private String provider = "local";

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "email")
    private String email;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = Set.of(Role.ROLE_USER);
}