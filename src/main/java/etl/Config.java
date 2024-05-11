package etl;

import etl.data.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Configuration
@ComponentScans({})
public class Config {

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public SheetData getSheetData() {
        return new SheetData();
    }

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public APIData getAPIData() {
        return new APIData();
    }

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public FilePath getFilePath() {
        return new FilePath();
    }

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public ScenarioLevel getScenarioLevel() {
        return new ScenarioLevel();
    }

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public SQL getSQL() {return new SQL();}

}
