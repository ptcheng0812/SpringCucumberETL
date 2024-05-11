package etl.data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class SQL {
    public String query;
    public List<Map<String, String>> result;

    public void setQuery(String query) {this.query = query;}

    public void setResult(List<Map<String, String>> result) {
        this.result = result;
    }
}
