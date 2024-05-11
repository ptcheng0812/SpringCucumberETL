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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WriteXLSXStepDef {
    @Autowired
    APIData apiData;

    @Then("I write node data to xlsx file {string}")
    public void iWriteNodeDataToXlsxFile(String arg0) throws IOException {
        if(!arg0.isEmpty()) {
            ArrayNode arrayNode = apiData.dataNode;
            List<String> headers = new ArrayList<>();
            // Get the array of keys
            JsonNode singleNodeH = arrayNode.get(0);
            MethodHelper.JsonNodeExtractKeysRecursive(singleNodeH, headers, "");
            System.out.println("headers" + headers);

            //Write headers in xlsx
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Result1");
            Row row_zero = sheet.createRow(0);
            int colNum = 0;
            for (String header : headers) {
                Cell cell = row_zero.createCell(colNum++);
                cell.setCellValue(header);
            }
            // Write cell based on body
            for (int r=0; r < arrayNode.size(); r++) {
                Row bodyRow = sheet.createRow(r+1);
                JsonNode singleNode = arrayNode.get(r);
                for (int h = 0; h < headers.size(); h++) {
                    JsonNode body = singleNode.get(headers.get(h));
                    Cell bodyCell = bodyRow.createCell(h);
                    if (body == null) {
                        body = MethodHelper.findNodeWithValue(singleNode, headers.get(h));
                        assert body != null;
                        bodyCell.setCellValue(body.asText());
                    }
                    if(body.asText() != null && !body.isObject()) {bodyCell.setCellValue(body.asText());}
                    if(body.isArray()) {
                        String converted = MethodHelper.ConvertArrayNodeToCommaSeparatedString((ArrayNode) body);
                        bodyCell.setCellValue(converted);
                    }
                    if(body.isObject()) {
                        if(body.get("").toString() != null) {
                            bodyCell.setCellValue(body.get("").asText());
                        }
                        else {
                            String converted = MethodHelper.convertNodeToString(body);
                            bodyCell.setCellValue(converted);
                        }
                    }
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
            List<String> headers = new ArrayList<>();
            // Get the array of keys
            JsonNode singleNodeH = arrayNode.get(0);
            MethodHelper.JsonNodeExtractKeysRecursive(singleNodeH, headers, "");
            System.out.println("headers" + headers);

            //Write headers in xlsx
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(arg0);
            Row row_zero = sheet.createRow(0);
            int colNum = 0;
            for (String header : headers) {
                Cell cell = row_zero.createCell(colNum++);
                cell.setCellValue(header);
            }
            // Write cell based on body
            for (int r=0; r < arrayNode.size(); r++) {
                Row bodyRow = sheet.createRow(r+1);
                JsonNode singleNode = arrayNode.get(r);
                for (int h = 0; h < headers.size(); h++) {
                    JsonNode body = singleNode.get(headers.get(h));
                    Cell bodyCell = bodyRow.createCell(h);
                    if (body == null) {
                        body = MethodHelper.findNodeWithValue(singleNode, headers.get(h));
                        assert body != null;
                        bodyCell.setCellValue(body.asText());
                    }
                    if(body.asText() != null && !body.isObject()) {bodyCell.setCellValue(body.asText());}
                    if(body.isArray()) {
                        String converted = MethodHelper.ConvertArrayNodeToCommaSeparatedString((ArrayNode) body);
                        bodyCell.setCellValue(converted);
                    }
                    if(body.isObject()) {
                        if(body.get("").toString() != null) {
                            bodyCell.setCellValue(body.get("").asText());
                        }
                        else {
                            String converted = MethodHelper.convertNodeToString(body);
                            bodyCell.setCellValue(converted);
                        }
                    }
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
