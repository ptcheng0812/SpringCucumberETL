package etl.stepdefs;

import com.fasterxml.jackson.databind.node.ArrayNode;
import etl.graphql.FetchRequestHandler;
import etl.restful.GetRequestHandler;
import etl.restful.PostRequestHandler;
import etl.restful.PutRequestHandler;
import etl.data.Secret;
import etl.data.APIData;
import etl.soap.SOAPRequestHandler;
import etl.utilities.MethodHelper;
import etl.websocket.WebSocketHandler;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.java_websocket.client.WebSocketClient;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;

public class APIStepDef {
    @Autowired
    APIData apiData;

    WebSocketHandler webSocketHandler;

    @Given("the API endpoint is {string} and node is {string}")
    public void theAPIEndpointIsAndNodeIs(String arg0, String arg1) {
        if(!arg0.isEmpty()) {
            apiData.setEndpoint(arg0);
            apiData.setNode(arg1);
        } else {
            Assert.fail("Please provide valid endpoint");
        }
    }

    @When("I fetch the api endpoint and extract data from node")
    @When("I fetch the api endpoint and extract data to node")
    public void iFetchTheApiEndpointAndExtractDataFromNode() {
        Response response = (Response) given().when().get(apiData.endpoint).getBody();
        ArrayNode arrayNode = MethodHelper.GetAPIResponseAndTurnIntoNode(response, apiData.node);
        if(!arrayNode.isEmpty()) {
            apiData.setDataNode(arrayNode);
        } else {
            Assert.fail("This node from response data is empty. Please ensure a valid response return");
        }
    }

    @When("I fetch the restful api endpoint by {string} and extract data from node with the following params")
    @When("I fetch the restful api endpoint by {string} and extract data from node using the following params")
    @When("I fetch the restful api endpoint by {string} and extract data from node")
    public void iFetchTheRestfulApiEndpointByAndExtractDataFromNodeWithTheFollowingParams(String arg0, DataTable table) throws ExecutionException, InterruptedException {
        Map<String, String> param_table = table.asMap();
//        System.out.print(param_table);

        // Use the builder to create a Secret object dynamically
        Secret.SecretBuilder secretBuilder = Secret.builder();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        String body = "";

        for (Map.Entry<String, String> entry : param_table.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Check if the key contains "secret_"
            if (key.startsWith("secret_")) {
                // Extract the field name after "secret_"
                String fieldName = key.substring("secret_".length());
                // Use reflection to set the field value in the builder
                try {
                    secretBuilder.getClass().getMethod(fieldName, String.class).invoke(secretBuilder, value);
                } catch (ReflectiveOperationException e) {
                    // Handle exception (e.g., method not found)
                    e.printStackTrace();
                }
            }

            // Check if the key contains "header_"
            if (key.startsWith("header_")) {
                // Extract the header field name after "header_"
                String headerFieldName = key.substring("header_".length());
                // Add the header field and value to the header map
                headers.put(headerFieldName, value);
            }

            if (key.startsWith("param_")) {
                // Extract the header field name after "header_"
                String headerFieldName = key.substring("param_".length());
                // Add the header field and value to the header map
                params.put(headerFieldName, value);
            }

            if(key.startsWith("body")) {
                body = value;
            }
        }
        // Build the Secret object
        Secret secret = secretBuilder.build();

        switch (arg0){
            case "GET":
                GetRequestHandler getRequestHandler = new GetRequestHandler(apiData.endpoint, headers, params, secret);
                Response response = getRequestHandler.extractResponseContent();
                ArrayNode arrayNode = getRequestHandler.extractJSONArrayData(apiData.node);
                if(!arrayNode.isEmpty()) {
                    apiData.setDataNode(arrayNode);
                    System.out.println("arr: " + apiData.dataNode);
                } else {
                    Assert.fail("This node from Get response data is empty. Please ensure a valid response return");
                }
                break;
            case "POST":
                PostRequestHandler postRequestHandler = new PostRequestHandler(apiData.endpoint, headers, params, secret, body);
                Response postResponse = postRequestHandler.extractResponseContent();
                ArrayNode arrayNode1 = postRequestHandler.extractJSONArrayData(apiData.node);
                if(!arrayNode1.isEmpty()) {
                    apiData.setDataNode(arrayNode1);
                    System.out.println("arr: " + apiData.dataNode);
                } else {
                    Assert.fail("This node from Post response data is empty. Please ensure a valid response return");
                }
                break;
            case "PUT":
                PutRequestHandler putRequestHandler = new PutRequestHandler(apiData.endpoint, headers, params, secret, body);
                Response putResponse = putRequestHandler.extractResponseContent();
                ArrayNode arrayNode2 = putRequestHandler.extractJSONArrayData(apiData.node);
                if(!arrayNode2.isEmpty()) {
                    apiData.setDataNode(arrayNode2);
                    System.out.println("arr: " + apiData.dataNode);
                } else {
                    Assert.fail("This node from Put response data is empty. Please ensure a valid response return");
                }
                break;
        }
    }

    @When("I fetch the graphql api endpoint and extract data from node using the following params")
    @When("I fetch the graphql api endpoint and extract data from node with the following params")
    public void iFetchTheGraphqlApiEndpointAndExtractDataFromNodeUsingTheFollowingParams(DataTable table) {
        Map<String, String> param_table = table.asMap();

        // Use the builder to create a Secret object dynamically
        Secret.SecretBuilder secretBuilder = Secret.builder();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        String query = "";
        String variable = "";

        for (Map.Entry<String, String> entry : param_table.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Check if the key contains "secret_"
            if (key.startsWith("secret_")) {
                // Extract the field name after "secret_"
                String fieldName = key.substring("secret_".length());
                // Use reflection to set the field value in the builder
                try {
                    secretBuilder.getClass().getMethod(fieldName, String.class).invoke(secretBuilder, value);
                } catch (ReflectiveOperationException e) {
                    // Handle exception (e.g., method not found)
                    e.printStackTrace();
                }
            }

            // Check if the key contains "header_"
            if (key.startsWith("header_")) {
                // Extract the header field name after "header_"
                String headerFieldName = key.substring("header_".length());
                // Add the header field and value to the header map
                headers.put(headerFieldName, value);
            }

            if (key.startsWith("param_")) {
                // Extract the header field name after "header_"
                String headerFieldName = key.substring("param_".length());
                // Add the header field and value to the header map
                params.put(headerFieldName, value);
            }

            if(key.startsWith("query") || key.startsWith("mutation")) {
                query = value;
            }

            if(key.startsWith("variables") || key.startsWith("variable")) {
                variable = value;
            }
        }
        // Build the Secret object
        Secret secret = secretBuilder.build();
        String full_query = String.format("{ \"query\": \"%s\", \"variables\": %s }", query.replace("\"", "\\\""), variable);
        System.out.println(full_query);

        FetchRequestHandler fetchRequestHandler = new FetchRequestHandler(apiData.endpoint, headers, params, secret, full_query);
        Response postResponse = fetchRequestHandler.extractResponseContent();
        ArrayNode arrayNode1 = fetchRequestHandler.extractJSONArrayData(apiData.node);
        if(!arrayNode1.isEmpty()) {
            apiData.setDataNode(arrayNode1);
            System.out.println("arr: " + apiData.dataNode);
        } else {
            Assert.fail("This node from Post response data is empty. Please ensure a valid response return");
        }

    }

    @When("I fetch the soap api endpoint and extract data from node using the following params")
    @When("I fetch the soap api endpoint and extract data from node with the following params")
    public void iFetchTheSoapApiEndpointAndExtractDataFromNodeUsingTheFollowingParams(DataTable table) throws IOException {
        Map<String, String> param_table = table.asMap();

        Secret.SecretBuilder secretBuilder = Secret.builder();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        String body = "";

        for (Map.Entry<String, String> entry : param_table.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Check if the key contains "secret_"
            if (key.startsWith("secret_")) {
                // Extract the field name after "secret_"
                String fieldName = key.substring("secret_".length());
                // Use reflection to set the field value in the builder
                try {
                    secretBuilder.getClass().getMethod(fieldName, String.class).invoke(secretBuilder, value);
                } catch (ReflectiveOperationException e) {
                    // Handle exception (e.g., method not found)
                    e.printStackTrace();
                }
            }

            // Check if the key contains "header_"
            if (key.startsWith("header_")) {
                // Extract the header field name after "header_"
                String headerFieldName = key.substring("header_".length());
                // Add the header field and value to the header map
                headers.put(headerFieldName, value);
            }

            if (key.startsWith("param_")) {
                // Extract the header field name after "header_"
                String headerFieldName = key.substring("param_".length());
                // Add the header field and value to the header map
                params.put(headerFieldName, value);
            }

            if (key.startsWith("body")) {
                body = value;
            }

        }
        // Build the Secret object
        Secret secret = secretBuilder.build();

        SOAPRequestHandler soapRequestHandler = new SOAPRequestHandler(apiData.endpoint, headers, params, secret, body);
        Response postResponse = soapRequestHandler.extractResponseContent();
        ArrayNode arrayNode1 = soapRequestHandler.extractJSONArrayData(apiData.node);
        if (!arrayNode1.isEmpty()) {
            apiData.setDataNode(arrayNode1);
            System.out.println("arr: " + apiData.dataNode);
        } else {
            Assert.fail("This node from Post response data is empty. Please ensure a valid response return");
        }
    }

    @Given("I connected to the WebSocket Server by endpoint {string}")
    public void iConnectedToTheWebSocketServerByEndpoint(String arg0) throws URISyntaxException, InterruptedException {
        WebSocketHandler webSocketHandler = new WebSocketHandler(arg0);
        webSocketHandler.connectedToWebSocketServer();
        this.webSocketHandler = webSocketHandler;
    }

    @When("I send a message {string} to the WebSocket Server")
    public void iSendAMessageToTheWebSocketServer(String arg0) throws InterruptedException {
        webSocketHandler.sendMessageToServer(arg0);
    }

    @Then("I receive a message from the WebSocket Server")
    public void iReceiveAMessageFromTheWebSocketServer() throws ExecutionException, InterruptedException {
        List<String> receivedMessages = webSocketHandler.receieveFromService();
        System.out.println(receivedMessages);
    }
}
