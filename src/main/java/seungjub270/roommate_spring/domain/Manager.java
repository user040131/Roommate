package seungjub270.roommate_spring.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long managerId;

    @Column
    private int managerNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public void appointAccount(Account account) {
        this.account = account;
    }

    public Manager(int managerNumber) {
        this.managerNumber = managerNumber;
    }

    public static Manager newManager(int managerNumber, Account account) {
        Manager m = new Manager();
        m.managerNumber = managerNumber;
        m.appointAccount(account);
        return m;
    }
}
