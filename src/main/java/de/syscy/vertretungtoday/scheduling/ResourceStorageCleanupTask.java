package de.syscy.vertretungtoday.scheduling;

import de.syscy.vertretungtoday.service.MoodleResourceStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResourceStorageCleanupTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStorageCleanupTask.class);

	private MoodleResourceStorageService resourceStorageService;

	public ResourceStorageCleanupTask(MoodleResourceStorageService resourceStorageService) {
		this.resourceStorageService = resourceStorageService;
	}

	// Führt alle 60 Minuten eine Säuberung der "MoodleResource" Tabelle durch um sehr alte Dateien zu löschen.
	// Mehr dazu in der MoodleResourceStorageService Klasse
	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void updateMoodleResources() {
		LOGGER.info("Cleaning up resource storage database...");
		int expiredAmount = resourceStorageService.cleanStorage();
		LOGGER.info("Removed " + expiredAmount + " expired resources!");
	}
}