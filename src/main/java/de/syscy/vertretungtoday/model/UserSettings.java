package de.syscy.vertretungtoday.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Data
@Entity
public class UserSettings {
	@Id private String username;
	private String settingsData;

	private LocalDateTime entryUpdated;

	public UserSettings() {

	}

	public UserSettings(String username) {
		this.username = username;
	}

	@PrePersist
	public void prePersist() {
		entryUpdated = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		entryUpdated = LocalDateTime.now();
	}
}