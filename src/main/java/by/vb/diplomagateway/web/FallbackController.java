package by.vb.diplomagateway.web;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/{service}")
    public ResponseEntity<ProblemDetail> fallback(@PathVariable String service) {
        log.warn("Fallback triggered for service '{}'", service);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service '%s' is temporarily unavailable. Please try again later.".formatted(service));

        problem.setTitle("Service Unavailable");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("service", service);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
    }
}
