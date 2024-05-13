package etl.stepdefs;

import com.fasterxml.jackson.databind.node.ArrayNode;
import etl.api.GetRequestHandler;
import etl.api.Secret;
import etl.data.APIData;
import etl.utilities.MethodHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;

public class APIStepDef {
    @Autowired
    APIData apiData;

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
        // Print the populated Secret object
//        System.out.print(secret.getTypeOfAuth());
//        System.out.print(secret.getJwtToken());
//        System.out.print(headers);
//        System.out.print(params);

        switch (arg0){
            case "GET":
                GetRequestHandler getRequestHandler = new GetRequestHandler(apiData.endpoint, headers, params, secret);
                Response response = getRequestHandler.extractResponseContent();
                ArrayNode arrayNode = getRequestHandler.extractJSONArrayData(apiData.node);
//                System.out.println(response.body().prettyPrint());
                System.out.println("arr: " + arrayNode);
                if(!arrayNode.isEmpty()) {
                    apiData.setDataNode(arrayNode);
                } else {
                    Assert.fail("This node from response data is empty. Please ensure a valid response return");
                }

        }
    }
}
