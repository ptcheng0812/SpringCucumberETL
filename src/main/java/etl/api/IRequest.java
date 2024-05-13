package etl.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IRequest {
    Response extractResponseContent();
    Integer checkResponseStatusCode();
    Map<String, String> checkResponseHeaders();
    ArrayNode extractJSONArrayData(String nodeName);
}
