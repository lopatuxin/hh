package pyc.lopatuxin.hh.apply.infrastructure.hh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import pyc.lopatuxin.hh.apply.infrastructure.hh.dto.HhVacanciesPage;

import java.util.Map;

@FeignClient(name = "hh-api", url = "${hh.api.base-url}")
public interface HhApiClient {

    @GetMapping("/vacancies")
    HhVacanciesPage searchVacancies(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("text") String text,
            @RequestParam("area") int area,
            @RequestParam("salary") int salary,
            @RequestParam("currency") String currency,
            @RequestParam("experience") String experience,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page
    );

    @PostMapping(value = "/negotiations", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void apply(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Map<String, String> params
    );
}