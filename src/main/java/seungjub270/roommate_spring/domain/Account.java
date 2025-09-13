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
    public Long id;

    @Column
    public String email;

    @Column
    public String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Auth auth;

    public Account() {
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE" + auth.name())); }
    public Long getId() { return id; }
    public String getEmail() { return email; }
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

    public Account(String email, String password, Auth auth, Manager manager) {
        this.email = email;
        this.password = password;
        this.auth = auth;
        this.manager = manager;
    }

    public static Account createNewManager(String email, String password, Auth auth, Manager manager) {
        Account account = new Account(email, password, auth, manager);
        account.appointManager(manager);
        return account;
    }

    public Account(String email, String password, Auth auth, Student student) {
        this.email = email;
        this.password = password;
        this.auth = auth;
        this.student = student;
    }

    public static Account createNewStudent(String email, String password, Auth auth, Student student){
        Account account = new Account(email, password, auth, student);
        account.appointStudent(student);
        return account;
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

    public static Account newAccount(String email, String pw, Auth auth, School school){
        Account a = new Account();
        a.email = email;
        a.password = pw;
        a.auth = auth;
        a.appointSchool(school);
        return a;
    }
}

