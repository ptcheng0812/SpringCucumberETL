package etl.stepdefs;

import etl.data.FilePath;
import etl.data.ScenarioLevel;
import etl.data.SheetData;
import etl.utilities.MethodHelper;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static etl.utilities.MethodHelper.tableExists;

public class ReadDatabaseStepDef {
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String user;
    @Value("${spring.datasource.password}")
    String password;
    @Autowired
    FilePath filePath;

    @Autowired
    SheetData sheetData;
    @Autowired
    ScenarioLevel scenarioLevel;

    @When("I retrieve data from master table {string} in database")
    public void iRetrieveDataFromMasterTableInDatabase(String arg0) {
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Database connected successfully");
                // Check if the table exists
                if (tableExists(connection, arg0)) { System.out.println("Table exists.");}
                List<String> headers1 = MethodHelper.getDBTableHeaders(connection, arg0);
                List<Map<String, String>> dataList = MethodHelper.getAllDataFromTable(connection, arg0);

                //Set master data headers
                for(String header : headers1) {
                    sheetData.setMaster_headerList(header.toLowerCase());
                }
                //Set master data headers
                for(Map<String, String> rowData : dataList) {
                    sheetData.setMaster_dataList(rowData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("headers from db:" + sheetData.master_headerList);
            System.out.println("Master Dict List:" + sheetData.master_dataList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

