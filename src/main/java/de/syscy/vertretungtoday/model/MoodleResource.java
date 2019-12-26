package de.syscy.vertretungtoday.model;

import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import lombok.Data;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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
	private LocalDateTime entryCreated;

	public MoodleResource() {

	}

	public MoodleResource(MoodleResourceInfo resourceInfo, String mimeType, byte[] data, LocalDateTime modifiedDate) {
		this.resourceId = resourceInfo.getId();
		this.type = resourceInfo.getType();
		this.url = resourceInfo.getUrl();
		this.fileName = extractFilename(resourceInfo.getUrl());
		this.mimeType = mimeType;
		this.data = data;
		this.modifiedDate = modifiedDate;
	}

	private static String extractFilename(String url) {
		try {
			//This is a bit more intelligent, it also removes query parameters
			//While it should work every time, I provided a trivial fallback which just takes the url part after the last '/'
			return Paths.get(new URI(url).getPath()).getFileName().toString();
		} catch(URISyntaxException ex) {
			ex.printStackTrace();

			return url.substring(url.lastIndexOf('/') + 1);
		}
	}

	@PrePersist
	public void prePersist() {
		entryCreated = LocalDateTime.now();
	}

	public MoodleResourceInfo toResourceInfo() {
		return new MoodleResourceInfo(resourceId, type, url);
	}
}