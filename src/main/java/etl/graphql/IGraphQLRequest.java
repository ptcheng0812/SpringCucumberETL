package etl.graphql;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.restassured.response.Response;

import java.util.Map;

public interface IGraphQLRequest {
    Response extractResponseContent();
    Integer checkResponseStatusCode();
    Map<String, String> checkResponseHeaders();
    ArrayNode extractJSONArrayData(String nodeName);
}
