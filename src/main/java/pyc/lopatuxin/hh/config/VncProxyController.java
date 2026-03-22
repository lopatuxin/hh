package pyc.lopatuxin.hh.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
public class VncProxyController {

    private static final String VNC_TARGET = "http://localhost:6080";
    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/vnc/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) {
        String path = request.getRequestURI().replaceFirst("/vnc", "");
        String query = request.getQueryString();
        String url = VNC_TARGET + path + (query != null ? "?" + query : "");

        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames())
                .forEach(h -> headers.addAll(h, Collections.list(request.getHeaders(h))));

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url, HttpMethod.valueOf(request.getMethod()),
                new HttpEntity<>(headers), byte[].class);

        return response;
    }
}
