package de.syscy.vertretungtoday.service;

import de.syscy.vertretungtoday.model.MoodleResource;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import de.syscy.vertretungtoday.repository.MoodleResourceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Kümmert sich um das Speichern von Dateien in der Datenbank und das Aufräumen dieser damit sehr alte Dateien auch gelöscht werden,
// besonders weil in der Datenbank auch der Datei Inhalt gespeichert wird
@Service
public class MoodleResourceStorageService {
	private MoodleResourceRepository resourceRepository;
	private long resourceExpiration;

	public MoodleResourceStorageService(MoodleResourceRepository resourceRepository,
										@Value("${resourceStorage.resourceExpiration}") Long resourceExpiration) {
		this.resourceRepository = resourceRepository;
		this.resourceExpiration = resourceExpiration;
	}

	public MoodleResource store(MoodleResource resource) {
		return resourceRepository.saveAndFlush(resource);
	}

	public java.util.List<MoodleResource> storeAll(Iterable<MoodleResource> resources) {
		return resourceRepository.saveAll(resources);
	}

	public Optional<MoodleResource> retrieveByResourceInfo(MoodleResourceInfo resourceInfo) {
		return resourceRepository.findFirstByResourceId(resourceInfo.getId());
	}

	public Optional<MoodleResource> retrieveByFileName(String fileName) {
		return resourceRepository.findFirstByFileNameOrderByModifiedDate(fileName);
	}

	public int cleanStorage() {
		LocalDateTime expirationDate = LocalDateTime.now();
		expirationDate = expirationDate.minusSeconds(resourceExpiration);

		List<MoodleResource> expiredResourceList = resourceRepository.findAllByModifiedDateBefore(expirationDate);
		resourceRepository.deleteAll(expiredResourceList);

		return expiredResourceList.size();
	}
}