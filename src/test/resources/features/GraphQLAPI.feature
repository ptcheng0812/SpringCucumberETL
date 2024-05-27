Feature: GraphQL API

  Scenario: Fetch GraphQL using request with variables
    Given the API endpoint is "https://spacex-production.up.railway.app/" and node is "dragon"
    When I fetch the graphql api endpoint and extract data from node using the following params
      | header_Content-Type | application/json                                                                                                                                                             |
      | query               | query ExampleQuery($dragonId: ID!) {dragon(id: $dragonId) {active crew_capacity description dry_mass_kg dry_mass_lb first_flight id name orbit_duration_yr type wikipedia }} |
      | variable            | { "dragonId": "5e9d058759b1ff74a7ad5f8f" }                                                                                                                                   |