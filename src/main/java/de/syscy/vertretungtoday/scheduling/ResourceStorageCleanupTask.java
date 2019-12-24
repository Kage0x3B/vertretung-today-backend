package de.syscy.vertretungtoday.scheduling;

import de.syscy.vertretungtoday.moodle.MoodleApi;
import de.syscy.vertretungtoday.moodle.MoodleApiException;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import de.syscy.vertretungtoday.service.MoodleResourceStorageService;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class ResourceStorageCleanupTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStorageCleanupTask.class);

	private MoodleResourceStorageService resourceStorageService;

	public ResourceStorageCleanupTask(MoodleResourceStorageService resourceStorageService) {
		this.resourceStorageService = resourceStorageService;
	}

	@Scheduled(fixedDelay = 6 * 60 * 60 * 1000)
	public void updateMoodleResources() {
		LOGGER.info("Cleaning up resource storage database...");
		int expiredAmount = resourceStorageService.cleanStorage();
		LOGGER.info("Removed " + expiredAmount + " expired resources!");
	}
}