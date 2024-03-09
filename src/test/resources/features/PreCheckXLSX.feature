Feature: Precondition Check XLSX

  Scenario: Verify if XLSX exist
    Given the xlsx file "C:/SpringCucumberETL/src/main/resources/master.xlsx" exists
    Given the xlsx has data and is not an empty xlsx


  Scenario: Verify if XLSX not exist (negative)
    Given I expect the scenario to fail
    Given the xlsx file "" exists
    Given the xlsx has data and is not an empty xlsx


#  Scenario: Verify if XLSX not exist (negative and skip)
#    Given I expect the scenario to fail
#    Given the xlsx file "" exists
#    Given the xlsx has data and is not an empty xlsx