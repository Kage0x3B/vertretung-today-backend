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

@Data
@Entity
public class SubstitutionEntry {
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
		return String.join("_", gradeString, courseId, String.valueOf(time));
	}

	@PrePersist
	public void prePersist() {
		id = getIdentifier();
	}

	@PreUpdate
	public void preUpdate() {
		id = getIdentifier();
	}

	public SubstitutionEntry copy() {
		SubstitutionEntry entry = new SubstitutionEntry();
		entry.setGrade(grade);
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