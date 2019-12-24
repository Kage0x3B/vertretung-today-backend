package de.syscy.vertretungtoday.security.repository;

import de.syscy.vertretungtoday.security.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	List<Account> findByUsername(String username);
}