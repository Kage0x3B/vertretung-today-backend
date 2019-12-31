package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.MoodleResource;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Tabelle für alle von Moodle heruntergeladenen Dateien, speichert Informationen und den Datei Inhalt als SQL Blob
@Repository
public interface MoodleResourceRepository extends JpaRepository<MoodleResource, Integer> {
	Optional<MoodleResource> findFirstByFileNameOrderByModifiedDate(String fileName);

	Optional<MoodleResource> findFirstByResourceId(int resourceId);

	List<MoodleResource> findAllByModifiedDateBefore(LocalDateTime modifiedDate);

	List<MoodleResource> findByTypeOrderByModifiedDate(MoodleResourceInfo.ResourceType resourceType);

	Optional<MoodleResource> findTopByResourceIdOrderByModifiedDate(int resourceId);
}