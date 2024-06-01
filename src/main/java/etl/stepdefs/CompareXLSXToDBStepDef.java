package etl.stepdefs;

import etl.data.FilePath;
import etl.data.ScenarioLevel;
import etl.data.SheetData;
import etl.utilities.ExpectedToFailException;
import etl.utilities.MethodHelper;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static etl.utilities.MethodHelper.*;

public class CompareXLSXToDBStepDef {
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

    @When("I compare master table {string} in database with test xlsx {string} and output difference file {string}")
    public void iCompareMasterTableInDatabaseWithTestXlsxAndOutputDifferenceFile(String arg0, String arg1, String arg2) throws ExpectedToFailException {
        try {
            if(Objects.equals(arg1, "") || Objects.equals(arg2, "")) {
                Assert.fail("Missing parameters \n please provide all master/test/output file path");
            } else {
                filePath.setTest_path(arg1);
                filePath.setOutput_path(arg2);
            }

            //Read test files
            FileInputStream inputStream2 = new FileInputStream(new File(filePath.test_path));
            XSSFWorkbook workbook2 = new XSSFWorkbook(inputStream2);
            XSSFSheet spreadsheet2 = workbook2.getSheetAt(0);

            //Get headers from db and xlsx
            List<String> headersLogMessages = new ArrayList<>();
            List<String> headers1 = new ArrayList<>();
            List<String> headers2 = MethodHelper.GetHeaderAndReturnToHeaderList(spreadsheet2);
            try {
               Class.forName("org.postgresql.Driver");
                // Establish database connection
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    System.out.println("Database connected successfully");
                    // Check if the table exists
                    if (tableExists(connection, arg0)) { System.out.println("Table exists.");}
                    headers1 = MethodHelper.getDBTableHeaders(connection, arg0);
                } catch (SQLException e) {e.printStackTrace();}
            } catch (Exception e) {e.printStackTrace();}

            //Compare headers
            for (int h=0; h < headers1.size(); h++) {
                if(!headers1.get(h).equals(headers2.get(h).toLowerCase())) {
                    String headersMessage = headers1.get(h);
                    headersLogMessages.add(headersMessage);
                }
            }
            System.out.println("headersLogMessages" + headersLogMessages);

            List<Map<String, String>> mismatched_rows = new ArrayList<>();
            //Get rows and its data, then add to row data list, return list of dictionary
            for (int r=1; r<= spreadsheet2.getLastRowNum(); r++) {
                List<String> rowData = new ArrayList<>();
                Row datarow = spreadsheet2.getRow(r);
                Iterator< Cell > itr_data_cells = datarow.cellIterator();
                while ( itr_data_cells.hasNext()) {
                    String data = String.valueOf(itr_data_cells.next());
                    rowData.add(data);
                }
                Map<String, String> singleData = MethodHelper.convertToDictionary(headers2, rowData);
                sheetData.setTest_dataList(singleData);
            }

            for(Map<String, String> singleData: sheetData.test_dataList) {
                try {
                    // Establish database connection
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        if(!MethodHelper.queryDataInDatabaseAndReturnMismatched(connection, singleData, arg0)) {
                            mismatched_rows.add(singleData);
                        }
                    } catch (SQLException e) {e.printStackTrace();}
                } catch (Exception e) {e.printStackTrace();}
            }

            System.out.println("mismatched_rows" + mismatched_rows);

            //Prepare New Workbook to write difference in rows/cells and headers
            XSSFWorkbook wb = new XSSFWorkbook();

            //Write different headers in xlsx
            if(!headersLogMessages.isEmpty()) {
                XSSFSheet sheet = wb.createSheet("Headers Difference");
                Row row_zero = sheet.createRow(0);
                Row row_one = sheet.createRow(1);
                Row row_two = sheet.createRow(2);
                Cell cell_zero = row_zero.createCell(0);
                cell_zero.setCellValue("Difference at index: " + MethodHelper.concatenateWithStringJoin(headersLogMessages));
                Cell cell_0_row_one = row_one.createCell(0);
                cell_0_row_one.setCellValue("master");
                cell_0_row_one.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(0));
                int colNum_row1 = 1;
                for (String header : headers1) {
                    Cell cell = row_one.createCell(colNum_row1++);
                    cell.setCellValue(header);
                }
                Cell cell_0_row_two = row_two.createCell(0);
                cell_0_row_two.setCellValue("test");
                cell_0_row_two.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));
                int colNum_row2 = 1;
                for (String header : headers2) {
                    Cell cell = row_two.createCell(colNum_row2++);
                    cell.setCellValue(header);
                }
            }
//
            //Write different rows/cell
            XSSFSheet sheet_sub = wb.createSheet("Mismatched Rows To Database Implicit Mode");
            CellCopyPolicy policy = new CellCopyPolicy();
            policy.isCopyCellValue();
            if (!sheetData.test_dataList.isEmpty()) {
                Row row_zero = sheet_sub.createRow(0);
                int colNum_row0 = 0;
                for (String header : headers1) {
                    Cell cell = row_zero.createCell(colNum_row0++);
                    cell.setCellValue(header);
                }
                if (!headersLogMessages.isEmpty()) {
                    Row row_one = sheet_sub.createRow(1);
                    int colNum_row1 = 0;
                    for (String header : headers2) {
                        Cell cell = row_one.createCell(colNum_row1++);
                        cell.setCellValue(header);
                    }
                }
                for(int r = 0; r <mismatched_rows.size(); r++){
                    XSSFRow test_row = null;
                    if(!headersLogMessages.isEmpty()) {
                        test_row = sheet_sub.createRow(r+2);
                    } else {
                        test_row = sheet_sub.createRow(r+1);
                    }
                    for (int el = 0; el < headers2.size(); el++) {
                        Cell cell = test_row.createCell(el);
                        cell.setCellValue(mismatched_rows.get(r).get(headers2.get(el)));
                    }
                    Cell indicate_test_cell = test_row.createCell(headers2.size());
                    indicate_test_cell.setCellValue("test");
                    indicate_test_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));

                }
            }
            ////Print Out xlsx
            if(!headersLogMessages.isEmpty() || !mismatched_rows.isEmpty()) {
                FileOutputStream outputStream = new FileOutputStream(filePath.output_path);
                wb.write(outputStream);
                wb.close();
                System.out.println("output xlsx file was generated");
            }

        }catch (IOException e) {
            throw new ExpectedToFailException(scenarioLevel, e);
        }
    }

    @When("I compare master table {string} in database with sheet {string} in test xlsx {string} and output difference file {string}")
    public void iCompareMasterTableInDatabaseWithSheetInTestXlsxAndOutputDifferenceFile(String arg0, String arg1, String arg2, String arg3) throws ExpectedToFailException {
        try {
            if(Objects.equals(arg2, "") || Objects.equals(arg3, "")) {
                Assert.fail("Missing parameters \n please provide all master/test/output file path");
            } else {
                filePath.setTest_path(arg2);
                filePath.setOutput_path(arg3);
            }

            //Read test files
            FileInputStream inputStream2 = new FileInputStream(new File(filePath.test_path));
            XSSFWorkbook workbook2 = new XSSFWorkbook(inputStream2);
            XSSFSheet spreadsheet2 = workbook2.getSheet(arg1);

            //Compare headers
            List<String> headersLogMessages = new ArrayList<>();
            List<String> headers1 = new ArrayList<>();
            List<String> headers2 = MethodHelper.GetHeaderAndReturnToHeaderList(spreadsheet2);
            try {
                Class.forName("org.postgresql.Driver");
                // Establish database connection
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    System.out.println("Database connected successfully");
                    // Check if the table exists
                    if (tableExists(connection, arg0)) { System.out.println("Table exists.");}
                    headers1 = MethodHelper.getDBTableHeaders(connection, arg0);
                } catch (SQLException e) {e.printStackTrace();}
            } catch (Exception e) {e.printStackTrace();}

            //Compare headers
            for (int h=0; h < headers1.size(); h++) {
                if(!headers1.get(h).equals(headers2.get(h).toLowerCase())) {
                    String headersMessage = headers1.get(h);
                    headersLogMessages.add(headersMessage);
                }
            }
            System.out.println("headersLogMessages" + headersLogMessages);

            List<Map<String, String>> mismatched_rows = new ArrayList<>();
            //Get rows and its data, then add to row data list, return list of dictionary
            for (int r=1; r<= spreadsheet2.getLastRowNum(); r++) {
                List<String> rowData = new ArrayList<>();
                Row datarow = spreadsheet2.getRow(r);
                Iterator< Cell > itr_data_cells = datarow.cellIterator();
                while ( itr_data_cells.hasNext()) {
                    String data = String.valueOf(itr_data_cells.next());
                    rowData.add(data);
                }
                Map<String, String> singleData = MethodHelper.convertToDictionary(headers2, rowData);
                sheetData.setTest_dataList(singleData);
            }

            for(Map<String, String> singleData: sheetData.test_dataList) {
                try {
                    Class.forName("org.postgresql.Driver");
                    // Establish database connection
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        if(!MethodHelper.queryDataInDatabaseAndReturnMismatched(connection, singleData, arg0)) {
                            mismatched_rows.add(singleData);
                        }
                    } catch (SQLException e) {e.printStackTrace();}
                } catch (Exception e) {e.printStackTrace();}
            }

            System.out.println("mismatched_rows" + mismatched_rows);

            //Prepare New Workbook to write difference in rows/cells and headers
            XSSFWorkbook wb = new XSSFWorkbook();

            //Write different headers in xlsx
            if(!headersLogMessages.isEmpty()) {
                XSSFSheet sheet = wb.createSheet("Headers Difference");
                Row row_zero = sheet.createRow(0);
                Row row_one = sheet.createRow(1);
                Row row_two = sheet.createRow(2);
                Cell cell_zero = row_zero.createCell(0);
                cell_zero.setCellValue("Difference at index: " + MethodHelper.concatenateWithStringJoin(headersLogMessages));
                Cell cell_0_row_one = row_one.createCell(0);
                cell_0_row_one.setCellValue("master");
                cell_0_row_one.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(0));
                int colNum_row1 = 1;
                for (String header : headers1) {
                    Cell cell = row_one.createCell(colNum_row1++);
                    cell.setCellValue(header);
                }
                Cell cell_0_row_two = row_two.createCell(0);
                cell_0_row_two.setCellValue("test");
                cell_0_row_two.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));
                int colNum_row2 = 1;
                for (String header : headers2) {
                    Cell cell = row_two.createCell(colNum_row2++);
                    cell.setCellValue(header);
                }
            }
//
            //Write different rows/cell
            XSSFSheet sheet_sub = wb.createSheet("Mismatched Rows To Database Implicit Mode");
            CellCopyPolicy policy = new CellCopyPolicy();
            policy.isCopyCellValue();
            if (!sheetData.test_dataList.isEmpty()) {
                Row row_zero = sheet_sub.createRow(0);
                int colNum_row0 = 0;
                for (String header : headers1) {
                    Cell cell = row_zero.createCell(colNum_row0++);
                    cell.setCellValue(header);
                }
                if (!headersLogMessages.isEmpty()) {
                    Row row_one = sheet_sub.createRow(1);
                    int colNum_row1 = 0;
                    for (String header : headers2) {
                        Cell cell = row_one.createCell(colNum_row1++);
                        cell.setCellValue(header);
                    }
                }
                for(int r = 0; r <mismatched_rows.size(); r++){
                    XSSFRow test_row = null;
                    if(!headersLogMessages.isEmpty()) {
                        test_row = sheet_sub.createRow(r+2);
                    } else {
                        test_row = sheet_sub.createRow(r+1);
                    }
                    for (int el = 0; el < headers2.size(); el++) {
                        Cell cell = test_row.createCell(el);
                        cell.setCellValue(mismatched_rows.get(r).get(headers2.get(el)));
                    }
                    Cell indicate_test_cell = test_row.createCell(headers2.size());
                    indicate_test_cell.setCellValue("test");
                    indicate_test_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));

                }
            }
            ////Print Out xlsx
            if(!headersLogMessages.isEmpty() || !mismatched_rows.isEmpty()) {
                FileOutputStream outputStream = new FileOutputStream(filePath.output_path);
                wb.write(outputStream);
                wb.close();
                System.out.println("output xlsx file was generated");
            }

        }catch (IOException e) {
            throw new ExpectedToFailException(scenarioLevel, e);
        }
    }
}
