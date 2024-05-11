Feature: Compare XLSX to Database

  Scenario: Compare XLSX to Database (implicit mode)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I compare master table "master_products" in database with test xlsx "src/main/resources/test_db.xlsx" and output difference file "src/main/resources/difference.xlsx"

  Scenario: Compare XLSX to Database with sheet specified (implicit mode)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I compare master table "master_products" in database with sheet "Result1" in test xlsx "src/main/resources/test_db.xlsx" and output difference file "src/main/resources/difference.xlsx"

  Scenario:  Compare XLSX to Database (explicit mode)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I retrieve data from master table "master_products" in database
    When I retrieve data from test file "src/main/resources/test_db.xlsx"
    When I compare the master with test and output the difference file "src/main/resources/difference.xlsx"

  Scenario:  Compare XLSX to Database with sheet specified (explicit mode)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I retrieve data from master table "master_products" in database
    When I retrieve data from sheet "Result1" in test file "src/main/resources/test_db.xlsx"
    When I compare the master with test and output the difference file "src/main/resources/difference.xlsx"