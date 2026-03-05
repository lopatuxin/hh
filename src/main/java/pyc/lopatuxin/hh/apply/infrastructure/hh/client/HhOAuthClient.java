package pyc.lopatuxin.hh.apply.infrastructure.hh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pyc.lopatuxin.hh.apply.infrastructure.hh.dto.OAuthResponse;

import java.util.Map;

@FeignClient(name = "hh-oauth", url = "https://hh.ru")
public interface HhOAuthClient {

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    OAuthResponse refreshToken(@RequestParam Map<String, String> params);
}