Feature: Compare Tabular Data to Database
  Allow not matched

  Scenario: Check if Tabular Data exist and compare with the table in database (row-query)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I compare the following tabular data to table "master_products" in database
      | id | title                         | description                                                                                                                                                                               |
      | 1  | Essence Mascara Lash Princess | The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects. Achieve dramatic lashes with this long-lasting and cruelty-free formula.         |
      | 2  | Eyeshadow Palette with Mirror | The Eyeshadow Palette with Mirror offers a versatile range of eyeshadow shades for creating stunning eye looks. With a built-in mirror, it's convenient for on-the-go makeup application. |
      | 3  | Powder Canister               | The Powder Canister is a finely milled setting powder designed to set makeup and control shine. With a lightweight and translucent formula, it provides a smooth and matte finish.        |


  Scenario: Check if Tabular Data exist and compare with the table in database (key-query)
    Given I connected to the database
    Given the API endpoint is "https://dummyjson.com/products" and node is "products"
    When I fetch the api endpoint and extract data from node
    Then I write node data to database
    When I compare the following tabular data to table "master_products" in database by each key
      | id | title                         | meta_createdat           | meta_updatedat           | meta_barcode  | meta_qrcode                              | images_0                                                                                   |
      | 1  | Essence Mascara Lash Princess | 2024-05-23T08:56:21.618Z | 2024-05-23T08:56:21.618Z | 9164035109868 | https://dummyjson.com/public/qr-code.png | https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/1.png |
      | 2  | Eyeshadow Palette with Mirror | 2024-05-23T08:56:21.618Z | 2024-05-23T08:56:21.618Z | 2817839095220 | https://dummyjson.com/public/qr-code.png | https://cdn.dummyjson.com/products/images/beauty/Eyeshadow%20Palette%20with%20Mirror/1.png |
      | 3  | Powder Canister               | 2024-05-23T08:56:21.618Z | 2024-05-23T08:56:21.618Z | 0516267971277 | https://dummyjson.com/public/qr-code.png | https://cdn.dummyjson.com/products/images/beauty/Powder%20Canister/1.png                   |



