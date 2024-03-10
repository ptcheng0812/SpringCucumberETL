Feature: 2 XLSX comparison

  Scenario: Compare 2 xlsx files (implicit mode)
    When I compare master xlsx "src/main/resources/master.xlsx" with test xlsx "src/main/resources/test.xlsx" and output difference file "src/main/resources/difference.xlsx"

  Scenario: Compare 2 xlsx files with sheet specified (implicit mode)
    When I compare sheet "Result1" in master xlsx "src/main/resources/master.xlsx" with sheet "Result1" in test xlsx "src/main/resources/test.xlsx" and output difference file "src/main/resources/difference.xlsx"

  Scenario:  Compare 2 xlsx files (explicit mode)
    When I retrieve data from master file "src/main/resources/master.xlsx"
    When I retrieve data from test file "src/main/resources/test.xlsx"
    When I compare the master with test and output the difference file "src/main/resources/difference.xlsx"

  Scenario:  Compare 2 xlsx files with sheet specified (explicit mode)
    When I retrieve data from sheet "Result1" in master file "src/main/resources/master.xlsx"
    When I retrieve data from sheet "Result1" in test file "src/main/resources/test.xlsx"
    When I compare the master with test and output the difference file "src/main/resources/difference.xlsx"