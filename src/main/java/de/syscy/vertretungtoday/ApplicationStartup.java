package de.syscy.vertretungtoday;

import de.syscy.vertretungtoday.security.model.Account;
import de.syscy.vertretungtoday.security.repository.AccountRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    public ApplicationStartup(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        accountRepository.deleteAll();

        Account account = new Account();
        account.setEmail("admin@vertretungtoday.de");
        account.setUsername("admin");
        account.setPassword(passwordEncoder.encode("test123"));
        account.setValidated(true);

        accountRepository.save(account);
    }
}
