package com.cucumbertestrunner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"etl.stepdefs", "etl.configuration"},
        plugin = {"pretty", "html:target/cucumber-html-report.html",
                "json:target/cucumber.json",
//                "rerun:target/cucumber-api-rerun.txt"
        }

)

public class CucumberIntegrationTestRunner {
}
