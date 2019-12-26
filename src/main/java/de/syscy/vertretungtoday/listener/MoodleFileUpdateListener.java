package de.syscy.vertretungtoday.listener;

import de.syscy.vertretungtoday.event.MoodleFileUpdateEvent;
import de.syscy.vertretungtoday.event.SubstitutionPlanUpdateEventPublisher;
import de.syscy.vertretungtoday.model.*;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import de.syscy.vertretungtoday.util.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MoodleFileUpdateListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoodleFileUpdateListener.class);

	//In lowercase and without any special characters (-> [^a-z0-9_-])
	private static final String[] EXPECTED_TABLE_STRUCTURE = new String[] {
			"klasse", "kurs", "std", "fach", "lehr", "raum", "art", "auf", "von", "bem"
	};
	private static final int EXPECTED_COLUMNS = EXPECTED_TABLE_STRUCTURE.length;

	private SubstitutionPlanUpdateEventPublisher updateEventPublisher;

	public MoodleFileUpdateListener(SubstitutionPlanUpdateEventPublisher updateEventPublisher) {
		this.updateEventPublisher = updateEventPublisher;
	}

	@Async
	@EventListener
	public void onMoodleFileUpdate(MoodleFileUpdateEvent event) {
		if(event.getResource().getType() == MoodleResourceInfo.ResourceType.EMBEDDED_PAGE && event.getResource().getUrl()
																								  .contains("subst")) {
			LOGGER.info("Parsing substitution info from " + event.getResource().getFileName());
			Document document = Jsoup.parse(new String(event.getResource().getData(), StandardCharsets.UTF_8));

			try {
				MoodleSubstitutionPlan substitutionPlan = parseSubstitutionPlan(event.getResource(), document);
				LOGGER.info("Found " + substitutionPlan.getSubstitutionEntries().size() + " substitution entries");
				updateEventPublisher.publishEvent(this, substitutionPlan);
			} catch(Exception ex) {
				LOGGER.warn("Could not parse substitution plan \"" + event.getResource().getUrl() + "\"", ex);
			}
		}
	}

	private MoodleSubstitutionPlan parseSubstitutionPlan(MoodleResource resource, Document document) {
		MoodleSubstitutionPlan substitutionPlan = new MoodleSubstitutionPlan();

		LocalDate date = parseDate(document);
		substitutionPlan.setDate(date);
		substitutionPlan.setModifiedTime(resource.getModifiedDate());
		substitutionPlan.setMessageOfTheDay(parseMOTD(document, date));

		List<SubstitutionEntry> substitutionEntries = readSubstitutionEntries(extractSubstitutionEntries(document), date, resource
				.getModifiedDate());
		substitutionPlan.setSubstitutionEntries(substitutionEntries);

		return substitutionPlan;
	}

	private LocalDate parseDate(Document document) {
		Element dateElement = document.selectFirst("div.mon_title");
		String dateString = dateElement.text().trim().split(" ")[0];

		return Util.parseDate(dateString);
	}

	private MoodleMessageOfTheDay parseMOTD(Document document, LocalDate date) {
		MoodleMessageOfTheDay motd = new MoodleMessageOfTheDay();

		Element motdElement = document.selectFirst("table.info > tbody > tr > td");
		motd.setMessage(motdElement.html());
		motd.setDate(date);

		return motd;
	}

	private List<SubstitutionEntry> readSubstitutionEntries(List<RawSubstitutionEntry> rawSubstitutionEntries, LocalDate day,
															LocalDateTime modifiedDate) {
		List<SubstitutionEntry> substitutionEntries = new ArrayList<>();

		for(RawSubstitutionEntry rawEntry : rawSubstitutionEntries) {
			SubstitutionEntry entry = new SubstitutionEntry();

			int grade = Integer.parseInt(rawEntry.getGrade().replaceAll("[^0-9]", ""));
			String gradeAddition = rawEntry.getGrade().replaceAll("[^A-Za-z_]", "");
			entry.setGrade(grade);
			entry.setGradeAddition(gradeAddition);
			entry.setGradeString(rawEntry.getGrade());
			entry.setCourseId(rawEntry.getCourseId());

			entry.setType(SubstitutionType.parseType(rawEntry.getType()));
			entry.setTypeString(rawEntry.getType());

			String[] course = parseReplacement(rawEntry.getCourse());
			entry.setCourse(course[0]);
			entry.setCourseReplacement(course[1]);

			String[] teacher = parseReplacement(rawEntry.getTeacher());
			entry.setTeacher(teacher[0]);
			entry.setTeacherReplacement(teacher[1]);

			String[] room = parseReplacement(rawEntry.getRoom());
			entry.setRoom(room[0]);
			entry.setRoomReplacement(room[1]);

			entry.setMovedToNotice(rawEntry.getMovedToNotice());
			entry.setMovedFromNotice(rawEntry.getMovedFromNotice());
			entry.setNotice(rawEntry.getNotice());

			entry.setDay(day);
			entry.setModifiedDate(modifiedDate);

			substitutionEntries.addAll(cloneEntries(entry, rawEntry.getTime()));
		}

		return substitutionEntries;
	}

	private List<SubstitutionEntry> cloneEntries(SubstitutionEntry entry, String timeString) {
		try {
			timeString = Util.cleanString(timeString);

			if(timeString.contains("-")) {
				String[] timeSplit = timeString.split("-", 2);
				int startTime = parseTime(timeSplit[0]);
				int endTime = parseTime(timeSplit[1]);

				List<SubstitutionEntry> entryCopies = new ArrayList<>(endTime - startTime);

				for(int time = startTime; time <= endTime; time++) {
					SubstitutionEntry copy = entry.copy();
					copy.setTime(time);
					entryCopies.add(copy);
				}

				return entryCopies;
			} else {
				entry.setTime(parseTime(timeString));
				return Collections.singletonList(entry);
			}
		} catch(Exception ex) {
			LOGGER.warn("Could not parse time string \"" + timeString + "\"", ex);

			entry.setTime(-1);
			return Collections.singletonList(entry);
		}
	}

	private int parseTime(String time) {
		if(time.equals("mp")) { // "Mittagspause", lunch break
			return 11;
		}

		return Integer.parseInt(time);
	}

	private String[] parseReplacement(String text) {
		if(text.contains("-")) {
			return new String[] { "-", "-" };
		}

		if(text.contains("→")) {
			return text.split("→", 2);
		}

		return new String[] { text, "-" };
	}

	private List<RawSubstitutionEntry> extractSubstitutionEntries(Document document) {
		List<RawSubstitutionEntry> rawSubstitutionEntries = new ArrayList<>();

		checkTableStructure(document);

		Elements tableBodyElements = document.select("table.mon_list > tbody > tr:has(td)");
		LOGGER.debug("Selected " + tableBodyElements.size() + " table body elements");
		for(Element tableRow : tableBodyElements) {
			int index = 0;
			String[] columnContents = new String[EXPECTED_COLUMNS];

			for(Element tableData : tableRow.getElementsByTag("td")) {
				if(index >= EXPECTED_COLUMNS) {
					break;
				}

				columnContents[index] = tableData.text();
				index++;
			}

			RawSubstitutionEntry substitutionEntry = new RawSubstitutionEntry();
			substitutionEntry.setGrade(columnContents[0]);
			substitutionEntry.setCourseId(columnContents[1]);
			substitutionEntry.setTime(columnContents[2]);
			substitutionEntry.setCourse(columnContents[3]);
			substitutionEntry.setTeacher(columnContents[4]);
			substitutionEntry.setRoom(columnContents[5]);
			substitutionEntry.setType(columnContents[6]);
			substitutionEntry.setMovedToNotice(columnContents[7]);
			substitutionEntry.setMovedFromNotice(columnContents[8]);
			substitutionEntry.setNotice(columnContents[9]);

			rawSubstitutionEntries.add(substitutionEntry);
		}

		LOGGER.debug("Extracted " + rawSubstitutionEntries.size() + " substitution entries");

		return rawSubstitutionEntries;
	}

	private void checkTableStructure(Document document) {
		boolean problemFound = false;

		//The second row
		Elements tableHeaderRowElements = document.select("table.mon_list > tbody > tr:has(th)");
		LOGGER.debug("Selected " + tableHeaderRowElements.size() + " table header row elements");
		for(Element tableRow : tableHeaderRowElements) {
			if(tableRow.getElementsByTag("th").first().text().trim().isEmpty()) {
				//Wrong row, next one
				continue;
			}

			int index = 0;

			for(Element tableHeader : tableRow.getElementsByTag("th")) {
				String headerText = Util.cleanString(tableHeader.text());

				if(!headerText.contains(EXPECTED_TABLE_STRUCTURE[index])) {
					LOGGER.warn("Substitution table structure unexpected! Expected header containing \"" + EXPECTED_TABLE_STRUCTURE[index] + "\", got \"" + tableHeader
							.text() + "\".");
					problemFound = true;
				}

				index++;
			}
		}

		if(!problemFound) {
			LOGGER.debug("Found valid table structure");
		}
	}
}