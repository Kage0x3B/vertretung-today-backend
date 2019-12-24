package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.SubstitutionEntry;
import de.syscy.vertretungtoday.security.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubstitutionInfoRepository extends JpaRepository<SubstitutionEntry, Integer> {

}