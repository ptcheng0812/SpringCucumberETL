package etl.stepdefs;

import com.fasterxml.jackson.databind.node.ArrayNode;
import etl.data.APIData;
import etl.utilities.MethodHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

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
        ArrayNode arrayNode = MethodHelper.GetAPIResponseAndTurnIntoNode(apiData.endpoint, apiData.node);
        if(!arrayNode.isEmpty()) {
            apiData.setDataNode(arrayNode);
        } else {
            Assert.fail("This node from response data is empty. Please ensure a valid response return");
        }
    }
}
