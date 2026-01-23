package pt.goncalorodrigues.integrationtests.testcontainers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {
    // static porque só pode haver 1 container do testcontainers em execução
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0");

        private static void startContainers() {
            Startables.deepStart(Stream.of(mysql)).join();
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testcontainers =
                    new MapPropertySource("testcontainers",
                            (Map) createConnectionConfiguration());

            environment.getPropertySources().addFirst(testcontainers);
        }

        private Map<String, String> createConnectionConfiguration() {// application.yml dinamicamente porque testcontainers gera db
            return Map.of(
                    "spring.datasource.url", mysql.getJdbcUrl(),
                    "spring.datasource.username", mysql.getUsername(),
                    "spring.datasource.password", mysql.getPassword()
            );
        }
    }
}
