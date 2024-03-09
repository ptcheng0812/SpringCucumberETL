package etl.data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class FilePath {
    public String path = "";
    public String master_path = "";
    public String test_path = "";

    public String output_path = "";

    public void setPath(String path) {
        this.path = path;
    }

    public void setMaster_path(String master_path) {
        this.master_path = master_path;
    }

    public void setTest_path(String test_path) {
        this.test_path = test_path;
    }

    public void setOutput_path(String output_path) { this.output_path = output_path; }
}
