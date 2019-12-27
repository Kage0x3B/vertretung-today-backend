package de.syscy.vertretungtoday.security.repository;

import de.syscy.vertretungtoday.security.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	Optional<Account> findByUsername(String username);
}