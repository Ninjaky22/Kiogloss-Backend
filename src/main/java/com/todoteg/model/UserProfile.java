package com.todoteg.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_userprofile")
@Getter
@Setter
@ToString(exclude = "account")
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_staff")
    private Boolean isStaff = false;
    
    @Column(name = "is_superuser")
    private Boolean isSuperuser = false;
    
    @Column(name = "profile_image")
    private String profileImage = "profile_p8im0u.png";
    
    @Pattern(regexp = "^\\+?1?\\d{8,15}$")
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Account account;
    
    // Métodos de UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isSuperuser) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
            );
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
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
        return isActive;
    }
}