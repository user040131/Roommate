package seungjub270.roommate_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import seungjub270.roommate_spring.domain.Account;
import seungjub270.roommate_spring.repository.AccountRepository;

@RequiredArgsConstructor
@Service
public class AccountDetailService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public Account loadUserByUsername(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
