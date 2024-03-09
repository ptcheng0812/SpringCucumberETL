package etl.stepdefs;

import etl.data.SheetData;
import etl.utilities.MethodHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class TabularCompareStepDef {
    @Autowired
    SheetData sheetData;

    private void internalLoggingGherkinTableFromMap(int index, String key, Map<String, String> target_master_map, List<Map<String, String>> tabularData) {
        System.out.print("Expected: ");System.out.println();
        System.out.print("|"); System.out.printf(" %-15s |", key);
        System.out.println();
        System.out.print("|"); System.out.printf(" %-15s |", target_master_map.get(key));
        System.out.println();
        System.out.println("Actual: ");
        System.out.print("|"); System.out.printf(" %-15s |", key);
        System.out.println();
        System.out.print("|"); System.out.printf(" %-15s |", tabularData.get(index).get(key));
        System.out.println("");
    }

    @When("I compare the following tabular data to sheet {string} in xlsx file {string}")
    public void iCompareTheFollowingTabularDataToSheetInXlsxFile(String arg0, String arg1, DataTable table) throws IOException {
        //Tabular data
        List<Map<String, String>> tabularData = table.asMaps();
//        System.out.println("tabularData: " + tabularData);


        //Master map data list
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
//        System.out.println("headers: "+ sheetData.master_headerList);

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
//        System.out.println("Master Dict List: " + sheetData.master_dataList);
//        System.out.println("Master Dict List Size: " + sheetData.master_dataList.size());
//        System.out.println("Master Dict List and Header List captured");

         //Check headers provided from gherkin table not null
        for (Map<String, String> el : tabularData) {
            Set<String> keys = el.keySet();
            for (String key: keys) {
                if (key == null) {
                    Assert.fail("Missing headers/key field. Please check the header row");
                }
            }
        }

        //Compare tabular data's headers to master data's headers
        for (Map<String, String> el : tabularData) {
            Set<String> keys = el.keySet();
            for (String key: keys) {
                if (!headers.contains(key)) {
                    Assert.fail("Headers/Key field not matched. Please check the header row. Not matched header/key: " + key);
                }
            }
        }

        //Compare tabular data to master data
        List<Map<String, String>> notMatchedMaps = new ArrayList<>();
        System.out.println("Not matched rows: \n");
        for(int m =0; m < tabularData.size(); m++) {
            if (!sheetData.master_dataList.contains(tabularData.get(m))) {
                notMatchedMaps.add(tabularData.get(m));
                System.out.println(table.rows(m + 1, m + 2));
            }
        }

        if(notMatchedMaps.isEmpty()) { System.out.println("All comparison completed with no difference.");}

    }

    @When("I compare the following tabular data to sheet {string} in xlsx file {string} with unique key {string}")
    public void iCompareTheFollowingTabularDataToSheetInXlsxFileWithUniqueKey(String arg0, String arg1, String arg2, DataTable table) throws IOException {
        //Tabular data
        List<Map<String, String>> tabularData = table.asMaps();
//        System.out.println("tabularData: " + tabularData);


        //Master map data list
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
//        System.out.println("headers: "+ sheetData.master_headerList);

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
//        System.out.println("Master Dict List: " + sheetData.master_dataList);
//        System.out.println("Master Dict List Size: " + sheetData.master_dataList.size());
//        System.out.println("Master Dict List and Header List captured");

        //Check unique key provided
        if(Objects.equals(arg2, "")) { Assert.fail("Unique key needs to be provided for comparison");}

        //Check headers provided from gherkin table not null
        for (Map<String, String> el : tabularData) {
            Set<String> keys = el.keySet();
            for (String key: keys) {
                if (key == null) {
                    Assert.fail("Missing headers/key field. Please check the header row");
                }
            }
        }

        //Compare tabular data's headers to master data's headers
        for (Map<String, String> el : tabularData) {
            Set<String> keys = el.keySet();
            for (String key: keys) {
                if (!headers.contains(key)) {
                    Assert.fail("Headers/Key field not matched. Please check the header row. Not matched header/key: " + key);
                }
            }
        }

        //Compare tabular data to master data
        List<Map<String, String>> notMatchedMaps = new ArrayList<>();
        for(int m =0; m < tabularData.size(); m++) {
            int finalM = m;
            Map<String, String> target_master_map = sheetData.master_dataList.stream().filter(map -> tabularData.get(finalM).get(arg2).equals(map.get(arg2))).findFirst().orElse(null);
//            System.out.println("target_master_map: "+ target_master_map);
            for(String key: tabularData.get(m).keySet()) {
                if (!Objects.equals(tabularData.get(m).get(key), target_master_map.get(key))) {
                    notMatchedMaps.add(tabularData.get(m));
                    System.out.print("\nNot matched rows:" + table.rows(m + 1, m + 2));
                    internalLoggingGherkinTableFromMap(m, key, target_master_map, tabularData);
                    break;
                }
            }
        }

        if(notMatchedMaps.isEmpty()) { System.out.println("All comparison completed with no difference.");}
//        else {System.out.println("Not Matched Maps: " + notMatchedMaps);}
    }
}
