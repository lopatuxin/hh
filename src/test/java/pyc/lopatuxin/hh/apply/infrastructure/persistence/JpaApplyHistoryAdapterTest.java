package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JpaApplyHistoryAdapterTest.TestConfig.class)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@Transactional
class JpaApplyHistoryAdapterTest {

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(
            basePackages = "pyc.lopatuxin.hh.apply.infrastructure.persistence",
            excludeFilters = @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = InMemoryApplyHistoryAdapter.class
            )
    )
    static class TestConfig {
    }

    @Autowired
    private JpaApplyHistoryAdapter adapter;

    @Test
    void новаяВакансияНеСчитаетсяОткликнутой() {
        assertThat(adapter.isApplied("vacancy-123")).isFalse();
    }

    @Test
    void послеМаркировкиВакансияСчитаетсяОткликнутой() {
        adapter.markApplied("vacancy-456");
        assertThat(adapter.isApplied("vacancy-456")).isTrue();
    }

    @Test
    void повторнаяМаркировкаНеВыбрасываетИсключение() {
        adapter.markApplied("vacancy-789");
        adapter.markApplied("vacancy-789"); // не должно падать
        assertThat(adapter.isApplied("vacancy-789")).isTrue();
    }
}
