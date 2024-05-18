package etl.stepdefs;

import etl.data.SQL;
import etl.utilities.MethodHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompareDBTablesStepDef {
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String user;
    @Value("${spring.datasource.password}")
    String password;
    @Autowired
    SQL sql;
    @When("I compare master and test in database with the following query")
    public void iCompareMasterAndTestInDatabaseWithTheFollowingQuery(DataTable dataTable) {
        String master_sql = MethodHelper.buildQueryFromTable(dataTable).substring(4).replace("{compare}", "master");
        String test_sql = MethodHelper.buildQueryFromTable(dataTable).substring(4).replace("{compare}", "test");
        List<Map<String, String>> master_result = new ArrayList<>();
        List<Map<String, String>> test_result = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                try(PreparedStatement statement = connection.prepareStatement(master_sql)) {
                    ResultSet resultSet = statement.executeQuery();
                    master_result = MethodHelper.convertResultSetToList(resultSet);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}

        try {
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                try(PreparedStatement statement = connection.prepareStatement(test_sql)) {
                    ResultSet resultSet = statement.executeQuery();
                    test_result = MethodHelper.convertResultSetToList(resultSet);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}

        if(!MethodHelper.CompareListsAndReturnNotMatched(master_result, test_result).isEmpty()) {
            Assert.fail("There is not matched data: ");
            System.out.println(MethodHelper.CompareListsAndReturnNotMatched(master_result, test_result));
        } else {
            System.out.println("All data matched in master and test table");
        }
    }

    @When("I compare master and test in database with all data in table {string}")
    public void iCompareMasterAndTestInDatabaseWithAllDataInTable(String arg0) {
        String master_sql = "SELECT * FROM master_" + arg0 + " ";
        String test_sql = "SELECT * FROM test_" + arg0 + " ";
        List<Map<String, String>> master_result = new ArrayList<>();
        List<Map<String, String>> test_result = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                try(PreparedStatement statement = connection.prepareStatement(master_sql)) {
                    ResultSet resultSet = statement.executeQuery();
                    master_result = MethodHelper.convertResultSetToList(resultSet);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}

        try {
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                try(PreparedStatement statement = connection.prepareStatement(test_sql)) {
                    ResultSet resultSet = statement.executeQuery();
                    test_result = MethodHelper.convertResultSetToList(resultSet);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}

        if(!MethodHelper.CompareListsAndReturnNotMatched(master_result, test_result).isEmpty()) {
            Assert.fail("There is not matched data: ");
            System.out.println(MethodHelper.CompareListsAndReturnNotMatched(master_result, test_result));
        } else {
            System.out.println("All data matched in master and test table");
        }
    }
}
