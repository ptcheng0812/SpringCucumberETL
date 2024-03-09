package etl.stepdefs;

import etl.data.FilePath;
import etl.data.ScenarioLevel;
import etl.data.SheetData;
import etl.utilities.ExpectedToFailException;
import etl.utilities.MethodHelper;
import io.cucumber.java.PendingException;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assume;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

public class ReadXLSXStepDef {
    @Autowired
    FilePath filePath;

    @Autowired
    SheetData sheetData;
    @Autowired
    ScenarioLevel scenarioLevel;

    @Given("the xlsx file {string} exists")
    public void theXlsxFileExists(String arg0) {
        filePath.setPath(arg0);
        Path path = FileSystems.getDefault().getPath(filePath.path);
        if(filePath.path.isEmpty()) {
            if(scenarioLevel.ForcePass) {
                System.out.println("Expected Invalid file path: empty string");
                Assume.assumeTrue(false);
            }
            else {fail("Invalid file path: empty string"); }
        }
        else {
            if(Files.exists(path)) {System.out.println(filePath.path + " exists");}
            else {fail(filePath.path + " not exists");}
        }
    }

    @Given("the xlsx has data and is not an empty xlsx")
    public void theXlsxHasDataAndIsNotAnEmptyXlsx() {
        int num;
        try (FileInputStream inputStream1 = new FileInputStream(new File(filePath.path))) {
            num = inputStream1.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (num == 0) {fail("Empty XLSX with no data");}
        else {System.out.println(filePath.path + "XLSX exists with data");}
//        System.out.println(filePath.path + "XLSX exists with data");
    }

    @When("I retrieve data from master file {string}")
    public void iRetrieveDataFromMasterFile(String arg0) throws ExpectedToFailException {
        try{
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
                sheetData.setMaster_headerList(header);
            }
            System.out.println("headers: "+ sheetData.master_headerList);

            //Get rows and its data, then add to row data list, return list of dictionary
            for (int r=1; r<= spreadsheet.getLastRowNum(); r++) {
                List<String> rowData = new ArrayList<>();
                Row datarow = spreadsheet.getRow(r);
                Iterator < Cell >  itr_data_cells = datarow.cellIterator();
                while ( itr_data_cells.hasNext()) {
                    String data = String.valueOf(itr_data_cells.next());
                    rowData.add(data);
                }
                Map<String, String> singleData = MethodHelper.convertToDictionary(headers, rowData);
                dictionaryList.add(singleData);
                sheetData.setMaster_dataList(singleData);
            }
            System.out.println("Master Dict List: " + sheetData.master_dataList);
            System.out.println("Master Dict List Size: " + sheetData.master_dataList.size());
            System.out.println("Master Dict List and Header List captured");
        } catch (IOException e) {
            throw new ExpectedToFailException(scenarioLevel, e);
        }

    }

    @When("I retrieve data from test file {string}")
    public void iRetrieveDataFromTestFile(String arg0) throws ExpectedToFailException {
        try{
            FileInputStream inputStream = new FileInputStream(new File(arg0));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);
            List<Map<String, String>> dictionaryList = new ArrayList<>();
            //Get headers, then add to header list
            List<String> headers = new ArrayList<>();
            Row headerRow = spreadsheet.getRow(0);
            Iterator < Cell >  itr_cells = headerRow.cellIterator();
            while(itr_cells.hasNext()) {
                String header = String.valueOf(itr_cells.next());
                headers.add(header);
                sheetData.setTest_headerList(header);
            }
            System.out.println("headers: "+ sheetData.test_headerList);

            //Get rows and its data, then add to row data list, return list of dictionary
            for (int r=1; r<= spreadsheet.getLastRowNum(); r++) {
                List<String> rowData = new ArrayList<>();
                Row datarow = spreadsheet.getRow(r);
                Iterator < Cell >  itr_data_cells = datarow.cellIterator();
                while ( itr_data_cells.hasNext()) {
                    String data = String.valueOf(itr_data_cells.next());
                    rowData.add(data);
                }
                Map<String, String> singleData = MethodHelper.convertToDictionary(headers, rowData);
                dictionaryList.add(singleData);
                sheetData.setTest_dataList(singleData);
            }
            System.out.println("Test Dict List: " + sheetData.test_dataList);
            System.out.println("Test Dict List Size: " + sheetData.test_dataList.size());
            System.out.println("Test Dict List and Header List captured");

        }catch (IOException e) {
            throw new ExpectedToFailException(scenarioLevel, e);
        }
    }

    @When("I retrieve data from sheet {string} in master file {string}")
    public void iRetrieveDataFromSheetInMasterFile(String arg0, String arg1) throws ExpectedToFailException {
        try {
            FileInputStream inputStream = new FileInputStream(new File(arg1));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheet(arg0);
            List<Map<String, String>> dictionaryList = new ArrayList<>();
            //Get headers, then add to header list
            List<String> headers = new ArrayList<>();
            Row headerRow = spreadsheet.getRow(0);
            Iterator<Cell> itr_cells = headerRow.cellIterator();
            while(itr_cells.hasNext()) {
                String header = String.valueOf(itr_cells.next());
                headers.add(header);
                sheetData.setMaster_headerList(header);
            }
            System.out.println("headers: "+ sheetData.master_headerList);

            //Get rows and its data, then add to row data list, return list of dictionary
            for (int r=1; r<= spreadsheet.getLastRowNum(); r++) {
                List<String> rowData = new ArrayList<>();
                Row datarow = spreadsheet.getRow(r);
                Iterator < Cell >  itr_data_cells = datarow.cellIterator();
                while ( itr_data_cells.hasNext()) {
                    String data = String.valueOf(itr_data_cells.next());
                    rowData.add(data);
                }
                Map<String, String> singleData = MethodHelper.convertToDictionary(headers, rowData);
                dictionaryList.add(singleData);
                sheetData.setMaster_dataList(singleData);
            }
        }catch (IOException e) {
            throw new ExpectedToFailException(scenarioLevel, e);
        }
    }

    @When("I retrieve data from sheet {string} in test file {string}")
    public void iRetrieveDataFromSheetInTestFile(String arg0, String arg1) throws ExpectedToFailException {
        try{
            FileInputStream inputStream = new FileInputStream(new File(arg1));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheet(arg0);
            List<Map<String, String>> dictionaryList = new ArrayList<>();
            //Get headers, then add to header list
            List<String> headers = new ArrayList<>();
            Row headerRow = spreadsheet.getRow(0);
            Iterator < Cell >  itr_cells = headerRow.cellIterator();
            while(itr_cells.hasNext()) {
                String header = String.valueOf(itr_cells.next());
                headers.add(header);
                sheetData.setTest_headerList(header);
            }
            System.out.println("headers: "+ sheetData.test_headerList);

            //Get rows and its data, then add to row data list, return list of dictionary
            for (int r=1; r<= spreadsheet.getLastRowNum(); r++) {
                List<String> rowData = new ArrayList<>();
                Row datarow = spreadsheet.getRow(r);
                Iterator < Cell >  itr_data_cells = datarow.cellIterator();
                while ( itr_data_cells.hasNext()) {
                    String data = String.valueOf(itr_data_cells.next());
                    rowData.add(data);
                }
                Map<String, String> singleData = MethodHelper.convertToDictionary(headers, rowData);
                dictionaryList.add(singleData);
                sheetData.setTest_dataList(singleData);
            }
        }catch (IOException e) {
            throw new ExpectedToFailException(scenarioLevel, e);
        }
    }


}
