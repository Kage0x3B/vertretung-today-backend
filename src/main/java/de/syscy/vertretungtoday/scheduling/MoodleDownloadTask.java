package de.syscy.vertretungtoday.scheduling;

import de.syscy.vertretungtoday.moodle.MoodleApi;
import de.syscy.vertretungtoday.moodle.MoodleApiException;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class MoodleDownloadTask {
	private MoodleApi moodleApi;

	public MoodleDownloadTask(MoodleApi moodleApi) {
		this.moodleApi = moodleApi;
	}

	@Scheduled(fixedDelay = 10 * 60 * 1000)
	public void updateSubstitutionPlans() {
		try {
			for(MoodleResourceInfo resourceInfo : moodleApi.getAllResources(MoodleApi.INFORMATION_COURSE_ID)) {
				if(resourceInfo.getType() == MoodleResourceInfo.ResourceType.EMBEDDED_PAGE && resourceInfo.getUrl().contains("subst")) {
					try {
						Response response = moodleApi.fetchResource(resourceInfo);
						Document doc = Jsoup.parse(response.body().string(), resourceInfo.getUrl());
						response.body().close();

						Element dateElement = doc.selectFirst("http-equiv > center > div.mon_title");
						String dateString = dateElement.text().trim().split(" ")[0];
						Calendar date = parseDate(dateString);

						Element modifiedDateElement = doc.selectFirst("td:contains(stand) > strong");
						String[] dateTimeSplit = modifiedDateElement.text().trim().split(" ", 2);
						Calendar modifiedDate = parseDate(dateTimeSplit[0].trim());
						Calendar modifiedDateTime = parseTime(modifiedDate, dateTimeSplit[1].trim());
					} catch(IOException | MoodleApiException ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch(IOException | MoodleApiException ex) {
			ex.printStackTrace();
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

		return new GregorianCalendar(baseDate.get(Calendar.YEAR), baseDate.get(Calendar.MONTH), baseDate.get(Calendar.DAY_OF_MONTH), hour, minute);
	}
}