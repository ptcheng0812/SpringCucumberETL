Feature: Compare Master and Test tables in Database

  Scenario: Compare Master and Test tables in Database using direct sql
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                                                       |
      | DROP TABLE IF EXISTS test_products;                                       |
      | CREATE TABLE "public"."test_products" AS TABLE "public"."master_products" |
    When I compare master and test in database with the following query
      | SQL                                                          |
      | SELECT * FROM {compare}_products                             |
      | WHERE title IN ('Infinix INBOOK', 'HP Pavilion 15-DK1056WM') |

  Scenario: Compare Master and Test tables in Database with all data
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                                                       |
      | DROP TABLE IF EXISTS test_products;                                       |
      | CREATE TABLE "public"."test_products" AS TABLE "public"."master_products" |
    When I compare master and test in database with all data in table "products"