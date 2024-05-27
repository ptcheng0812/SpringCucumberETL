Feature: Direct SQL Query Validation
  NOT ALLOW MISMATCH FOR TABULAR SQL COMPARISON
  ALLOW MISMATCH FOR OUTPUT COMPARISON

  Scenario: Send Direct SQL query to Database and Assert Returned Results (Example 1)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                           |
      | SELECT COUNT(*) as count FROM master_products |
      | WHERE category = 'furniture'                  |
    Then the following results return from database
      | count |
      | 5     |

  Scenario: Send Direct SQL query to Database and Assert Returned Results (Example 2)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                                     |
      | SELECT id, title, brand, images FROM master_products mp |
      | WHERE mp.title IN ('Apple', 'Beef Steak')               |
    Then the following results return from database
      | id | title      | brand | images                                                                 |
      | 16 | Apple      |       | https://cdn.dummyjson.com/products/images/groceries/Apple/1.png        |
      | 17 | Beef Steak |       | https://cdn.dummyjson.com/products/images/groceries/Beef%20Steak/1.png |

  Scenario: Send Direct SQL query to Database and Assert Returned Results
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                       |
      | SELECT * FROM master_products mp          |
      | WHERE mp.title IN ('Apple', 'Beef Steak') |
    Then the following results return from database match with test xlsx "src/main/resources/test_db_direct.xlsx" with primary key "id" and output difference file "src/main/resources/difference_db.xlsx"
