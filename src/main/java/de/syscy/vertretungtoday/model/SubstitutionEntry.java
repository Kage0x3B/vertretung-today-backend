package de.syscy.vertretungtoday.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class SubstitutionEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

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

	private Date parsedTime;

	@PrePersist
	public void prePersist() {
		parsedTime = new Date();
	}
}