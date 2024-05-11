package etl.stepdefs;

import etl.data.SQL;
import etl.utilities.MethodHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static etl.utilities.MethodHelper.tableExists;

public class DirectSQLStepDef {
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String user;
    @Value("${spring.datasource.password}")
    String password;
    @Autowired
    SQL sql;
    @When("I send the following query to database")
    public void iSendTheFollowingQueryToDatabase(DataTable dataTable) {
        sql.setQuery(MethodHelper.buildQueryFromTable(dataTable).substring(4));
        System.out.println("query" + sql.query);

        try {
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                try(PreparedStatement statement = connection.prepareStatement(sql.query)) {
                    ResultSet resultSet = statement.executeQuery();
                    sql.setResult(MethodHelper.convertResultSetToList(resultSet));
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}
        System.out.println("result" + sql.result);

    }

    @Then("the following results return from database")
    public void theFollowingResultsReturnFromDatabase(DataTable expectedTable) {
        List<Map<String, String>> expectedData = expectedTable.asMaps();
        System.out.println("expectedData"+expectedData);
        // Assert that the actual data matches the expected data
        Assert.assertEquals(expectedData, sql.result);
    }
}
