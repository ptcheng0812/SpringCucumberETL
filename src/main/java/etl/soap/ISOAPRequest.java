package etl.soap;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.Map;

public interface ISOAPRequest {
    Response extractResponseContent() throws IOException;
    Integer checkResponseStatusCode() throws IOException;
    Map<String, String> checkResponseHeaders() throws IOException;
    ArrayNode extractJSONArrayData(String nodeName) throws IOException;
}
