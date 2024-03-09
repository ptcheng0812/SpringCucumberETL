package etl.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class MethodHelper {
    public static String ConvertArrayNodeToCommaSeparatedString(ArrayNode arrayNode) {
        // Convert the ArrayNode to a comma-separated string
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayNode.size(); i++) {
            stringBuilder.append(arrayNode.get(i).asText());
            if (i < arrayNode.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public static Map<String, String> convertToDictionary(List<String> columnNames, List<String> rowData) {
        Map<String, String> dictionary = new HashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            dictionary.put(columnNames.get(i), rowData.get(i));
        }
        return dictionary;
    }

    public static String concatenateWithStringJoin(List<String> stringList) {
        return String.join(", ", stringList);
    }

    public static List<String> GetHeaderAndReturnToHeaderList(XSSFSheet spreadsheet) {
        List<String> headers = new ArrayList<>();
        Row headerRow = spreadsheet.getRow(0);
        Iterator<Cell> itr_cells = headerRow.cellIterator();
        while(itr_cells.hasNext()) {
            String header = String.valueOf(itr_cells.next());
            headers.add(header);
        }
        return headers;
    }

    public static List<Row> compareTwoRows(Row row1, Row row2) {
        List<Row>not_matched_rows = new ArrayList<>();
        if((row1 == null) && (row2 == null)) {not_matched_rows.add(null); return not_matched_rows;}
        else if((row1 == null) || (row2 == null)) {not_matched_rows.add(row1); return not_matched_rows;}
        boolean equalRows = true;
        for(int c=0; c <= row1.getLastCellNum(); c++) {
            Cell cell1 = row1.getCell(c);
            Cell cell2 = row2.getCell(c);
            equalRows = compareTwoCells(cell1, cell2);
            if(!equalRows) { not_matched_rows.add(row1);}
        }
        return not_matched_rows;
    }
    public static boolean compareTwoCells(Cell cell1, Cell cell2) {
        boolean equalCells = false;
        if((cell1 == null) && (cell2 == null)) { equalCells =  true;}
        else if((cell1 == null) || (cell2 == null)) { equalCells =  false;}
        else {
            String data1 = cell1.toString();
            String data2 = cell2.toString();
            equalCells = data1.equals(data2);
        }
        return equalCells;
    }

    public static List<CellStyle> MasterAndTestCellStyle(XSSFWorkbook workbook) {
        List<CellStyle> toggleStyle = new ArrayList<>();
        Font font = workbook.createFont();
        CellStyle masterCellStyle = workbook.createCellStyle();
        CellStyle testCellStyle = workbook.createCellStyle();
        font.setBold(true);
        masterCellStyle.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        masterCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
        masterCellStyle.setFont(font);
        testCellStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
        testCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
        testCellStyle.setFont(font);
        toggleStyle.add(masterCellStyle);
        toggleStyle.add(testCellStyle);
        return toggleStyle;
    }

    public static List<Map<String, String>> findNotMatchedMaps(List<Map<String, String>> list1, List<Map<String, String>> list2) {
        List<Map<String, String>> combinedList = new ArrayList<>(list1);
        combinedList.addAll(list2);

        // Group maps by content (ignoring order)
        Map<Map<String, String>, Long> countMap = combinedList.stream()
                .collect(Collectors.groupingBy(m -> m, Collectors.counting()));

        // Filter maps that occurred an odd number of times (not matched)
        List<Map<String, String>> notMatchedMaps = countMap.entrySet().stream()
                .filter(entry -> entry.getValue() % 2 == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return notMatchedMaps;
    }

    public static List<List<String>> removeKeysAndConvert(List<Map<String, String>> list, List<String> keysToRemove) {
        return list.stream()
                .map(map -> map.entrySet().stream()
                        .filter(entry -> !keysToRemove.contains(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }


    public static ArrayNode GetAPIResponseAndTurnIntoNode(String apiEndPoint, String nodeName) {
        Response response = (Response) given().when().get(apiEndPoint).getBody();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        if (response.getContentType().toLowerCase().contains("xml")) {
            ObjectMapper xmlMapper = new XmlMapper();
            try {
                JsonNode rootNode = xmlMapper.readTree(response.asPrettyString());
                if(rootNode.isArray()) {return (ArrayNode) rootNode;}
                else {
                    if (!Objects.equals(nodeName, "")) {arrayNode = (ArrayNode) rootNode.findParent(nodeName).get(nodeName);}
                    else {return arrayNode.add(rootNode);}
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        if (response.getContentType().toLowerCase().contains("json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(response.asPrettyString());
                if(rootNode.isArray()) {return (ArrayNode) rootNode;}
                else {
                    if (!Objects.equals(nodeName, "")) {arrayNode = (ArrayNode) rootNode.findParent(nodeName).get(nodeName);}
                    else {return arrayNode.add(rootNode);}
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return arrayNode;
    }

    public static String convertNodeToString(JsonNode node) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                if (entry.getValue().isObject()) {
                    convertNodeToString(entry.getValue());
                } else {
                    if (entry.getValue().isTextual()) {
                        ((ObjectNode) node).put(entry.getKey(), entry.getValue().asText());
                    }
                }
            });
        }
        return node.toString();
    }

    public static String printMapInGherkinStyle(Map<String, String> map) {
        // Print the table header dynamically
        System.out.print("|");
        map.keySet().forEach(key -> System.out.printf(" %-15s |", key));
        System.out.println(); // Newline after the header

        // Print the data row dynamically
        System.out.print("|");
        map.values().forEach(value -> System.out.printf(" %-15s |", value));
        System.out.println(); // Newline after the data row
        return null;
    }

}
