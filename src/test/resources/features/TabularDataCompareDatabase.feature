Feature: Compare Tabular Data to Database
  Allow not matched

  Scenario: Check if Tabular Data exist and compare with the table in database (row-query)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I compare the following tabular data to table "master_products" in database
      | id | title                   | description                                                                                                                        | price | discountPercentage | rating | stock | brand       | category    | thumbnail                                                  | images                                                                                                                                                                                                                                                           |
      | 1  | iPhone 9                | An apple mobile which is nothing like apple                                                                                        | 549   | 12.96              | 4.69   | 94    | Apple       | smartphones | https://cdn.dummyjson.com/product-images/1/thumbnail.jpg   | https://cdn.dummyjson.com/product-images/1/1.jpg, https://cdn.dummyjson.com/product-images/1/2.jpg, https://cdn.dummyjson.com/product-images/1/3.jpg, https://cdn.dummyjson.com/product-images/1/4.jpg, https://cdn.dummyjson.com/product-images/1/thumbnail.jpg |
      | 5  | Huaw P30                | Huawei’s re-badged P30 Pro New Edition was officially unveiled yesterday in Germany and now the device has made its way to the UK. | 499   | 10.58              | 4.09   | 32    | Huawei      | smartphones | https://cdn.dummyjson.com/product-images/5/thumbnail.jpg   | https://cdn.dummyjson.com/product-images/5/1.jpg, https://cdn.dummyjson.com/product-images/5/2.jpg, https://cdn.dummyjson.com/product-images/5/3.jpg                                                                                                             |
      | 10 | HP Pavilion 15-DK1056WM | HP Pavil 15-DK1056WM Gaming Laptop 10th Gen Core i5, 8GB, 256GB SSD, GTX 1650 4GB, Windows 10                                      | 1099  | 6.18               | 4.43   | 89    | HP Pavilion | laptops     | https://cdn.dummyjson.com/product-images/10/thumbnail.jpeg | https://cdn.dummyjson.com/product-images/10/1.jpg, https://cdn.dummyjson.com/product-images/10/2.jpg, https://cdn.dummyjson.com/product-images/10/3.jpg, https://cdn.dummyjson.com/product-images/10/thumbnail.jpeg                                              |


  Scenario: Check if Tabular Data exist and compare with the table in database (key-query)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I compare the following tabular data to table "master_products" in database by each key
      | id | title                   | description                                                                                   | price | discountPercentage | rating | stock | brand       | category    | thumbnail                                                  | images                                                                                                                                                                                                                                                           |
      | 1  | iPhone 9                | An apple mobile which is nothing like apple                                                   | 549   | 12.96              | 4.69   | 94    | Apple       | smartphones | https://cdn.dummyjson.com/product-images/1/thumbnail.jpg   | https://cdn.dummyjson.com/product-images/1/1.jpg, https://cdn.dummyjson.com/product-images/1/2.jpg, https://cdn.dummyjson.com/product-images/1/3.jpg, https://cdn.dummyjson.com/product-images/1/4.jpg, https://cdn.dummyjson.com/product-images/1/thumbnail.jpg |
      | 10 | HP Pavilion 15-DK1056WM | HP Pavil 15-DK1056WM Gaming Laptop 10th Gen Core i5, 8GB, 256GB SSD, GTX 1650 4GB, Windows 10 | 1099  | 6.18               | 4.43   | 89    | HP Pavilion | laptops     | https://cdn.dummyjson.com/product-images/10/thumbnail.jpeg | https://cdn.dummyjson.com/product-images/10/1.jpg, https://cdn.dummyjson.com/product-images/10/2.jpg, https://cdn.dummyjson.com/product-images/10/3.jpg, https://cdn.dummyjson.com/product-images/10/thumbnail.jpeg                                              |
