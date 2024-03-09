package etl.data;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class APIData {
    public String endpoint;
    public String node;

    public ArrayNode dataNode;

    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public void setNode(String node) {this.node = node;}

    public void setDataNode(ArrayNode dataNode) {this.dataNode = dataNode;}
}
