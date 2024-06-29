package etl.stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import etl.data.APIData;
import etl.utilities.MethodHelper;
import io.cucumber.java.en.Then;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class WriteXLSXStepDef {
    @Autowired
    APIData apiData;

    @Then("I write node data to xlsx file {string}")
    public void iWriteNodeDataToXlsxFile(String arg0) throws IOException {
        if(!arg0.isEmpty()) {
            ArrayNode arrayNode = apiData.dataNode;
            List<Map<String, String>> dataList_converted = new ArrayList<>();
            //Convert Array Json to List<Map<String, String>>
            for (int r=0; r < arrayNode.size(); r++) {
                Map<String, String> flatMap = new TreeMap<>();
                MethodHelper.flattenJson(arrayNode.get(r), flatMap, "");
                dataList_converted.add(flatMap) ;
            }
            // Flatten each element in array node and collect all unique keys
            Set<String> uniqueKeys = new TreeSet<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                Map<String, String> flat_Map = new TreeMap<>();
                MethodHelper.flattenJson(arrayNode.get(i), flat_Map, "");
                uniqueKeys.addAll(flat_Map.keySet());
            }
            List<String> uniqueKeysList = new ArrayList<>(uniqueKeys);
            System.out.println("uniqueKeys :" + uniqueKeysList);
            //Write headers in xlsx
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Result1");
            Row row_zero = sheet.createRow(0);
            int colNum = 0;
            for (String header : uniqueKeysList) {
                Cell cell = row_zero.createCell(colNum++);
                cell.setCellValue(header);
            }
            // Write cell based on body
            for (int r=0; r < dataList_converted.size(); r++) {
                Row bodyRow = sheet.createRow(r+1);
                Map<String, String> single_map = dataList_converted.get(r);
                for (int h = 0; h < uniqueKeysList.size(); h++) {
                    String value = single_map.get(uniqueKeysList.get(h));
                    Cell bodyCell = bodyRow.createCell(h);
                    if (value == null || value == "") {
                        bodyCell.setCellValue("null");
                    } else { bodyCell.setCellValue(value); }
                }
            }
            //Print Out xlsx
            FileOutputStream outputStream = new FileOutputStream(arg0);
            workbook.write(outputStream);
            workbook.close();
            System.out.println("xlsx generated");
        } else {
            Assert.fail("Please specify the location of xlsx file to export");
        }

    }

    @Then("I write node data to sheet {string} in xlsx file {string}")
    public void iWriteNodeDataToSheetInXlsxFile(String arg0, String arg1) throws IOException {
        if(!arg0.isEmpty() && !arg1.isEmpty()) {
            ArrayNode arrayNode = apiData.dataNode;
            List<Map<String, String>> dataList_converted = new ArrayList<>();
            //Convert Array Json to List<Map<String, String>>
            for (int r=0; r < arrayNode.size(); r++) {
                Map<String, String> flatMap = new TreeMap<>();
                MethodHelper.flattenJson(arrayNode.get(r), flatMap, "");
                dataList_converted.add(flatMap) ;
            }
            // Flatten each element in array node and collect all unique keys
            Set<String> uniqueKeys = new TreeSet<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                Map<String, String> flat_Map = new TreeMap<>();
                MethodHelper.flattenJson(arrayNode.get(i), flat_Map, "");
                uniqueKeys.addAll(flat_Map.keySet());
            }
            List<String> uniqueKeysList = new ArrayList<>(uniqueKeys);
            System.out.println("uniqueKeys :" + uniqueKeysList);
            //Write headers in xlsx
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(arg0);
            Row row_zero = sheet.createRow(0);
            int colNum = 0;
            for (String header : uniqueKeysList) {
                Cell cell = row_zero.createCell(colNum++);
                cell.setCellValue(header);
            }
            // Write cell based on body
            for (int r=0; r < dataList_converted.size(); r++) {
                Row bodyRow = sheet.createRow(r+1);
                Map<String, String> single_map = dataList_converted.get(r);
                for (int h = 0; h < uniqueKeysList.size(); h++) {
                    String value = single_map.get(uniqueKeysList.get(h));
                    Cell bodyCell = bodyRow.createCell(h);
                    if (value == null || value == "") {
                        bodyCell.setCellValue("null");
                    } else { bodyCell.setCellValue(value); }
                }
            }
            //Print Out xlsx
            FileOutputStream outputStream = new FileOutputStream(arg1);
            workbook.write(outputStream);
            workbook.close();
            System.out.println("xlsx generated");
        } else {
            Assert.fail("Please specify the location of xlsx file and sheet name to export");
        }
    }
}
