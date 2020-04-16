package org.qtee.dashboard.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private final String username;
    private final String password;
    private final Integer role;
    private final Boolean enabled;

    @Column(name = "fullname")
    private final String fullName;

    @Column(name = "phone")
    private final String phoneNumber;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private final Account account;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == 1) {
            // TODO make role as string in Entity
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        } else {
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
