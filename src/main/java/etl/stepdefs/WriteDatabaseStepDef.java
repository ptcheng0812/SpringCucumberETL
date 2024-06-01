package etl.stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import etl.data.APIData;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static etl.utilities.MethodHelper.*;

public class WriteDatabaseStepDef {
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String user;
    @Value("${spring.datasource.password}")
    String password;
    @Autowired
    APIData apiData;
    @Then("I write node data to database")
    public void iWriteNodeDataToDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Database connected successfully");
                ArrayNode arrayNode = apiData.dataNode;

                // Check if the table exists
                if (tableExists(connection, "master_"+apiData.node)) {
                    System.out.println("Table exists. Dropping...");
                    dropTable(connection, "master_"+apiData.node);
                }

                // Generate SQL CREATE TABLE statement
                String createTableSql = generateCreateTableSql("master_"+apiData.node, arrayNode);
                System.out.println(createTableSql);

                // Execute SQL statement to create the table
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(createTableSql);
                    System.out.println("Table created successfully");
                }

                for (JsonNode singleNode : arrayNode) {
                    insertDataToDatabase(connection, singleNode, "master_"+apiData.node);
                }

                System.out.println("Data inserted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
