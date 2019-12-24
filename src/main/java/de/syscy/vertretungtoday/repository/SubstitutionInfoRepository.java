package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.SubstitutionEntry;
import de.syscy.vertretungtoday.security.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubstitutionInfoRepository extends JpaRepository<SubstitutionEntry, Integer> {

}