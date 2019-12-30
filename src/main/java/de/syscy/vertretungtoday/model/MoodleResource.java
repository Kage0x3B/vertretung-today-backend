package de.syscy.vertretungtoday.model;

import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import de.syscy.vertretungtoday.util.Util;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class MoodleResource {
	@Id private int resourceId;
	private MoodleResourceInfo.ResourceType type;
	private String url;
	private String fileName;
	private String mimeType;
	@Lob @Basic(fetch = FetchType.LAZY) private byte[] data;

	private LocalDateTime modifiedDate;
	private LocalDateTime entryUpdated;

	public MoodleResource() {

	}

	public MoodleResource(MoodleResourceInfo resourceInfo, String mimeType, byte[] data, LocalDateTime modifiedDate) {
		this.resourceId = resourceInfo.getId();
		this.type = resourceInfo.getType();
		this.url = resourceInfo.getUrl();
		this.fileName = Util.extractFilename(resourceInfo.getUrl());
		this.mimeType = mimeType;
		this.data = data;
		this.modifiedDate = modifiedDate;
	}

	@PrePersist
	public void prePersist() {
		entryUpdated = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		entryUpdated = LocalDateTime.now();
	}

	public MoodleResourceInfo toResourceInfo() {
		return new MoodleResourceInfo(resourceId, type, url, fileName, mimeType);
	}
}