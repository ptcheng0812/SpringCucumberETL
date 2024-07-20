Feature: Web Socket

  Scenario: Web Socket Testing
    Given I connected to the WebSocket Server by endpoint "wss://echo.websocket.org"
    When I send a message "" to the WebSocket Server
    When I send a message "test from spring boot" to the WebSocket Server
    Then I receive a message from the WebSocket Server