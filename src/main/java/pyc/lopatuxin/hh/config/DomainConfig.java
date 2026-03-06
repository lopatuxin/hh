package pyc.lopatuxin.hh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pyc.lopatuxin.hh.apply.domain.service.VacancyFilter;

@Configuration
public class DomainConfig {

    @Bean
    public VacancyFilter vacancyFilter() {
        return new VacancyFilter();
    }
}