package de.syscy.vertretungtoday.model;

import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import lombok.Data;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Date;

@Data
@Entity
public class MoodleResource {
	@Id @GeneratedValue(strategy = GenerationType.AUTO) private Integer id;

	private Integer resourceId;
	private MoodleResourceInfo.ResourceType type;
	private String url;
	private String fileName;
	@Lob @Basic(fetch = FetchType.LAZY) private byte[] data;

	private Date modifiedDate;
	private Date entryCreated;

	public MoodleResource() {

	}

	public MoodleResource(MoodleResourceInfo resourceInfo, byte[] data, Date modifiedDate) {
		this.resourceId = resourceInfo.getId();
		this.type = resourceInfo.getType();
		this.url = resourceInfo.getUrl();
		this.fileName = extractFilename(resourceInfo.getUrl());
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
		entryCreated = new Date();
	}
}