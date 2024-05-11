package etl.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.sql.*;
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

    public static JsonNode findNodeWithValue(JsonNode node, String value) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            JsonNode fieldNode = entry.getValue();
            if (fieldNode.isObject()) {
                JsonNode foundNode = findNodeWithValue(fieldNode, value);
                if (foundNode != null) {
                    return foundNode;
                }
            } else if (entry.getKey().equals(value)) {
                return fieldNode;
            }
        }
        return null;
    }
    public static void JsonNodeExtractKeysRecursive(JsonNode jsonNode, List<String> pre_headers, String parentKey) {
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldValue = jsonNode.get(fieldName);
            if (fieldValue.isObject()) {
                // If the value is a JSON object, recursively call extractKeysRecursive
                JsonNodeExtractKeysRecursive(fieldValue, pre_headers, fieldName);
                parentKey = fieldName;
            } else {
                // Add the key to headers with the appropriate prefix
                if(fieldName != "") {pre_headers.add(fieldName);}
                else { if (parentKey != null) {pre_headers.add(parentKey);} }
            }
        }
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

    /********************Database Helper*************************/
    public static String generateCreateTableSql(String tableName, JsonNode node) {
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        boolean lastCol = false;

        // Check if the JSON node is null or empty
        if (node == null || node.isNull() || node.isEmpty()) {
            throw new IllegalArgumentException("JSON node is null or empty");
        }
        // Iterate through the fields of the first JSON object to determine the column names and data types

        StringBuilder finalSql = sql;
        node.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();
            String fieldType = "VARCHAR(400)"; // Adjust data types as needed
            if(Objects.equals(fieldName, "id") || Objects.equals(fieldName, "num")) {
                fieldType = "NUMERIC";
                finalSql.append(fieldName).append(" ").append(fieldType).append(" NOT NULL, ");
            } else{
                finalSql.append(fieldName).append(" ").append(fieldType).append(", ");
            }
        });

        sql = new StringBuilder(sql.substring(0, sql.length() - 2)); // Remove the last two characters

        sql.append(")");

        return sql.toString();
    }

    public static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = '" + tableName + "')")) {
            resultSet.next();
            return resultSet.getBoolean(1);
        }
    }

    public static void dropTable(Connection connection, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE " + tableName);
        }
    }

    public static void insertDataToDatabase(Connection connection, JsonNode node, String nodeName) throws SQLException {
        StringBuilder insertSql = new StringBuilder("INSERT INTO " + nodeName + " (");
        StringBuilder valuesSql = new StringBuilder(") VALUES (");

        // Iterate over the fields of the JSON object to dynamically construct the SQL statement
        boolean firstField = true;
        StringBuilder finalInsertSql = insertSql;
        StringBuilder finalValuesSql = valuesSql;
        node.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();
            String fieldValueStringtified = fieldValue.asText();
            finalInsertSql.append(fieldName).append(", ");
            if(Objects.equals(fieldName, "id")) { finalValuesSql.append(fieldValueStringtified).append(", "); }
            else if(fieldValue.isArray()) { finalValuesSql.append("'").append(ConvertArrayNodeToCommaSeparatedString((ArrayNode) fieldValue)).append("'").append(", "); }
            else if(fieldValue.isObject()) { finalValuesSql.append("'").append(fieldValue.get("").asText().replace("'", "''")).append("'").append(", ");}
            else { finalValuesSql.append("'").append(fieldValueStringtified.replace("'", "''")).append("'").append(", ");}
        });
        insertSql = new StringBuilder(insertSql.substring(0, insertSql.length() - 2));
        valuesSql = new StringBuilder(valuesSql.substring(0, valuesSql.length() - 2));
        insertSql.append(valuesSql).append(")");

        try (PreparedStatement statement = connection.prepareStatement(insertSql.toString())) {
          statement.executeUpdate();
        }
    }

    public static void queryDataInDatabase(Connection connection, Map<String, String> singleData, String table) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM " + table + " WHERE ");

        for (Map.Entry<String, String> entry : singleData.entrySet()) {
            query.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" AND ");
        }
        // Remove the last "AND"
        query.delete(query.length() - 5, query.length());

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Assert that at least one result is returned
            if (!resultSet.next())  {
                System.out.print("Expected but not found in query: ");
                System.out.print("|");
                for(String key : singleData.keySet()) {
                   System.out.printf(" %-15s |", singleData.get(key));

                }
                System.out.println();
            }
        }
    }

    public static boolean queryDataInDatabaseAndReturnMismatched(Connection connection, Map<String, String> singleData, String table) throws SQLException {
        boolean matched = false;
        StringBuilder query = new StringBuilder("SELECT * FROM " + table + " WHERE ");

        for (Map.Entry<String, String> entry : singleData.entrySet()) {
            String entryValue = entry.getValue();
            if (entryValue.endsWith(".0")) { entryValue = entryValue.substring(0, entryValue.length() - 2);}
            query.append(entry.getKey()).append(" = ").append("'").append(entryValue.replace("'", "''")).append("'").append(" AND ");
        }
        // Remove the last "AND"
        query.delete(query.length() - 5, query.length());

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Assert that at least one result is returned
            if (resultSet.next()) {
                return matched = true;
            } else {
                return matched = false;
            }
        }
    }

    public static void queryDataInDatabaseUsingEachKey(Connection connection, Map<String, String> singleData, String table) throws SQLException {
        List<String> notMatchedKeys = new ArrayList<>();
        for(String key : singleData.keySet()) {
            StringBuilder query = new StringBuilder("SELECT * FROM " + table + " WHERE ");
            query.append(key).append(" = ").append("'").append(singleData.get(key)).append("'");

            try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
                // Execute the query
                ResultSet resultSet = statement.executeQuery();
                // Assert that at least one result is returned
                if (!resultSet.next()) {
                    System.out.println("At least one result found.");
                    notMatchedKeys.add(key);
                }
            }
        }

        if(!notMatchedKeys.isEmpty()) {
            System.out.print("Expected but not found in query: ");
            System.out.print("|");
            for(String key : singleData.keySet()) {
                System.out.printf(" %-15s |", singleData.get(key));
            }
            System.out.println();
            System.out.print("Not matched key and value: ");
            for(String notMatchedKey : notMatchedKeys) {
                System.out.printf(notMatchedKey + " : " + singleData.get(notMatchedKey) + ", ");
            }
            System.out.println();
        }

    }

    public static List<String> getDBTableHeaders(Connection connection, String tableName) {
        List<String> headers = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE 1 = 0")) {
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                headers.add(columnName.toLowerCase());
            }
        }
         catch (SQLException e) {
            e.printStackTrace();
        }

        return headers;
    }

    public static List<Map<String, String>> getAllDataFromTable(Connection connection, String tableName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName)) {
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i).toLowerCase();
                    String columnValue = resultSet.getString(i);
                    row.put(columnName, columnValue);
                }
                dataList.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static String buildQueryFromTable(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists();
        StringBuilder queryBuilder = new StringBuilder();
        // Iterate over each row in the table
        for (List<String> row : rows) {
            // Ensure the row has at least one cell
            if (!row.isEmpty()) {
                // Get the first cell value as SQL query
                String sqlQuery = row.get(0);
                // Append the SQL query to the query builder
                queryBuilder.append(sqlQuery).append(" ");
            }
        }
        // Convert the query builder to a string and return
        return queryBuilder.toString().trim();
    }

    public static List<Map<String, String>> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<Map<String, String>> dataList = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnValue = resultSet.getString(i);
                row.put(columnName, columnValue);
            }
            dataList.add(row);
        }

        return dataList;
    }

}
