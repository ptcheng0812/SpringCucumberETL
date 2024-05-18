Feature: Restful API

  Scenario: Get Request Test
    Given the API endpoint is "https://dummyjson.com/auth/me" and node is ""
    When I fetch the restful api endpoint by "GET" and extract data from node with the following params
      | header_Content-Type | application/json                                                                                                                                                                                                                                                                                                                                                        |
      | secret_typeOfAuth   | JWT                                                                                                                                                                                                                                                                                                                                                                     |
      | secret_jwtToken     | eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTUsInVzZXJuYW1lIjoia21pbmNoZWxsZSIsImVtYWlsIjoia21pbmNoZWxsZUBxcS5jb20iLCJmaXJzdE5hbWUiOiJKZWFubmUiLCJsYXN0TmFtZSI6IkhhbHZvcnNvbiIsImdlbmRlciI6ImZlbWFsZSIsImltYWdlIjoiaHR0cHM6Ly9yb2JvaGFzaC5vcmcvSmVhbm5lLnBuZz9zZXQ9c2V0NCIsImlhdCI6MTcxNjAzNDI5NCwiZXhwIjoxNzE4NjI2Mjk0fQ.ZeLwi1Oh81jSuiBQA453o5wt9Z4DKvqxUh8O59zFg_Y |

  Scenario: Post Request Test
    Given the API endpoint is "https://dummyjson.com/auth/login" and node is ""
    When I fetch the restful api endpoint by "POST" and extract data from node with the following params
      | header_Content-Type | application/json                                                        |
      | body                | {"username": "kminchelle","password": "0lelplR","expiresInMins": 43200} |