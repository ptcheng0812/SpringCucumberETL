package etl.configuration;

import etl.Config;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = Config.class)
public class CucumberSpringConfiguration {
}
