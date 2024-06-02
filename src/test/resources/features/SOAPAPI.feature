Feature: SOAP API

  Scenario: Fetch GraphQL using xml file
    Given the API endpoint is "http://www.dneonline.com/calculator.asmx" and node is "DivideResponse"
    When I fetch the soap api endpoint and extract data from node using the following params
      | header_Content-Type | text/xml; charset=utf-8          |
      | header_SOAPAction   | http://tempuri.org/Divide        |
      | body                | src/main/resources/soap_test.xml |