package by.vb.diplomagateway.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

	@RequestMapping("/{service}")
	public ResponseEntity<String> fallback(@PathVariable String service) {
		log.warn("Fallback triggered for service '{}'", service);

		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("Service '%s' is temporarily unavailable. Please try again later.".formatted(
						service));
	}
}
