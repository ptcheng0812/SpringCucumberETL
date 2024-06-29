package etl.restful;

import com.fasterxml.jackson.databind.node.ArrayNode;
import etl.data.Secret;
import etl.utilities.MethodHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static etl.utilities.MethodHelper.*;

public class PostRequestHandler implements IRequest{
    private String apiEndpoint;
    private Map<String, String> _header;
    private Map<String, String> _param;
    private Secret _secret;
    private String _body;

    public PostRequestHandler(String apiEndpoint, Map<String, String> header, Map<String, String> param, Secret secret, String body) {
        this.apiEndpoint = apiEndpoint;
        this._header = header;
        this._param = param;
        this._secret = secret;
        this._body = body;
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
        if(_secret.getTypeOfAuth() != null && !_secret.getTypeOfAuth().isEmpty()) {
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
        }

        //Logic for body
        if (_body != "") {
            // Determine the content type based on the requestBody
            ContentType contentType = ContentType.ANY;
            if (isJson(_body)) {
                contentType = ContentType.JSON;
            } else if (isXml(_body)) {
                contentType = ContentType.XML;
            } else if (isFilePath(_body)) {
                contentType = ContentType.ANY; // Content type for multi-part request
            } else {
                contentType = ContentType.URLENC; // Default to form URL-encoded
            }
            request.contentType(contentType).body(_body);
        }
        // Send the POST request
        return request.post();
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
