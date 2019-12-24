package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.MoodleResource;
import de.syscy.vertretungtoday.model.SubstitutionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoodleResourceRepository extends JpaRepository<MoodleResource, Integer> {
	Optional<MoodleResource> findFirstByFileNameOrderByModifiedDate(String fileName);

	Optional<MoodleResource> findFirstByResourceId(int resourceId);

	List<MoodleResource> findAllByModifiedDateBefore(Date modifiedDate);
}