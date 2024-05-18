Feature: Direct SQL Query Validation
  NOT ALLOW MISMATCH

  Scenario: Send Direct SQL query to Database and Assert Returned Results (Example 1)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                           |
      | SELECT COUNT(*) as count FROM master_products |
      | WHERE category = 'smartphones'                |
    Then the following results return from database
      | count |
      | 5     |

  Scenario: Send Direct SQL query to Database and Assert Returned Results (Example 2)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                                             |
      | SELECT * FROM master_products mp                                |
      | WHERE mp.title IN ('Infinix INBOOK', 'HP Pavilion 15-DK1056WM') |
    Then the following results return from database
      | id | title                   | description                                                                                      | price | discountpercentage | rating | stock | brand       | category | thumbnail                                                  | images                                                                                                                                                                                                                                                           |
      | 9  | Infinix INBOOK          | Infinix Inbook X1 Ci3 10th 8GB 256GB 14 Win10 Grey – 1 Year Warranty                             | 1099  | 11.83              | 4.54   | 96    | Infinix     | laptops  | https://cdn.dummyjson.com/product-images/9/thumbnail.jpg   | https://cdn.dummyjson.com/product-images/9/1.jpg, https://cdn.dummyjson.com/product-images/9/2.png, https://cdn.dummyjson.com/product-images/9/3.png, https://cdn.dummyjson.com/product-images/9/4.jpg, https://cdn.dummyjson.com/product-images/9/thumbnail.jpg |
      | 10 | HP Pavilion 15-DK1056WM | HP Pavilion 15-DK1056WM Gaming Laptop 10th Gen Core i5, 8GB, 256GB SSD, GTX 1650 4GB, Windows 10 | 1099  | 6.18               | 4.43   | 89    | HP Pavilion | laptops  | https://cdn.dummyjson.com/product-images/10/thumbnail.jpeg | https://cdn.dummyjson.com/product-images/10/1.jpg, https://cdn.dummyjson.com/product-images/10/2.jpg, https://cdn.dummyjson.com/product-images/10/3.jpg, https://cdn.dummyjson.com/product-images/10/thumbnail.jpeg                                              |

  Scenario: Send Direct SQL query to Database and Assert Returned Results
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I send the following query to database
      | SQL                                                             |
      | SELECT * FROM master_products mp                                |
      | WHERE mp.title IN ('Infinix INBOOK', 'HP Pavilion 15-DK1056WM') |
    Then the following results return from database match with test xlsx "src/main/resources/test_db_direct.xlsx" and output difference file "src/main/resources/difference_db.xlsx"
