Feature: Restful API

  Scenario: Get Request Test
    Given the API endpoint is "https://dummyjson.com/auth/me" and node is ""
    When I fetch the restful api endpoint by "GET" and extract data from node with the following params
      | header_Content-Type | application/json                                                                                                                                                                                                                                                                                                                                                        |
      | secret_typeOfAuth   | JWT                                                                                                                                                                                                                                                                                                                                                                     |
      | secret_jwtToken     | eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTUsInVzZXJuYW1lIjoia21pbmNoZWxsZSIsImVtYWlsIjoia21pbmNoZWxsZUBxcS5jb20iLCJmaXJzdE5hbWUiOiJKZWFubmUiLCJsYXN0TmFtZSI6IkhhbHZvcnNvbiIsImdlbmRlciI6ImZlbWFsZSIsImltYWdlIjoiaHR0cHM6Ly9yb2JvaGFzaC5vcmcvSmVhbm5lLnBuZz9zZXQ9c2V0NCIsImlhdCI6MTcxNTU1MDQwNCwiZXhwIjoxNzE4MTQyNDA0fQ.Lk3W5qqxM9T9V4mv_RJ04x3orJUr4PrCvhSVCD_68TY |