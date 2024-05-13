package etl.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import etl.utilities.MethodHelper;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GetRequestHandler implements IRequest {
    private String apiEndpoint;
    private Map<String, String> _header;
    private Map<String, String> _param;

    private Secret _secret;

    public GetRequestHandler(String apiEndpoint, Map<String, String> header, Map<String, String> param, Secret secret) {
        this.apiEndpoint = apiEndpoint;
        this._header = header;
        this._param = param;
        this._secret = secret;
    }
    @Override
    public Response extractResponseContent() {
        RequestSpecification request = RestAssured.given().baseUri(apiEndpoint);

        if(!_header.isEmpty()) {
            request.headers(_header);
        }

        if (!_param.isEmpty()) {
            request.queryParams(_param);
        }

        // Determine authentication method based on Secret
        switch (_secret.getTypeOfAuth()) {
            case "Basic":
                request.auth().basic(_secret.getBasicUsername(), _secret.getBasicPassword());
                break;
            case "OAuth1":
                request.auth().oauth(
                        _secret.getOAuth1ConsumerKey(),
                        _secret.getOAuth1ConsumerSecret(),
                        _secret.getOAuth1Token(),
                        _secret.getOAuth1TokenSecret()
                );
                break;
            case "OAuth2":
                request.auth().oauth2(_secret.getOAuth2Token());
                break;
            case "JWT":
                request.header("Authorization", "Bearer " + _secret.getJwtToken());
                break;
            case "":
                break;
            default:
                // Handle other authentication methods or throw an exception for unsupported methods
                throw new IllegalArgumentException("Unsupported authentication method: " + _secret.getTypeOfAuth());
        }
        // Send the GET request
        return request.get();
    }

    @Override
    public Integer checkResponseStatusCode() {
        Response response =  extractResponseContent();
        return response.statusCode();
    }

    @Override
    public Map<String, String> checkResponseHeaders() {
        Map<String, String> headersMap = new HashMap<>();
        Response response =  extractResponseContent();
        Headers responseHeaders = response.getHeaders();
        responseHeaders.asList().forEach(header -> {
            String headerName = header.getName();
            String headerValues = header.getValue();
            headersMap.put(headerName, headerValues);
        });
        return headersMap;
    }

    @Override
    public ArrayNode extractJSONArrayData(String nodeName) {
        Response response = (Response) extractResponseContent().getBody();
        return MethodHelper.GetAPIResponseAndTurnIntoNode(response, nodeName);

    }
}
