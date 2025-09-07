package seungjub270.roommate_spring.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import seungjub270.roommate_spring.domain.School.School;

import java.util.Collection;
import java.util.List;

@Entity
public class Account implements UserDetails {
    //이거는 SecutiryContextHolder가 있어야 제대로 되는 듯
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long accountId;

    @Column
    public String email;

    @Column
    public String password;

    @Column(nullable = false)
    public Auth auth;

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE" + auth)); }
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    public Student getStudent() { return this.student; }
    public Manager getManager() { return this.manager; }

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public Student student;

    public void appointStudent(Student student) {
        this.student = student;
        student.appointAccount(this);
    }

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public Manager manager;

    public void appointManager(Manager manager) {
        this.manager = manager;
        manager.appointAccount(this);
    }

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public RefreshToken refreshToken;

    public void appointRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
        refreshToken.appointAccount(this);
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_id", nullable = false)
    public School school;

    public void appointSchool(School school){
        this.school = school;
    }
}

