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

import java.io.*;
import java.util.*;

public class CompareXLSXStepDef {
    @Autowired
    FilePath filePath;
    @Autowired
    SheetData sheetData;
    @Autowired
    ScenarioLevel scenarioLevel;

    @When("I compare master xlsx {string} with test xlsx {string} and output difference file {string}")
    public void iCompareMasterXlsxWithTestXlsxAndOutputDifferenceFile(String arg0, String arg1, String arg2) throws ExpectedToFailException {
        try {
            if(Objects.equals(arg0, "") || Objects.equals(arg1, "") || Objects.equals(arg2, "")) {
                Assert.fail("Missing parameters \n please provide all master/test/output file path");
            } else {
                filePath.setMaster_path(arg0);
                filePath.setTest_path(arg1);
                filePath.setOutput_path(arg2);
            }

            //Read master and test files
            FileInputStream inputStream1 = new FileInputStream(new File(filePath.master_path));
            FileInputStream inputStream2 = new FileInputStream(new File(filePath.test_path));
            XSSFWorkbook workbook1 = new XSSFWorkbook(inputStream1);
            XSSFWorkbook workbook2 = new XSSFWorkbook(inputStream2);
            XSSFSheet spreadsheet1 = workbook1.getSheetAt(0);
            XSSFSheet spreadsheet2 = workbook2.getSheetAt(0);

            //Compare headers
            List<String> headersLogMessages = new ArrayList<>();
            List<String> headers1 = MethodHelper.GetHeaderAndReturnToHeaderList(spreadsheet1);
            List<String> headers2 = MethodHelper.GetHeaderAndReturnToHeaderList(spreadsheet2);
            for (int h=0; h < headers1.size(); h++) {
                if(!headers1.get(h).equals(headers2.get(h))) {
                    String headersMessage = headers1.get(h);
                    headersLogMessages.add(headersMessage);
                }
            }

            //Compare cells in rows
            List<Row> mismatched_row1 = new ArrayList<>();
            List<Row> mismatched_row2 = new ArrayList<>();
            for (int r=1; r<= spreadsheet1.getLastRowNum(); r++) {
                Row datarow1 = spreadsheet1.getRow(r);
                Row datarow2 = spreadsheet2.getRow(r);
                if(MethodHelper.compareTwoRows(datarow1, datarow2).isEmpty()) {System.out.println("rows and cells are equal: " + datarow1.getRowNum());}
                else {
    //                System.out.println("rows and cells are NOT equal:: " + datarow1.getRowNum());
                    mismatched_row1.add(datarow1);
                    mismatched_row2.add(datarow2);
                }
            }

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


            //Write different rows/cell
            XSSFSheet sheet_sub = wb.createSheet("Compare Difference");
            CellCopyPolicy policy = new CellCopyPolicy();
            policy.isCopyCellValue();
            if (!mismatched_row1.isEmpty()) {
                Row row_zero = sheet_sub.createRow(0);
                int colNum_row0 = 0;
                for (String header : headers1) {
                    Cell cell = row_zero.createCell(colNum_row0++);
                    cell.setCellValue(header);
                }
                for(int r = 0; r <mismatched_row1.size(); r++){
                    XSSFRow master_row = sheet_sub.createRow(r*2+1);
                    XSSFRow test_row = sheet_sub.createRow(r*2 +2);
                    master_row.copyRowFrom(mismatched_row1.get(r),policy);
                    test_row.copyRowFrom(mismatched_row2.get(r),policy);
                    Cell indicate_master_cell = master_row.createCell(headers1.size());
                    indicate_master_cell.setCellValue("master");
                    indicate_master_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(0));
                    Cell indicate_test_cell = test_row.createCell(headers2.size());
                    indicate_test_cell.setCellValue("test");
                    indicate_test_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));
                }
            }

            ////Print Out xlsx
            if(!headersLogMessages.isEmpty() || !mismatched_row1.isEmpty()) {
                FileOutputStream outputStream = new FileOutputStream(filePath.output_path);
                wb.write(outputStream);
                wb.close();
                System.out.println("output xlsx file was generated");
            }

        }catch (IOException e) {
            throw new ExpectedToFailException(scenarioLevel, e);
        }
    }

    @When("I compare sheet {string} in master xlsx {string} with sheet {string} in test xlsx {string} and output difference file {string}")
    public void iCompareSheetInMasterXlsxWithSheetInTestXlsxAndOutputDifferenceFile(String arg0, String arg1, String arg2, String arg3, String arg4) throws IOException {
        if(Objects.equals(arg1, "") || Objects.equals(arg3, "") || Objects.equals(arg4, "")) {
            Assert.fail("Missing parameters \n please provide all master/test/output file path");
        } else {
            filePath.setMaster_path(arg1);
            filePath.setTest_path(arg3);
            filePath.setOutput_path(arg4);
        }

        if(Objects.equals(arg0, "") || Objects.equals(arg2, "")) {
            Assert.fail("Missing parameters \n please provide specific sheet name");
        }

        //Read master and test files
        FileInputStream inputStream1 = new FileInputStream(new File(filePath.master_path));
        FileInputStream inputStream2 = new FileInputStream(new File(filePath.test_path));
        XSSFWorkbook workbook1 = new XSSFWorkbook(inputStream1);
        XSSFWorkbook workbook2 = new XSSFWorkbook(inputStream2);
        XSSFSheet spreadsheet1 = workbook1.getSheet(arg0);
        XSSFSheet spreadsheet2 = workbook2.getSheet(arg2);

        //Compare headers
        List<String> headersLogMessages = new ArrayList<>();
        List<String> headers1 = MethodHelper.GetHeaderAndReturnToHeaderList(spreadsheet1);
        List<String> headers2 = MethodHelper.GetHeaderAndReturnToHeaderList(spreadsheet2);
        for (int h=0; h < headers1.size(); h++) {
            if(!headers1.get(h).equals(headers2.get(h))) {
                String headersMessage = headers1.get(h);
                headersLogMessages.add(headersMessage);
            }
        }

        //Compare cells in rows
        List<Row> mismatched_row1 = new ArrayList<>();
        List<Row> mismatched_row2 = new ArrayList<>();
        for (int r=1; r<= spreadsheet1.getLastRowNum(); r++) {
            Row datarow1 = spreadsheet1.getRow(r);
            Row datarow2 = spreadsheet2.getRow(r);
            if(MethodHelper.compareTwoRows(datarow1, datarow2).isEmpty()) {System.out.println("rows and cells are equal: " + datarow1.getRowNum());}
            else {
//                System.out.println("rows and cells are NOT equal:: " + datarow1.getRowNum());
                mismatched_row1.add(datarow1);
                mismatched_row2.add(datarow2);
            }
        }

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


        //Write different rows/cell
        XSSFSheet sheet_sub = wb.createSheet("Compare Difference");
        CellCopyPolicy policy = new CellCopyPolicy();
        policy.isCopyCellValue();
        if (!mismatched_row1.isEmpty()) {
            Row row_zero = sheet_sub.createRow(0);
            int colNum_row0 = 0;
            for (String header : headers1) {
                Cell cell = row_zero.createCell(colNum_row0++);
                cell.setCellValue(header);
            }
            for(int r = 0; r <mismatched_row1.size(); r++){
                XSSFRow master_row = sheet_sub.createRow(r*2+1);
                XSSFRow test_row = sheet_sub.createRow(r*2 +2);
                master_row.copyRowFrom(mismatched_row1.get(r),policy);
                test_row.copyRowFrom(mismatched_row2.get(r),policy);
                Cell indicate_master_cell = master_row.createCell(headers1.size());
                indicate_master_cell.setCellValue("master");
                indicate_master_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(0));
                Cell indicate_test_cell = test_row.createCell(headers2.size());
                indicate_test_cell.setCellValue("test");
                indicate_test_cell.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));
            }
        }

        ////Print Out xlsx
        if(!headersLogMessages.isEmpty() || !mismatched_row1.isEmpty()) {
            FileOutputStream outputStream = new FileOutputStream(filePath.output_path);
            wb.write(outputStream);
            wb.close();
            System.out.println("output xlsx file was generated");
        }
    }

    @When("I compare the master with test with primary key {string} and output the difference file {string}")
    public void iCompareTheMasterWithTestAndOutputTheDifference(String arg0, String arg1) throws IOException {
        List<Map<String, String>> notMatchedMaps_master = new ArrayList<>();
        List<Map<String, String>> notMatchedMaps_test = new ArrayList<>();
        List<String> headersLogMessages = new ArrayList<>();
        List<String> keysToRemove_master = new ArrayList<>(sheetData.master_dataList.get(0).keySet());
        List<String> keysToRemove_test = new ArrayList<>(sheetData.master_dataList.get(0).keySet());
        List<List<String>> withoutfields_master_dataList = MethodHelper.removeKeysAndConvert(sheetData.master_dataList, keysToRemove_master);
        List<List<String>> withoutfields_test_dataList = MethodHelper.removeKeysAndConvert(sheetData.test_dataList, keysToRemove_test);

        // Sort the master data and test data by the "name" key
        Collections.sort(sheetData.master_dataList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
                return map1.get(arg0).compareTo(map2.get(arg0));
            }
        });
        Collections.sort(sheetData.test_dataList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
                return map1.get(arg0).compareTo(map2.get(arg0));
            }
        });

        try {
            //Compare headers
            for (int h=0; h < sheetData.master_headerList.size(); h++) {
                if(!sheetData.master_headerList.get(h).equals(sheetData.test_headerList.get(h))) {
                    String headersMessage = sheetData.master_headerList.get(h);
                    headersLogMessages.add(headersMessage);
                }
            }

            //Compare master and test data list
            for (int el=0; el< sheetData.master_dataList.size(); el++) {
                for(String key: sheetData.master_dataList.get(el).keySet()) {
                    if (!Objects.equals(sheetData.master_dataList.get(el).get(key), sheetData.test_dataList.get(el).get(key))) {
                        if(sheetData.master_dataList.get(el).get(key)!=null && sheetData.test_dataList.get(el).get(key)!=null) {
                            notMatchedMaps_master.add(sheetData.master_dataList.get(el));
                            break;
                        }
                    }

                }
            }

            for (int el=0; el< sheetData.test_dataList.size(); el++) {
                for(String key: sheetData.test_dataList.get(el).keySet()) {
                    if (!Objects.equals(sheetData.test_dataList.get(el).get(key), sheetData.master_dataList.get(el).get(key))) {
                        if(sheetData.master_dataList.get(el).get(key)!=null && sheetData.test_dataList.get(el).get(key)!=null) {
                            notMatchedMaps_test.add(sheetData.test_dataList.get(el));
                            break;
                        }
                    }
                }
            }
        } catch (Exception exception) {if (exception.toString().contains("out of bounds")) { System.out.println("Row / Column counts from 2 files are not match");}}


        System.out.println("Not matched from MASTER: " + notMatchedMaps_master);
        System.out.println("Not matched headers: "+ headersLogMessages);

        //Prepare New Workbook to write difference in rows/cells and headers
        XSSFWorkbook wb = new XSSFWorkbook();

        //Write different headers in xlsx
        if(!headersLogMessages.isEmpty()) {
            XSSFSheet sheet = wb.createSheet("Headers Difference Explicit Mode");
            Row row_zero = sheet.createRow(0);
            Row row_one = sheet.createRow(1);
            Row row_two = sheet.createRow(2);
            Cell cell_zero = row_zero.createCell(0);
            cell_zero.setCellValue("Difference at index: " + MethodHelper.concatenateWithStringJoin(headersLogMessages));
            Cell cell_0_row_one = row_one.createCell(0);
            cell_0_row_one.setCellValue("master");
            cell_0_row_one.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(0));
            int colNum_row1 = 1;
            for (String header : sheetData.master_headerList) {
                Cell cell = row_one.createCell(colNum_row1++);
                cell.setCellValue(header);
            }
            Cell cell_0_row_two = row_two.createCell(0);
            cell_0_row_two.setCellValue("test");
            cell_0_row_two.setCellStyle(MethodHelper.MasterAndTestCellStyle(wb).get(1));
            int colNum_row2 = 1;
            for (String header : sheetData.test_headerList) {
                Cell cell = row_two.createCell(colNum_row2++);
                cell.setCellValue(header);
            }
        }

        //Write different rows/cell
        XSSFSheet sheet_sub = wb.createSheet("Compare Difference Explicit Mode");
        CellCopyPolicy policy = new CellCopyPolicy();
        policy.isCopyCellValue();
        if (!sheetData.master_dataList.isEmpty()) {
            Row row_zero = sheet_sub.createRow(0);
            int colNum_row0 = 0;
            for (String header : sheetData.master_headerList) {
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
                for (int el = 0; el < sheetData.master_headerList.size(); el++) {
                    Cell cell = master_row.createCell(el);
                    cell.setCellValue(notMatchedMaps_master.get(r).get(sheetData.master_headerList.get(el)));
                }
                for (int el = 0; el < sheetData.test_headerList.size(); el++) {
                    Cell cell = test_row.createCell(el);
                    cell.setCellValue(notMatchedMaps_test.get(r).get(sheetData.test_headerList.get(el)));
                }

                Cell indicate_master_cell = master_row.createCell(sheetData.master_headerList.size());
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
        }

    }



}
