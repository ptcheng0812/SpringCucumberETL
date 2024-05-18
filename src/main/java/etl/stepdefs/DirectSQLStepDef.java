package etl.stepdefs;

import etl.data.FilePath;
import etl.data.SQL;
import etl.data.SheetData;
import etl.utilities.MethodHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.sql.*;
import java.util.*;

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
    @Autowired
    SheetData sheetData;
    @Autowired
    FilePath filePath;
    @When("I send the following query to database")
    public void iSendTheFollowingQueryToDatabase(DataTable dataTable) {
        sql.setQuery(MethodHelper.buildQueryFromTable(dataTable).substring(4));

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
    }

    @Then("the following results return from database")
    public void theFollowingResultsReturnFromDatabase(DataTable expectedTable) {
        List<Map<String, String>> expectedData = expectedTable.asMaps();
        // Assert that the actual data matches the expected data
        Assert.assertEquals(expectedData, sql.result);
    }

    @Then("the following results return from database match with test xlsx {string} and output difference file {string}")
    public void theFollowingResultsReturnFromDatabaseMatchWithTestXlsxAndOutputDifferenceFile(String arg0, String arg1) throws IOException {
        if(Objects.equals(arg0, "") || Objects.equals(arg1, "")) {
            Assert.fail("Missing parameters \n please provide all master/test/output file path");
        } else {
            filePath.setTest_path(arg0);
            filePath.setOutput_path(arg1);
        }
        List<String> headersFromDbResult = MethodHelper.getKeysAsList(sql.result.get(0));

        List<Map<String, String>> notMatchedMaps_master = new ArrayList<>();
        List<Map<String, String>> notMatchedMaps_test = new ArrayList<>();
        List<String> headersLogMessages = new ArrayList<>();


        //Test map data list
        FileInputStream inputStream = new FileInputStream(new File(arg0));
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);
        List<Map<String, String>> dictionaryList = new ArrayList<>();
        //Get headers, then add to header list
        List<String> headers = new ArrayList<>();
        Row headerRow = spreadsheet.getRow(0);
        Iterator<Cell> itr_cells = headerRow.cellIterator();
        while(itr_cells.hasNext()) {
            String header = String.valueOf(itr_cells.next());
            headers.add(header);
            sheetData.setTest_headerList(header);
        }

        //Get rows and its data, then add to row data list, return list of dictionary
        for (int r=1; r<= spreadsheet.getLastRowNum(); r++) {
            List<String> rowData = new ArrayList<>();
            Row datarow = spreadsheet.getRow(r);
            Iterator < Cell >  itr_data_cells = datarow.cellIterator();
            while ( itr_data_cells.hasNext()) {
                String data = String.valueOf(itr_data_cells.next());
                rowData.add(MethodHelper.CheckEndsWithZeroStringConvert(data));
            }
            Map<String, String> singleData = MethodHelper.convertToDictionary(headers, rowData);
            dictionaryList.add(singleData);
            sheetData.setTest_dataList(singleData);
        }


        //Compare headers
        for (String header: sheetData.test_headerList) {
            if(!headersFromDbResult.contains(header)) {
                headersLogMessages.add(header);
            }
        }
        //Compare master and test data list
        for (int el=0; el< sql.result.size(); el++) {
            for(String key: sql.result.get(el).keySet()) {
                if (!Objects.equals(sql.result.get(el).get(key), sheetData.test_dataList.get(el).get(key))) {
                    notMatchedMaps_master.add(sql.result.get(el));
                    break;
                }
            }
        }
        for (int el=0; el< sheetData.test_dataList.size(); el++) {
            for(String key: sheetData.test_dataList.get(el).keySet()) {
                if (!Objects.equals(sheetData.test_dataList.get(el).get(key), sql.result.get(el).get(key))) {
                    notMatchedMaps_test.add(sheetData.test_dataList.get(el));
                    break;
                }
            }
        }


        System.out.println("Not matched from MASTER: " + notMatchedMaps_master);
        System.out.println("Not matched from TEST: " + notMatchedMaps_test);
        System.out.println("Not matched headers: "+ headersLogMessages);

        //Prepare New Workbook to write difference in rows/cells and headers
        XSSFWorkbook wb = new XSSFWorkbook();

        //Write different headers in xlsx
        if(!headersLogMessages.isEmpty()) {
            XSSFSheet sheet = wb.createSheet("Headers Difference comparng XLSX from DB Result");
            Row row_zero = sheet.createRow(0);
            Row row_one = sheet.createRow(1);
            Row row_two = sheet.createRow(2);
            Cell cell_zero = row_zero.createCell(0);
            cell_zero.setCellValue("Difference at index: " + MethodHelper.concatenateWithStringJoin(headersLogMessages));
            Cell cell_0_row_one = row_one.createCell(0);
            cell_0_row_one.setCellValue("master from DB");
            cell_0_row_one.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(0));
            int colNum_row1 = 1;
            for (String header : headersFromDbResult) {
                Cell cell = row_one.createCell(colNum_row1++);
                cell.setCellValue(header);
            }
            Cell cell_0_row_two = row_two.createCell(0);
            cell_0_row_two.setCellValue("test from XLSX");
            cell_0_row_two.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));
            int colNum_row2 = 1;
            for (String header : sheetData.test_headerList) {
                Cell cell = row_two.createCell(colNum_row2++);
                cell.setCellValue(header);
            }
        }

        //Write different rows/cell
        XSSFSheet sheet_sub = wb.createSheet("Compare Difference between XLSX and DB Result");
        CellCopyPolicy policy = new CellCopyPolicy();
        policy.isCopyCellValue();
        if (!sql.result.isEmpty()) {
            Row row_zero = sheet_sub.createRow(0);
            int colNum_row0 = 0;
            for (String header : headersFromDbResult) {
                Cell cell = row_zero.createCell(colNum_row0++);
                cell.setCellValue(header);
            }
            if (!headersLogMessages.isEmpty()) {
                Row row_one = sheet_sub.createRow(1);
                int colNum_row1 = 0;
                for (String header : sheetData.test_headerList) {
                    Cell cell = row_one.createCell(colNum_row1++);
                    cell.setCellValue(header);
                }
            }
            for(int r = 0; r <notMatchedMaps_master.size(); r++){
                XSSFRow master_row = null;
                XSSFRow test_row = null;
                if(!headersLogMessages.isEmpty()) {
                    master_row = sheet_sub.createRow(r*2+2);
                    test_row = sheet_sub.createRow(r*2 +3);
                } else {
                    master_row = sheet_sub.createRow(r*2+1);
                    test_row = sheet_sub.createRow(r*2 +2);
                }
                for (int el = 0; el < headersFromDbResult.size(); el++) {
                    Cell cell = master_row.createCell(el);
                    cell.setCellValue(notMatchedMaps_master.get(r).get(headersFromDbResult.get(el)));
                }
                for (int el = 0; el < sheetData.test_headerList.size(); el++) {
                    Cell cell = test_row.createCell(el);
                    cell.setCellValue(notMatchedMaps_test.get(r).get(sheetData.test_headerList.get(el)));
                }

                Cell indicate_master_cell = master_row.createCell(headersFromDbResult.size());
                indicate_master_cell.setCellValue("master");
                indicate_master_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(0));
                Cell indicate_test_cell = test_row.createCell(sheetData.test_headerList.size());
                indicate_test_cell.setCellValue("test");
                indicate_test_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));

            }
        }

        ////Print Out xlsx
        if(!headersLogMessages.isEmpty() || !notMatchedMaps_master.isEmpty() || !notMatchedMaps_test.isEmpty()) {
            filePath.setOutput_path(arg1);
            FileOutputStream outputStream = new FileOutputStream(filePath.output_path);
            wb.write(outputStream);
            wb.close();
            System.out.println("output xlsx file was generated");
            Assert.fail("difference is found");
        }
    }
}
