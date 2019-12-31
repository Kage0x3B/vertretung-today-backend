package de.syscy.vertretungtoday.scheduling;

import de.syscy.vertretungtoday.event.MoodleFileUpdateEventPublisher;
import de.syscy.vertretungtoday.model.MoodleResource;
import de.syscy.vertretungtoday.moodle.MoodleApi;
import de.syscy.vertretungtoday.moodle.MoodleApiException;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import de.syscy.vertretungtoday.service.MoodleResourceStorageService;
import de.syscy.vertretungtoday.util.Util;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class MoodleDownloadTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoodleDownloadTask.class);

	private MoodleApi moodleApi;
	private MoodleResourceStorageService resourceStorageService;
	private MoodleFileUpdateEventPublisher updateEventPublisher;

	public MoodleDownloadTask(MoodleApi moodleApi, MoodleResourceStorageService resourceStorageService,
							  MoodleFileUpdateEventPublisher updateEventPublisher) {
		this.moodleApi = moodleApi;
		this.resourceStorageService = resourceStorageService;
		this.updateEventPublisher = updateEventPublisher;
	}

	// Wird alle 60 Minuten mit Hilfe der MoodleAPI Klasse alle Dateien herunterladen und mit Events weitergeben an Listener
	// um diese weiterzuverarbeiten (und so z.B. den Vertretungsplan zu aktualisieren)
	// (nur alle 60 Minuten fürs testen, später vllt. öfters, will aber auch aufpassen dass mein Verhalten, alle 60 Minuten alle Dateien
	// herunterzuladen, nicht allzu sehr auffällt, besonders bevor alles genau mit der Schulleitung abgeklärt ist. Werde ich wohl noch machen)
	//TODO: Changed for sending less requests while testing, default is every 20 min
	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void updateMoodleResources() {
		LOGGER.info("Updating moodle resources...");

		try {
			for(MoodleResourceInfo resourceInfo : moodleApi.getAllResources(MoodleApi.INFORMATION_COURSE_ID)) {
				try(Response response = moodleApi.fetchResource(resourceInfo)) {
					byte[] data = response.body().bytes();
					LocalDateTime modifiedDate = parseModifiedDate(resourceInfo, response, data);

					String mimeType = response.header("Content-Type", "application/octet-stream").split(";")[0].trim();
					MoodleResource resource = resourceStorageService.store(new MoodleResource(resourceInfo, mimeType, data, modifiedDate));

					updateEventPublisher.publishEvent(this, resource);
					LOGGER.info("Fetched " + resourceInfo.getUrl());
				} catch(Exception ex) {
					LOGGER.warn("Could not update file " + resourceInfo.getUrl(), ex);
				}
			}
		} catch(IOException | MoodleApiException ex) {
			LOGGER.warn("Error updating moodle resources", ex);
		}
	}

	private LocalDateTime parseModifiedDate(MoodleResourceInfo resourceInfo, Response response, byte[] data) {
		if(resourceInfo.getType() == MoodleResourceInfo.ResourceType.EMBEDDED_PAGE && resourceInfo.getUrl().contains("subst")) {
			String bodyString = new String(data, StandardCharsets.UTF_8);
			Document doc = Jsoup.parse(bodyString, resourceInfo.getUrl());

			Element modifiedDateElement = doc.selectFirst("td:contains(stand) > strong");
			String[] dateTimeSplit = modifiedDateElement.text().trim().split(" ", 2);
			LocalDate modifiedDate = Util.parseDate(dateTimeSplit[0].trim());

			return Util.parseTime(modifiedDate, dateTimeSplit[1].trim());
		} else {
			//For PDFs and other document.. maybe change this and compare the files to previous versions to check for changes?
			return LocalDateTime.now();
		}
	}
}