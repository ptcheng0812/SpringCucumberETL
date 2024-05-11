Feature: Fetch API and Write to Database

  Scenario: Fetch json API endpoint and write node data to database
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database

  Scenario: Fetch xml API endpoint and write node data to database
    Given I connected to the database
    Given the API endpoint is "https://cdn.animenewsnetwork.com/encyclopedia/api.xml?title=4658" and node is "episode"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database