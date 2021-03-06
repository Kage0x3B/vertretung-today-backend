package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.exception.EntityNotFoundException;
import de.syscy.vertretungtoday.model.MoodleResource;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import de.syscy.vertretungtoday.repository.MoodleResourceRepository;
import de.syscy.vertretungtoday.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

	private MoodleResourceRepository resourceRepository;

	public FileController(MoodleResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	@GetMapping("/list")
	public ResponseEntity<ApiResponse> listFiles() {
		return ApiResponse.ok(resourceRepository.findByTypeOrderByModifiedDate(MoodleResourceInfo.ResourceType.FILE).stream()
												.map(MoodleResource::toResourceInfo).collect(Collectors.toList())).create();
	}

	@GetMapping("/get/{resourceId}")
	public ResponseEntity<byte[]> getFile(@PathVariable("resourceId") int resourceId,
										  @RequestParam(value = "download", required = false, defaultValue = "false") boolean download) {
		MoodleResource resource = resourceRepository.findFirstByResourceId(resourceId)
													.orElseThrow(() -> new EntityNotFoundException("File not found"));

		byte[] data = resource.getData();
		ResponseEntity.BodyBuilder builder = ResponseEntity.ok().contentType(MediaType.parseMediaType(resource.getMimeType()))
														   .contentLength(data.length);

		if(download) {
			builder.header("Content-Disposition", "attachment; filename='" + resource.getFileName() + "'");
		}

		return builder.body(data);
	}
}