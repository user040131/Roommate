package seungjub270.roommate_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungjub270.roommate_spring.domain.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
}
