package etl.stepdefs;

import etl.data.ScenarioLevel;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import static io.cucumber.java.Scenario.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.junit.Assume;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class Hooks {
    @Autowired
    ScenarioLevel scenarioLevel;

    @Before
    public void beforeScenario(Scenario scenario){
        System.out.println("*****************************************************************************************");
        System.out.println("	Scenario: "+scenario.getName());
        System.out.println("*****************************************************************************************");
        scenarioLevel.setForcePass(false);
    }

    @After
    public void afterScenario(Scenario scenario){
        System.out.println("*****************************************************************************************");
        System.out.println("	Scenario: "+scenario.getName());
        System.out.println("	Result:   "+scenario.getStatus());
        System.out.println("*****************************************************************************************");
    }

   @Given("I expect the scenario to fail")
   public void iExpectTheScenarioToFail() {
        scenarioLevel.setForcePass(true);
    }

}
