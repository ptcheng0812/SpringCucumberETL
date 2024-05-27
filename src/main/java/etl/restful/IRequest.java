package etl.restful;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.restassured.response.Response;

import java.util.Map;

public interface IRequest {
    Response extractResponseContent();
    Integer checkResponseStatusCode();
    Map<String, String> checkResponseHeaders();
    ArrayNode extractJSONArrayData(String nodeName);
}
