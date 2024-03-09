package etl.utilities;

import etl.data.ScenarioLevel;
import io.cucumber.java.Scenario;
import org.apache.groovy.json.internal.IO;
import org.junit.Assume;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ExpectedToFailException extends IOException {
    @Autowired
    ScenarioLevel scenarioLevel;
    IOException IOException;
//    private final boolean recoverable;

    public ExpectedToFailException(ScenarioLevel scenarioLevel, IOException IOException) {
//        super("Expected to fail");
        if (scenarioLevel.ForcePass) {
            Assume.assumeFalse(true);
            System.out.println("----------Expected to fail---------------");
//            System.out.println(IOException.getMessage());

        } else {
            System.out.println("----------Error: Throwing IO Exception---------------");
            System.out.println(IOException.getMessage());

            return;
        }
    }

    public boolean isRecoverable() {
        return scenarioLevel.ForcePass;
    }
}
