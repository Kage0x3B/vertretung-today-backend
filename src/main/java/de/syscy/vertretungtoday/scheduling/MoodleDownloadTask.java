package de.syscy.vertretungtoday.scheduling;

import de.syscy.vertretungtoday.event.MoodleFileUpdateEventPublisher;
import de.syscy.vertretungtoday.model.MoodleResource;
import de.syscy.vertretungtoday.moodle.MoodleApi;
import de.syscy.vertretungtoday.moodle.MoodleApiException;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import de.syscy.vertretungtoday.service.MoodleResourceStorageService;
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
import java.util.*;

@Component
public class MoodleDownloadTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoodleDownloadTask.class);

	private MoodleApi moodleApi;
	private MoodleResourceStorageService resourceStorageService;
	private MoodleFileUpdateEventPublisher updateEventPublisher;

	public MoodleDownloadTask(MoodleApi moodleApi, MoodleResourceStorageService resourceStorageService, MoodleFileUpdateEventPublisher updateEventPublisher) {
		this.moodleApi = moodleApi;
		this.resourceStorageService = resourceStorageService;
		this.updateEventPublisher = updateEventPublisher;
	}

	@Scheduled(fixedDelay = 10 * 60 * 1000)
	public void updateMoodleResources() {
		LOGGER.info("Updating moodle resources...");

		try {
			List<MoodleResourceInfo> updatedResourceList = new ArrayList<>();

			for(MoodleResourceInfo resourceInfo : moodleApi.getAllResources(MoodleApi.INFORMATION_COURSE_ID)) {
				try(Response response = moodleApi.fetchResource(resourceInfo)) {
					byte[] data = response.body().bytes();
					Date modifiedDate = parseModifiedDate(resourceInfo, response, data);

					resourceStorageService.store(new MoodleResource(resourceInfo, data, modifiedDate));

					updatedResourceList.add(resourceInfo);
					LOGGER.info("Fetched " + resourceInfo.getUrl());
				} catch(Exception ex) {
					LOGGER.warn("Could not update file " + resourceInfo.getUrl(), ex);
				}
			}

			updateEventPublisher.publishEvent(this, updatedResourceList);
		} catch(IOException | MoodleApiException ex) {
			LOGGER.warn("Error updating moodle resources", ex);
		}
	}

	private Date parseModifiedDate(MoodleResourceInfo resourceInfo, Response response, byte[] data) {
		if(resourceInfo.getType() == MoodleResourceInfo.ResourceType.EMBEDDED_PAGE && resourceInfo.getUrl().contains("subst")) {
			String bodyString = new String(data, StandardCharsets.UTF_8);
			Document doc = Jsoup.parse(bodyString, resourceInfo.getUrl());

			/*Element dateElement = doc.selectFirst("http-equiv > center > div.mon_title");
			String dateString = dateElement.text().trim().split(" ")[0];
			Calendar date = parseDate(dateString);*/

			Element modifiedDateElement = doc.selectFirst("td:contains(stand) > strong");
			String[] dateTimeSplit = modifiedDateElement.text().trim().split(" ", 2);
			Calendar modifiedDate = parseDate(dateTimeSplit[0].trim());
			Calendar modifiedDateTime = parseTime(modifiedDate, dateTimeSplit[1].trim());

			return modifiedDateTime.getTime();
		} else {
			//For PDFs and other document.. maybe change this and compare the files to previous versions to check for changes?
			return new Date();
		}
	}

	private Calendar parseDate(String dateString) {
		String[] dateSplit = dateString.split("\\.");

		int day = Integer.parseInt(dateSplit[0]);
		int month = Integer.parseInt(dateSplit[1]);
		int year = Integer.parseInt(dateSplit[2]);

		return new GregorianCalendar(year, month, day);
	}

	private Calendar parseTime(Calendar baseDate, String timeString) {
		String[] timeSplit = timeString.split(":");

		int hour = Integer.parseInt(timeSplit[0]);
		int minute = Integer.parseInt(timeSplit[1]);

		return new GregorianCalendar(baseDate.get(Calendar.YEAR), baseDate.get(Calendar.MONTH), baseDate
				.get(Calendar.DAY_OF_MONTH), hour, minute);
	}
}