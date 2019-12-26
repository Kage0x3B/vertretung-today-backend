package de.syscy.vertretungtoday.model;

import lombok.Data;

@Data
public class RawSubstitutionEntry {
	private String grade;
	private String courseId;
	private String time;
	private String course;
	private String teacher;
	private String room;
	private String type;
	private String movedToNotice;
	private String movedFromNotice;
	private String notice;
}