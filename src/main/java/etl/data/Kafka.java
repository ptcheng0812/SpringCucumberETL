package etl.data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class Kafka {
    public Map<String, String> props = new HashMap<>();

    public void setProps(String key, String value) {props.put(key, value);}
}
