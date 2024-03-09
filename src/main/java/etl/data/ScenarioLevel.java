package etl.data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ScenarioLevel {
    public boolean ForcePass;
    public void setForcePass(boolean forcePass) {this.ForcePass = forcePass;}
}
