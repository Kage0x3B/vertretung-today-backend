package de.syscy.vertretungtoday.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
public class SubstitutionEntry {
	private static final DateTimeFormatter DATE_IDENTIFIER_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

	@Id @Getter(value = AccessLevel.PRIVATE) private String id;

	private int grade;
	private String gradeAddition;
	private String gradeString;
	private String courseId;
	private int time;

	private String course;
	private String courseReplacement;
	private String teacher;
	private String teacherReplacement;
	private String room;
	private String roomReplacement;
	private SubstitutionType type;
	private String typeString;
	private String movedToNotice;
	private String movedFromNotice;
	private String notice;

	private LocalDate day;
	private LocalDateTime modifiedDate;

	public String getIdentifier() {
		return String.join("_", gradeString, courseId, String.valueOf(time), DATE_IDENTIFIER_FORMATTER.format(day));
	}

	public void updateIdentifier() {
		id = getIdentifier();
	}

	@PrePersist
	public void prePersist() {
		updateIdentifier();
	}

	@PreUpdate
	public void preUpdate() {
		updateIdentifier();
	}

	public SubstitutionEntry copy() {
		SubstitutionEntry entry = new SubstitutionEntry();
		entry.setGrade(grade);
		entry.setGradeAddition(gradeAddition);
		entry.setGradeString(gradeString);
		entry.setCourseId(courseId);
		entry.setTime(time);
		entry.setCourse(course);
		entry.setCourseReplacement(courseReplacement);
		entry.setTeacher(teacher);
		entry.setTeacherReplacement(teacherReplacement);
		entry.setRoom(room);
		entry.setRoomReplacement(roomReplacement);
		entry.setType(type);
		entry.setTypeString(typeString);
		entry.setMovedToNotice(movedToNotice);
		entry.setMovedFromNotice(movedFromNotice);
		entry.setNotice(notice);
		entry.setDay(day);
		entry.setModifiedDate(modifiedDate);

		return entry;
	}
}