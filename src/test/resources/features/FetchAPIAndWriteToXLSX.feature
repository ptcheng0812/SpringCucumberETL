Feature: Fetch API endpoint and Write data to XLSX

  Scenario: Fetch API endpoint and store data in scenario context
    both json and xml can use the same step, response method will identify whether response is in json/xml format
    node name can be empty, if you are extracting from root level
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node

    Given the API endpoint is "https://cdn.animenewsnetwork.com/encyclopedia/api.xml?title=4658" and node is "staff"
    When I fetch the api endpoint and extract data from node

  Scenario: Fetch API endpoint and write node data to xlsx
    Given the API endpoint is "https://cdn.animenewsnetwork.com/encyclopedia/api.xml?title=4658" and node is "episode"
    When I fetch the api endpoint and extract data from node
    Then I write node data to xlsx file "src/main/resources/master_xml.xlsx"

  Scenario: Fetch API endpoint and write node data to xlsx with sheet specified
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to sheet "Result1" in xlsx file "src/main/resources/master_json.xlsx"

  Scenario: Fetch API endpoint and write node data to xlsx with sheet specified (root is array)
    Given the API endpoint is "https://jsonplaceholder.typicode.com/posts" and node is ""
    When I fetch the api endpoint and extract data from node
    Then I write node data to sheet "Result1" in xlsx file "src/main/resources/master_json_arrayTest.xlsx"